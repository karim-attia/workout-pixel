package ch.karimattia.workoutpixel.old;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import ch.karimattia.workoutpixel.R;
import ch.karimattia.workoutpixel.core.CommonFunctions;
import ch.karimattia.workoutpixel.core.Goal;

public class OldConfigureFragment extends Fragment {
    private static final String TAG = "WORKOUT_PIXEL CONFIGURE FRAGMENT";
    boolean isFirstConfigure = true;
    TextView goalIntervalTextView;
    TextView goalIntervalPluralTextView;
    int intervalInDays = 2;
    EditText widgetTitle;
    CheckBox showDateCheckbox;
    CheckBox showTimeCheckbox;
    Goal goal = new Goal(AppWidgetManager.INVALID_APPWIDGET_ID, "", 0, intervalInDays, 2, false, false, CommonFunctions.STATUS_NONE);
    private Context context;
    private View view;
    // OnClickListener for button
    final View.OnClickListener updateWidgetOnClickListener = new View.OnClickListener() {
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
            if (isFirstConfigure) goal.setUid(OldGoalViewModel.saveDuringInitialize(context, goal));
            else OldGoalViewModel.updateGoal(context, goal);

            setWidgetAndFinish();
        }
    };

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.configuration, container, false);
        Log.v(TAG, "onCreateView");

        // Find the widget id  and whether it is a reconfigure activity from the intent.
        // TODO: Get data. Move reading intent to activity in firstConfigure case.

        // Check from the intent whether this goal gets configured for the first time or gets reconfigured.
        // if(getArguments().getBoolean("isFirstConfigure") != null && getArguments().getBoolean("isFirstConfigure").equals("APPWIDGET_RECONFIGURE")) {
        assert getArguments() != null;
        isFirstConfigure = getArguments().getBoolean("isFirstConfigure");
        // }
        // else {
        //     isFirstConfigure = true;
        //     Log.d(TAG, "No action set");
        // }
        Log.d(TAG, "Is it a first configure activity? " + isFirstConfigure);

        // Get the AppWidgetId from the launcher if there is one provided. Else exit.
        if (isFirstConfigure) {
            // Set the result to CANCELED. This will cause the widget host to cancel out of the widget placement if the user presses the back button.
            requireActivity().setResult(RESULT_CANCELED);

            // if (getArguments().getInt("appWidgetId") != null) {
            goal.setAppWidgetId(getArguments().getInt("appWidgetId"));
            // else {Log.d(TAG, "extras = null");}

            // If this activity was started with an intent without an app widget ID, finish with an error.
            if (goal.getAppWidgetId() == null || goal.getAppWidgetId() == AppWidgetManager.INVALID_APPWIDGET_ID) {
                Log.d(TAG, "AppWidgetId is invalid.");
                requireActivity().finishAndRemoveTask();
                return view;
            }

            // Toolbar
            try {
                Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            } catch (Exception e) {
                Log.w(TAG, "requireActivity " + e);
            }
        }
        // else if (!isFirstConfigure) {
        else {
            // Get the Uid of the goal that should be configured
            // if (getArguments().getInt("widgetUid") != null) {
            goal.setUid(getArguments().getInt("goalUid"));
            // } else {Log.d(TAG, "extras = null");}

            if (goal.getUid() == 0) {
                Log.w(TAG, "goalUid is invalid.");
                Navigation.findNavController(view).navigateUp();
                return view;
            }

            // Toolbar
            requireActivity().setTitle(R.string.reconfigureWidgetActivityLabel);
            try {
                Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            } catch (Exception e) {
                Log.w(TAG, "requireActivity " + e);
            }
        }

        // Bind views
        TextView introTitle = view.findViewById(R.id.configuration_hint_title);
        TextView introText = view.findViewById(R.id.configure_activity_intro_text);
        widgetTitle = view.findViewById(R.id.appwidget_text);
        showDateCheckbox = view.findViewById(R.id.showDateCheckbox);
        showTimeCheckbox = view.findViewById(R.id.showTimeCheckbox);
        Button minusButtonInterval = view.findViewById(R.id.minus);
        goalIntervalTextView = view.findViewById(R.id.goal_interval);
        Button plusButtonInterval = view.findViewById(R.id.plus);
        goalIntervalPluralTextView = view.findViewById(R.id.plural);
        final TextView preview = view.findViewById(R.id.widget_preview);
        Button addAndUpdateButton = view.findViewById(R.id.add_button);

        // Load and pre-fill existing configurations.
        if (!isFirstConfigure) {
            // Don't show the initial text if the user edits the widget.
            introTitle.setVisibility(View.GONE);
            introText.setVisibility(View.GONE);
            goal = OldGoalViewModel.loadGoalByUid(context, goal.getUid());
            widgetTitle.setText(goal.getTitle());
            intervalInDays = goal.getIntervalBlue();
            showDateCheckbox.setChecked(goal.getShowDate());
            showTimeCheckbox.setChecked(goal.getShowTime());

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
        if (isFirstConfigure) preview.setBackgroundResource(R.drawable.rounded_corner_green);
        else
            preview.setBackgroundResource(CommonFunctions.getDrawableIntFromStatus(goal.getStatus()));

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
                List<Goal> widgetsWithoutValidAppwidgetId = OldGoalViewModel.loadGoalsWithoutValidAppWidgetId(context);
                if (widgetsWithoutValidAppwidgetId.size() > 0) {
                    TextView configurationConnectHintTitle = view.findViewById(R.id.configuration_connect_hint_title);
                    configurationConnectHintTitle.setVisibility(View.VISIBLE);
                    CardView connectWidgetView = view.findViewById(R.id.connect_widget);
                    connectWidgetView.setVisibility(View.VISIBLE);
                    Spinner connectSpinner = view.findViewById(R.id.connect_spinner);
                    ArrayAdapter<Goal> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, widgetsWithoutValidAppwidgetId);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    connectSpinner.setAdapter(adapter);
                    Button connectButton = view.findViewById(R.id.connect_widget_button);

                    connectButton.setOnClickListener((View v) -> {
                        Integer appWidgetId = goal.getAppWidgetId();
                        goal = (Goal) connectSpinner.getSelectedItem();
                        if (goal != null) {
                            goal.setAppWidgetId(appWidgetId);
                            OldGoalViewModel.updateGoal(context, goal);
                            setWidgetAndFinish();
                        } else {
                            connectSpinner.setBackgroundColor(Color.RED);
                        }
                    });

                    TextView configurationWidgetSetupTitle = view.findViewById(R.id.configuration_widget_setup_title);
                    configurationWidgetSetupTitle.setText(R.string.configuration_widget_setup_title_new_goal);
                }
            });
        }

        return view;
    }

    private void setPreview(TextView preview) {
        String widgetText = widgetTitle.getText().toString();
        long previewDateTime;
        // For the initial screen, show now, otherwise load the last workout
        if (isFirstConfigure) {
            previewDateTime = System.currentTimeMillis();
        } else {
            previewDateTime = goal.getLastWorkout();
        }

        if (showDateCheckbox.isChecked()) {
            widgetText += "\n" + CommonFunctions.dateBeautiful(previewDateTime);
        }
        if (showTimeCheckbox.isChecked()) {
            widgetText += "\n" + CommonFunctions.timeBeautiful(previewDateTime);
        }

        preview.setText(widgetText);
    }

    private void setWidgetAndFinish() {
        // It is the responsibility of the configuration activity to update the app widget
        if (goal.hasValidAppWidgetId()) {
            // new GoalSaveActions(context, goal).updateWidgetBasedOnStatus();
        }

        // Make sure we pass back the original appWidgetId.
        // Reconfiguration does not need this.
        if (isFirstConfigure) {
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, goal.getAppWidgetId());
            requireActivity().setResult(RESULT_OK, resultValue);
            Toast.makeText(context, "Widget created. Click on it to register a workout.", Toast.LENGTH_LONG).show();
            requireActivity().finishAndRemoveTask();
        } else {
            Toast.makeText(context, "Widget updated.", Toast.LENGTH_LONG).show();
            Navigation.findNavController(view).navigateUp();
        }
    }
}