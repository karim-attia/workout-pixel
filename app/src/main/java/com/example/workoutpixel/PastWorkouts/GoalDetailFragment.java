package com.example.workoutpixel.PastWorkouts;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutpixel.Database.Goal;
import com.example.workoutpixel.Database.PastWorkout;
import com.example.workoutpixel.Main.InteractWithGoalInDb;
import com.example.workoutpixel.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static android.widget.LinearLayout.VERTICAL;
import static com.example.workoutpixel.Core.CommonFunctions.STATUS_GREEN;
import static com.example.workoutpixel.Core.CommonFunctions.dateBeautiful;
import static com.example.workoutpixel.Core.CommonFunctions.getDrawableIntFromStatus;

public class GoalDetailFragment extends Fragment {
    private static final String TAG = "WORKOUT_PIXEL GoalDetailFragment";
    View view;
    int uid;
    Goal goal;
    CardView goalCardView;
    TextView pastWorkoutsDescription;
    PastWorkoutsRecyclerViewAdapter pastWorkoutsRecyclerViewAdapter;
    private Context context;

    public GoalDetailFragment() {
        super();
    }

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
        setHasOptionsMenu(true);
        Log.d(TAG, "setHasOptionsMenu");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.goal_detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_goal:
                Bundle bundle = new Bundle();
                bundle.putBoolean("isFirstConfigure", false);
                bundle.putInt("goalUid", goal.getUid());
                Navigation.findNavController(view).navigate(R.id.action_goalDetailFragment_to_configureFragment, bundle);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.goal_detail_view, container, false);
        Log.v(TAG, "onCreateView");

        assert getArguments() != null;
        uid = getArguments().getInt("goalUid");

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (uid == 0) {
            Log.d(TAG, "uid invalid.");
            requireActivity().finishAndRemoveTask();
            return view;
        }

        goal = InteractWithGoalInDb.loadGoalByUid(context, uid);
        LiveData<Goal> goalLiveData = InteractWithGoalInDb.liveDataGoalByUid(context, uid);

        // Toolbar
        requireActivity().setTitle(goal.getTitle());
        try {
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            Log.w(TAG, "requireActivity " + e);
        }

        // Title
        // TextView title = view.findViewById(R.id.title_goal_detail_view);
        // title.setText(goal.getTitle());

        // Goal overview card
        setGoalOverview(goal);
        goalLiveData.observe(getViewLifecycleOwner(), this::setGoalOverview);

        // Past workouts card
        // Bind views and set them
        TextView date = view.findViewById(R.id.workout_date);
        date.setTypeface(null, Typeface.BOLD);
        TextView time = view.findViewById(R.id.workout_time);
        time.setTypeface(null, Typeface.BOLD);
        ImageView delete = view.findViewById(R.id.workout_delete);
        delete.setImageResource(0);
        pastWorkoutsDescription = view.findViewById(R.id.past_workouts_description);

        // Populate RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        pastWorkoutsRecyclerViewAdapter = new PastWorkoutsRecyclerViewAdapter(context, goal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, VERTICAL), 0);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, VERTICAL));
        recyclerView.setAdapter(pastWorkoutsRecyclerViewAdapter);

        // Create the observer which updates the UI.
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        LiveData<List<PastWorkout>> liveData = InteractWithPastWorkout.getPastWorkouts(context.getApplicationContext(), goal.uid);
        liveData.observe(getViewLifecycleOwner(), pastWorkouts -> {
            if (pastWorkouts.size() > 0) {
                pastWorkoutsRecyclerViewAdapter.setData(pastWorkouts);
                pastWorkoutsDescription.setVisibility(View.GONE);
            } else {
                LinearLayout pastWorkoutsList = view.findViewById(R.id.card_view_past_workout);
                pastWorkoutsList.setVisibility(View.GONE);
                View pastWorkoutsDivider = view.findViewById(R.id.past_workouts_divider);
                pastWorkoutsDivider.setVisibility(View.GONE);
            }
            liveData.removeObservers(this);
        });

        // Connect View
        CardView connectInfo = view.findViewById(R.id.connect_info);

        if (!goal.hasValidAppWidgetId()) {
            // if(!CommonFunctions.doesWidgetHaveValidAppWidgetId(context, widgets.get(i))) {
            Log.v(TAG, "connectInfo.setVisibility VISIBLE " + goal.debugString());
            connectInfo.setVisibility(View.VISIBLE);
            Button deleteGoal = view.findViewById(R.id.delete_goal);
            deleteGoal.setOnClickListener(v -> {
                // TODO: Confirmation dialog?
                InteractWithGoalInDb.deleteGoal(context, goal);
                Navigation.findNavController(view).navigateUp();
            });
        } else {
            Log.v(TAG, "connectInfo.setVisibility GONE " + goal.debugString());
            connectInfo.setVisibility(View.GONE);
        }

        return view;
    }

    /*
        public void setPastWorkoutsDescription () {
            int countOfActivePastWorkouts = InteractWithPastWorkout.getCountOfActivePastWorkouts(context, uid);

            if (countOfActivePastWorkouts > 0) {
                pastWorkoutsDescription.setText("Already done this " + CommonFunctions.times(countOfActivePastWorkouts) + ".");
            } else if (countOfActivePastWorkouts == 0) {
                pastWorkoutsDescription.setText("You have never completed this goal. As soon as you click on the widget, the click will show up here.");
            } else {
                Log.w(TAG, "numberOfPastWorkouts is below 0: " + countOfActivePastWorkouts);
            }
        }
    */
    public void setGoalOverview(Goal goal) {
        // goalCardView.setVisibility(View.VISIBLE);
        TextView widgetLastWorkout = view.findViewById(R.id.widget_last_workout);
        TextView widgetIntervalBlue = view.findViewById(R.id.widget_interval);
        TextView widgetPreview = view.findViewById(R.id.widget_preview);
        widgetLastWorkout.setText(dateBeautiful(goal.getLastWorkout()));
        widgetIntervalBlue.setText(goal.everyWording());
        widgetPreview.setBackgroundResource(getDrawableIntFromStatus(goal.getStatus()));
        widgetPreview.setText(goal.widgetText());
        widgetPreview.setOnClickListener(v -> {
            // Update the widget the same way as a click on the widget would.
            goal.updateAfterClick(context);
            goal.setLastWorkout(System.currentTimeMillis());
            goal.setStatus(STATUS_GREEN);
            LinearLayout pastWorkoutsList = view.findViewById(R.id.card_view_past_workout);
            pastWorkoutsList.setVisibility(View.VISIBLE);
            View pastWorkoutsDivider = view.findViewById(R.id.past_workouts_divider);
            pastWorkoutsDivider.setVisibility(View.VISIBLE);




/*
            // Not needed due to live data
            // Update the goal overview after the click
            setGoalOverview(goal);
*/

            // Update the data in the recycler view
            pastWorkoutsRecyclerViewAdapter.notifyDataSetChanged();
        });

    }

}

