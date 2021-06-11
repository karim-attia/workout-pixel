package com.example.workoutpixel.PastWorkouts;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutpixel.Database.Goal;
import com.example.workoutpixel.Database.PastWorkout;
import com.example.workoutpixel.Main.InteractWithGoalInDb;
import com.example.workoutpixel.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoalDetailFragment extends Fragment {
    private static final String TAG = "WORKOUT_PIXEL ViewWorkoutsActivity";
    // final Context context = GoalDetailFragment.this;
    private Context context;

    int uid;

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

    public GoalDetailFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.goal_detail_view, container, false);
        Log.v(TAG, "onCreateView");

        // Find the widget id  from the intent.
/*
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            uid = extras.getInt("widgetUid", 0);
        }
*/

        assert getArguments() != null;
        uid = getArguments().getInt("goalUid");


        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (uid == 0) {
            Log.d(TAG, "uid invalid.");
            getActivity().finishAndRemoveTask();
            return view;
        }

        Goal goal = InteractWithGoalInDb.loadGoalByUid(context, uid);

        // Bind views and set them
        TextView title = view.findViewById(R.id.widget_title);
        title.setText(goal.getTitle());
        // ManageSavedPreferences.loadTitleByAppWidgetId(context, appWidgetId).observe(this, title::setText);
        TextView date = view.findViewById(R.id.workout_date);
        date.setTypeface(null, Typeface.BOLD);
        TextView time = view.findViewById(R.id.workout_time);
        time.setTypeface(null, Typeface.BOLD);
        ImageView delete = view.findViewById(R.id.workout_delete);
        delete.setImageResource(0);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        PastWorkoutsRecyclerViewAdapter pastWorkoutsRecyclerViewAdapter = new PastWorkoutsRecyclerViewAdapter(context, goal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(pastWorkoutsRecyclerViewAdapter);

        // Create the observer which updates the UI.
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        LiveData<List<PastWorkout>> liveData = InteractWithPastWorkout.getPastWorkouts(context.getApplicationContext(), goal.uid);
        InteractWithPastWorkout.getPastWorkouts(context, goal.uid).observe(getViewLifecycleOwner(), pastWorkouts -> {
            pastWorkoutsRecyclerViewAdapter.setData(pastWorkouts);
            liveData.removeObservers(this);
        });
        return view;
    }
}

