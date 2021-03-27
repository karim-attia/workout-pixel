package com.example.workoutpixel;

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

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;

import static com.example.workoutpixel.CommonFunctions.*;

/**
 * The configuration screen for the {@link WorkoutPixel WorkoutPixel} AppWidget.
 */
public class WorkoutPixelConfigureActivity extends AppCompatActivity {
    private static final String TAG = "WORKOUT_PIXEL CONFIGURE ACTIVITY";
    final Context context = WorkoutPixelConfigureActivity.this;

    boolean isReconfigure;

    TextView goalIntervalTextView;
    TextView goalIntervalPluralTextView;
    int intervalInDays = 2;

    int intervalInMilliseconds(int intervalInDays) {return intervalInDays*24*60*60*1000;};

    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    CheckBox showDateCheckbox;
    CheckBox showTimeCheckbox;

    public WorkoutPixelConfigureActivity() {super();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "ON_CREATE");
        super.onCreate(savedInstanceState);

        // Find the widget id  and whether it is a reconfigure activity from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        isReconfigure = intent.getAction().equals("APPWIDGET_RECONFIGURE");
        Log.v(TAG, "Is it a reconfigure activity? " + isReconfigure);

        // Change the activity title in the app bar
        if(isReconfigure) {
            setTitle(R.string.ReconfigureWidgetActivityLabel);
        }

        setContentView(R.layout.workout_pixel_configure);

        if(!isReconfigure) {
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

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finishAndRemoveTask();
            return;
        }

        // Load and pre-fill existing configurations.
        if (isReconfigure){
            // Don't show the initial text if the user edits the widget.
            introText.setVisibility(View.GONE);
            mAppWidgetText.setText(ManageSavedPreferences.loadTitle(this, appWidgetId));
            intervalInDays = ManageSavedPreferences.loadIntervalBlue(this, appWidgetId)/(24*60*60*1000);
            showDateCheckbox.setChecked(ManageSavedPreferences.loadShowDate(this, appWidgetId));
            showTimeCheckbox.setChecked(ManageSavedPreferences.loadShowTime(this, appWidgetId));
            addAndUpdateButton.setText(R.string.add_widget_reconfigure);
        }
        goalIntervalTextView.setText(intervalInDays + "");

        // Make plus and minus button work
        minusButtonInterval.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intervalInDays > 1){
                    intervalInDays--;}
                goalIntervalTextView.setText(intervalInDays + "");
                if (intervalInDays < 2){
                    goalIntervalPluralTextView.setVisibility(View.GONE);}
            }
        });

        plusButtonInterval.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intervalInDays < 366){
                    intervalInDays++;}
                goalIntervalTextView.setText(intervalInDays + "");
                if (intervalInDays > 1){
                    goalIntervalPluralTextView.setVisibility(View.VISIBLE);}
            }
        });

        // Preview
        if(isReconfigure) preview.setBackgroundResource(getDrawableIntFromStatus(ManageSavedPreferences.loadCurrentStatus(context, appWidgetId)));
        if(!isReconfigure) preview.setBackgroundResource(R.drawable.rounded_corner_green);

        setPreview(preview);
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPreview(preview);
            }
        };
        showDateCheckbox.setOnCheckedChangeListener(onCheckedChangeListener);
        showTimeCheckbox.setOnCheckedChangeListener(onCheckedChangeListener);

        mAppWidgetText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {setPreview(preview);            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Update widget button
        addAndUpdateButton.setOnClickListener(updateWidgetOnClickListener);
    }

    // OnClickListener for button
    View.OnClickListener updateWidgetOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            // When the button is clicked, store the string locally
            String widgetTitle = mAppWidgetText.getText().toString();
            boolean showDate = showDateCheckbox.isChecked();
            boolean showTime = showTimeCheckbox.isChecked();

            // Create widget object. Save it in the preferences.
            Widget widget = new Widget(appWidgetId, widgetTitle, 0, intervalInMilliseconds(intervalInDays), 2, showDate, showTime, STATUS_NONE);

            // Save new prefs
            ManageSavedPreferences.saveDuringInitialize(context, widget);

            // It is the responsibility of the configuration activity to update the app widget
            Log.v(TAG, "UPDATE_THROUGH_CONFIGURE_ACTIVITY");
            WorkoutPixel.updateAppWidget(context, appWidgetId, !isReconfigure);

            // WorkoutPixelReConfigureActivity does not need this:
            // Make sure we pass back the original appWidgetId
            if(!isReconfigure){
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            }

            finishAndRemoveTask();
        }
    };

    private void setPreview(TextView preview) {
        String widgetTitle = mAppWidgetText.getText().toString();

        if (showDateCheckbox.isChecked()) {
            String lastWorkoutDateBeautiful = DateFormat.getDateInstance(DateFormat.SHORT).format(ManageSavedPreferences.loadLastWorkout(this, appWidgetId));
            widgetTitle += "\n" + lastWorkoutDateBeautiful;
        }

        if (showTimeCheckbox.isChecked()) {
            String lastWorkoutTimeBeautiful = DateFormat.getTimeInstance(DateFormat.SHORT).format(ManageSavedPreferences.loadLastWorkout(this, appWidgetId));
            widgetTitle += "\n" + lastWorkoutTimeBeautiful;
        }

        preview.setText(widgetTitle);
    }
}

