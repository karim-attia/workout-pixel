package com.karim.workoutpixel.Core;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.karim.workoutpixel.Database.Goal;
import com.karim.workoutpixel.Main.InteractWithGoalInDb;
import com.karim.workoutpixel.R;

import java.util.List;

import static com.karim.workoutpixel.Core.CommonFunctions.STATUS_NONE;
import static com.karim.workoutpixel.Core.CommonFunctions.dateBeautiful;
import static com.karim.workoutpixel.Core.CommonFunctions.getDrawableIntFromStatus;
import static com.karim.workoutpixel.Core.CommonFunctions.timeBeautiful;

/**
 * The configuration screen for the {@link WorkoutPixelAppWidgetProvider WidgetFunctions} AppWidget.
 */
public class ConfigureActivity extends AppCompatActivity {
    private static final String TAG = "WORKOUT_PIXEL CONFIGURE ACTIVITY";
    final Context context = ConfigureActivity.this;

    boolean isFirstConfigure = true;

    TextView goalIntervalTextView;
    TextView goalIntervalPluralTextView;
    int intervalInDays = 2;

    EditText widgetTitle;
    CheckBox showDateCheckbox;
    CheckBox showTimeCheckbox;

    Goal goal = new Goal(AppWidgetManager.INVALID_APPWIDGET_ID, "", 0, intervalInDays, 2, false, false, STATUS_NONE);

    public ConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "ON_CREATE");
        super.onCreate(savedInstanceState);

        // Find the widget id  and whether it is a reconfigure activity from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        // Check form the intent whether this goal gets configured for the first time or gets reconfigured.
        if(intent.getAction() != null && intent.getAction().equals("APPWIDGET_RECONFIGURE")) {
            isFirstConfigure = false;
        }
        else {
            isFirstConfigure = true;
            Log.d(TAG, "No action set");
        }
        Log.d(TAG, "Is it a first configure activity? " + isFirstConfigure);

        // Get the AppWidgetId from the launcher if there is one provided. Else exit.
        if (isFirstConfigure) {
            // Set the result to CANCELED. This will cause the widget host to cancel out of the widget placement if the user presses the back button.
            setResult(RESULT_CANCELED);

            if (extras != null) {
                goal.setAppWidgetId(extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID));
            } else {Log.d(TAG, "extras = null");}

            // If this activity was started with an intent without an app widget ID, finish with an error.
            if (goal.getAppWidgetId() == null || goal.getAppWidgetId() == AppWidgetManager.INVALID_APPWIDGET_ID) {
                Log.d(TAG, "AppWidgetId is invalid.");
                finishAndRemoveTask();
                return;
            }

            // Disable the back button in the app bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // Get the Uid of the goal that should be configured
        if (!isFirstConfigure) {
            if (extras != null) {
                goal.setUid(extras.getInt("widgetUid", 0));
            } else {Log.d(TAG, "extras = null");}

            if (goal.getUid() == 0) {
                Log.d(TAG, "widgetUid is invalid.");
                finishAndRemoveTask();
                return;
            }
            // Change the activity title in the app bar
            setTitle(R.string.ReconfigureWidgetActivityLabel);
        }

        setContentView(R.layout.configuration);

        // Bind views
        TextView introText = findViewById(R.id.configure_activity_intro_text);
        widgetTitle = findViewById(R.id.appwidget_text);
        showDateCheckbox = findViewById(R.id.showDateCheckbox);
        showTimeCheckbox = findViewById(R.id.showTimeCheckbox);
        Button minusButtonInterval = findViewById(R.id.minus);
        goalIntervalTextView = findViewById(R.id.goal_interval);
        Button plusButtonInterval = findViewById(R.id.plus);
        goalIntervalPluralTextView = findViewById(R.id.plural);
        final TextView preview = findViewById(R.id.widget_preview);
        Button addAndUpdateButton = findViewById(R.id.add_button);

        // Load and pre-fill existing configurations.
        if (!isFirstConfigure) {
            // Don't show the initial text if the user edits the widget.
            introText.setVisibility(View.GONE);
            goal = InteractWithGoalInDb.loadGoalByUid(context, goal.getUid());
            widgetTitle.setText(goal.getTitle());
            intervalInDays = goal.getIntervalBlue();
            showDateCheckbox.setChecked(goal.getShowDate());
            showTimeCheckbox.setChecked(goal.getShowTime());

            addAndUpdateButton.setText(R.string.add_widget_reconfigure);
        }

        goalIntervalTextView.setText(String.valueOf(intervalInDays));

        // Make plus and minus button work
        minusButtonInterval.setOnClickListener(v -> {
            if (intervalInDays > 1) {intervalInDays--;}
            goalIntervalTextView.setText(String.valueOf(intervalInDays));
            if (intervalInDays < 2) {goalIntervalPluralTextView.setVisibility(View.GONE);}
        });

        plusButtonInterval.setOnClickListener(v -> {
            if (intervalInDays < 366) {intervalInDays++;}
            goalIntervalTextView.setText(String.valueOf(intervalInDays));
            if (intervalInDays > 1) {goalIntervalPluralTextView.setVisibility(View.VISIBLE);}
        });

        // Preview
        if (isFirstConfigure) preview.setBackgroundResource(R.drawable.rounded_corner_green);
        else preview.setBackgroundResource(getDrawableIntFromStatus(goal.getStatus()));

        setPreview(preview);
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> setPreview(preview);
        showDateCheckbox.setOnCheckedChangeListener(onCheckedChangeListener);
        showTimeCheckbox.setOnCheckedChangeListener(onCheckedChangeListener);

        widgetTitle.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setPreview(preview);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 'Update widget' button
        addAndUpdateButton.setOnClickListener(updateWidgetOnClickListener);

        // Setup reconnect widget card
        if (isFirstConfigure) {
            CommonFunctions.executorService.execute(() -> {
                List<Goal> widgetsWithoutValidAppwidgetId = InteractWithGoalInDb.loadWidgetsWithoutValidAppWidgetId(context);
                if (widgetsWithoutValidAppwidgetId.size() > 0) {
                    CardView connectWidgetView = findViewById(R.id.connect_widget);
                    connectWidgetView.setVisibility(View.VISIBLE);
                    Spinner connectSpinner = findViewById(R.id.connect_spinner);
                    ArrayAdapter<Goal> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, widgetsWithoutValidAppwidgetId);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    connectSpinner.setAdapter(adapter);
                    Button connectButton = findViewById(R.id.connect_widget_button);

                    connectButton.setOnClickListener((View v) -> {
                        Integer appWidgetId = goal.getAppWidgetId();
                        goal = (Goal) connectSpinner.getSelectedItem();
                        if (goal != null) {
                            goal.setAppWidgetId(appWidgetId);
                            InteractWithGoalInDb.updateGoal(context, goal);
                            setWidgetAndFinish();
                        } else {
                            connectSpinner.setBackgroundColor(Color.RED);
                        }
                    });
                }
            });
        }
    }

    private void setPreview(TextView preview) {
        String widgetText = widgetTitle.getText().toString();
        long previewDateTime;
        // For the initial screen, show now, otherwise load the last workout
        if (isFirstConfigure) {previewDateTime = System.currentTimeMillis();}
        else {previewDateTime = goal.getLastWorkout();}

        if (showDateCheckbox.isChecked()) {widgetText += "\n" + dateBeautiful(previewDateTime);}
        if (showTimeCheckbox.isChecked()) {widgetText += "\n" + timeBeautiful(previewDateTime);}

        preview.setText(widgetText);
    }

    // OnClickListener for button
    View.OnClickListener updateWidgetOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            // When the button is clicked, store the string locally
            String widgetText = widgetTitle.getText().toString();
            boolean showDate = showDateCheckbox.isChecked();
            boolean showTime = showTimeCheckbox.isChecked();

            // Create widget object. Save it in the preferences.
            goal.setTitle(widgetText);
            goal.setIntervalBlue(intervalInDays);
            goal.setShowDate(showDate);
            goal.setShowTime(showTime);
            // If the status is updated based on the new interval, doing it here saves a DB interaction in updateWidgetBasedOnNewStatus.
            goal.setNewStatus();

            // Store the goal in the DB
            // Save the new goal to the db and store the generated uid to the widget so that the onClickListener can be generated with a valid uid later.
            if (isFirstConfigure) goal.setUid(InteractWithGoalInDb.saveDuringInitialize(context, goal));
            else InteractWithGoalInDb.updateGoal(context, goal);

            setWidgetAndFinish();
        }
    };

    private void setWidgetAndFinish() {
        // It is the responsibility of the configuration activity to update the app widget
        if(goal.hasValidAppWidgetId()) {
            goal.updateWidgetBasedOnStatus(context);}

        // Make sure we pass back the original appWidgetId.
        // Reconfiguration does not need this.
        if (isFirstConfigure) {
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, goal.getAppWidgetId());
            setResult(RESULT_OK, resultValue);
            Toast.makeText(context, "Widget created. Click on it to register a workout.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Widget updated.", Toast.LENGTH_LONG).show();
        }

        finishAndRemoveTask();
    }
}

