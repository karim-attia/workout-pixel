package com.example.workoutpixel.Core;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.workoutpixel.Database.Widget;
import com.example.workoutpixel.MainActivity.ManageSavedPreferences;
import com.example.workoutpixel.R;

import static com.example.workoutpixel.Core.CommonFunctions.STATUS_NONE;
import static com.example.workoutpixel.Core.CommonFunctions.getDrawableIntFromStatus;

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

    EditText mAppWidgetText;
    CheckBox showDateCheckbox;
    CheckBox showTimeCheckbox;

    Widget widget = new Widget(AppWidgetManager.INVALID_APPWIDGET_ID, "", 0, CommonFunctions.intervalInMilliseconds(intervalInDays), 2, false, false, STATUS_NONE);

    // OnClickListener for button
    View.OnClickListener updateWidgetOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            // When the button is clicked, store the string locally
            String widgetText = mAppWidgetText.getText().toString();
            boolean showDate = showDateCheckbox.isChecked();
            boolean showTime = showTimeCheckbox.isChecked();

            // Create widget object. Save it in the preferences.
            widget.setTitle(widgetText);
            widget.setIntervalBlue(CommonFunctions.intervalInMilliseconds(intervalInDays));
            widget.setShowDate(showDate);
            widget.setShowTime(showTime);
            Log.d(TAG, "WidgetTitle " + widgetText);
            Log.d(TAG, "WidgetTitle " + widget.getTitle());
            // = new Widget(appWidgetId, widgetText, 0, CommonFunctions.intervalInMilliseconds(intervalInDays), 2, showDate, showTime, STATUS_NONE);

            // Save new prefs
            if (isReconfigure) ManageSavedPreferences.updateWidget(context, widget);
            else ManageSavedPreferences.saveDuringInitialize(context, widget);

            // It is the responsibility of the configuration activity to update the app widget
            Log.v(TAG, "UPDATE_THROUGH_CONFIGURE_ACTIVITY");
            WidgetFunctions.updateWidgetBasedOnNewStatus(context, widget);

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
    };

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
        if (extras != null) {
            widget.setAppWidgetId(extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID));
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (widget.getAppWidgetId() == AppWidgetManager.INVALID_APPWIDGET_ID) {
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // Bind views
        TextView introText = findViewById(R.id.configure_activity_intro_text);
        mAppWidgetText = findViewById(R.id.appwidget_text);
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
            widget = ManageSavedPreferences.loadWidgetByAppWidgetId(context, widget.getAppWidgetId());
            mAppWidgetText.setText(widget.getTitle());
            intervalInDays = widget.getIntervalBlue() / (24 * 60 * 60 * 1000);
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

        mAppWidgetText.addTextChangedListener(new TextWatcher() {
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
    }

    private void setPreview(TextView preview) {
        String widgetText = mAppWidgetText.getText().toString();

        if (showDateCheckbox.isChecked()) {
            String lastWorkoutDateBeautiful;
            // For the initial screen, show now, otherwise load the last workout
            if (isReconfigure) {
                lastWorkoutDateBeautiful = CommonFunctions.lastWorkoutDateBeautiful(widget.getLastWorkout());
            } else {
                lastWorkoutDateBeautiful = CommonFunctions.lastWorkoutDateBeautiful(System.currentTimeMillis());
            }
            widgetText += "\n" + lastWorkoutDateBeautiful;
        }

        if (showTimeCheckbox.isChecked()) {
            String lastWorkoutTimeBeautiful;
            if (isReconfigure) {
                lastWorkoutTimeBeautiful = CommonFunctions.lastWorkoutTimeBeautiful(widget.getLastWorkout());
            } else {
                lastWorkoutTimeBeautiful = CommonFunctions.lastWorkoutTimeBeautiful(System.currentTimeMillis());
            }
            widgetText += "\n" + lastWorkoutTimeBeautiful;
        }

        preview.setText(widgetText);
    }

}
