package com.example.workoutpixel.Core;

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

import com.example.workoutpixel.Database.Widget;
import com.example.workoutpixel.MainActivity.InteractWithWidget;
import com.example.workoutpixel.R;

import java.util.List;
import java.util.Objects;

import static com.example.workoutpixel.Core.CommonFunctions.STATUS_NONE;
import static com.example.workoutpixel.Core.CommonFunctions.getDrawableIntFromStatus;
import static com.example.workoutpixel.Core.CommonFunctions.getNewStatus;
import static com.example.workoutpixel.Core.CommonFunctions.lastWorkoutDateBeautiful;
import static com.example.workoutpixel.Core.CommonFunctions.lastWorkoutTimeBeautiful;

/**
 * The configuration screen for the {@link WidgetFunctions WidgetFunctions} AppWidget.
 */
public class ConfigureActivity extends AppCompatActivity {
    private static final String TAG = "WORKOUT_PIXEL CONFIGURE ACTIVITY";
    final Context context = ConfigureActivity.this;

    boolean isReconfigure;

    TextView goalIntervalTextView;
    TextView goalIntervalPluralTextView;
    int intervalInDays = 2;

    EditText widgetTitle;
    CheckBox showDateCheckbox;
    CheckBox showTimeCheckbox;

    Widget widget = new Widget(AppWidgetManager.INVALID_APPWIDGET_ID, "", 0, intervalInDays, 2, false, false, STATUS_NONE);

    // OnClickListener for button
    View.OnClickListener updateWidgetOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            // When the button is clicked, store the string locally
            String widgetText = widgetTitle.getText().toString();
            boolean showDate = showDateCheckbox.isChecked();
            boolean showTime = showTimeCheckbox.isChecked();

            // Create widget object. Save it in the preferences.
            widget.setTitle(widgetText);
            widget.setIntervalBlue(intervalInDays);
            widget.setShowDate(showDate);
            widget.setShowTime(showTime);
            // If the status is updated based on the new interval, doing it here saves a DB interaction in updateWidgetBasedOnNewStatus.
            widget.setStatus(getNewStatus(widget.getLastWorkout(), widget.getIntervalBlue()));

            // Save new prefs
            if (isReconfigure) InteractWithWidget.updateWidget(context, widget);
            else InteractWithWidget.saveDuringInitialize(context, widget);

            setWidgetAndFinish();
        }
    };

    public ConfigureActivity() {
        super();
    }

    private void setWidgetAndFinish() {
        // It is the responsibility of the configuration activity to update the app widget
        Log.v(TAG, "UPDATE_THROUGH_CONFIGURE_ACTIVITY");
        widget.updateWidgetBasedOnStatus(context);

        // Make sure we pass back the original appWidgetId.
        // WorkoutPixelReConfigureActivity does not need this.
        if (!isReconfigure) {
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getAppWidgetId());
            setResult(RESULT_OK, resultValue);
            Toast.makeText(context, "Widget created. Click on it to register a workout.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Widget updated.", Toast.LENGTH_LONG).show();
        }

        finishAndRemoveTask();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "ON_CREATE");
        super.onCreate(savedInstanceState);

        // Find the widget id  and whether it is a reconfigure activity from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widget.setAppWidgetId(extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID));
        } else {Log.d(TAG, "extras = null");}

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (widget.getAppWidgetId() == null || widget.getAppWidgetId() == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.d(TAG, "AppWidgetId is invalid.");
            finishAndRemoveTask();
            return;
        }
        isReconfigure = intent.getAction().equals("APPWIDGET_RECONFIGURE");
        Log.v(TAG, "Is it a reconfigure activity? " + isReconfigure);

        // Change the activity title in the app bar
        if (isReconfigure) {
            setTitle(R.string.ReconfigureWidgetActivityLabel);
        }

        setContentView(R.layout.workout_pixel_configure);

        if (!isReconfigure) {
            // Set the result to CANCELED.  This will cause the widget host to cancel out of the widget placement if the user presses the back button.
            setResult(RESULT_CANCELED);
            // Disable the back button in the app bar.
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        }

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
        if (isReconfigure) {
            // Don't show the initial text if the user edits the widget.
            introText.setVisibility(View.GONE);
            widget = InteractWithWidget.loadWidgetByAppWidgetId(context, widget.getAppWidgetId());
            widgetTitle.setText(widget.getTitle());
            intervalInDays = widget.getIntervalBlue();
            showDateCheckbox.setChecked(widget.getShowDate());
            showTimeCheckbox.setChecked(widget.getShowTime());

            addAndUpdateButton.setText(R.string.add_widget_reconfigure);
        }

        goalIntervalTextView.setText(String.valueOf(intervalInDays));

        // Make plus and minus button work
        minusButtonInterval.setOnClickListener(v -> {
            if (intervalInDays > 1) {
                intervalInDays--;
            }
            goalIntervalTextView.setText(String.valueOf(intervalInDays));
            if (intervalInDays < 2) {
                goalIntervalPluralTextView.setVisibility(View.GONE);
            }
        });

        plusButtonInterval.setOnClickListener(v -> {
            if (intervalInDays < 366) {
                intervalInDays++;
            }
            goalIntervalTextView.setText(String.valueOf(intervalInDays));
            if (intervalInDays > 1) {
                goalIntervalPluralTextView.setVisibility(View.VISIBLE);
            }
        });

        // Preview
        if (isReconfigure)
            preview.setBackgroundResource(getDrawableIntFromStatus(widget.getStatus()));
        else preview.setBackgroundResource(R.drawable.rounded_corner_green);

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

        // Update widget button
        addAndUpdateButton.setOnClickListener(updateWidgetOnClickListener);

        // Reconnect widget
        CommonFunctions.executorService.execute(() -> {
            List<Widget> widgetsWithoutValidAppwidgetId = InteractWithWidget.loadWidgetsWithoutValidAppWidgetId(context);
            if (!isReconfigure & widgetsWithoutValidAppwidgetId.size() > 0) {
                CardView connectWidgetView = findViewById(R.id.connect_widget);
                connectWidgetView.setVisibility(View.VISIBLE);
                Spinner connectSpinner = (Spinner) findViewById(R.id.connect_spinner);
                ArrayAdapter<Widget> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, widgetsWithoutValidAppwidgetId);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                connectSpinner.setAdapter(adapter);
                Button connectButton = findViewById(R.id.connect_widget_button);
                connectButton.setOnClickListener((View v) -> {
                    Integer appWidgetId = widget.getAppWidgetId();
                    widget = (Widget) ((Spinner) connectSpinner).getSelectedItem();
                    if (widget != null) {
                        widget.setAppWidgetId(appWidgetId);
                        InteractWithWidget.updateWidget(context, widget);
                        setWidgetAndFinish();
                    } else {
                        connectSpinner.setBackgroundColor(Color.RED);
                    }
                });
            }
        });

    }

    private void setPreview(TextView preview) {
        String widgetText = widgetTitle.getText().toString();

        if (showDateCheckbox.isChecked()) {
            String lastWorkoutDateBeautiful;
            // For the initial screen, show now, otherwise load the last workout
            if (isReconfigure) {
                lastWorkoutDateBeautiful = lastWorkoutDateBeautiful(widget.getLastWorkout());
            } else {
                lastWorkoutDateBeautiful = lastWorkoutDateBeautiful(System.currentTimeMillis());
            }
            widgetText += "\n" + lastWorkoutDateBeautiful;
        }

        if (showTimeCheckbox.isChecked()) {
            String lastWorkoutTimeBeautiful;
            if (isReconfigure) {
                lastWorkoutTimeBeautiful = lastWorkoutTimeBeautiful(widget.getLastWorkout());
            } else {
                lastWorkoutTimeBeautiful = lastWorkoutTimeBeautiful(System.currentTimeMillis());
            }
            widgetText += "\n" + lastWorkoutTimeBeautiful;
        }

        preview.setText(widgetText);
    }
}

