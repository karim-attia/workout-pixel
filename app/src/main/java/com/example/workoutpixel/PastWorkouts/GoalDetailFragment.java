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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
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

    View view;
    int uid;
    Goal goal;

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
                Navigation.findNavController(view).navigate(R.id.configureFragment, bundle);
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
            getActivity().finishAndRemoveTask();
            return view;
        }

        goal = InteractWithGoalInDb.loadGoalByUid(context, uid);

        // Toolbar
        requireActivity().setTitle("Workout Pixel > " + goal.getTitle());
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // requireActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        // requireActivity().getActionBar().setDisplayShowHomeEnabled(true);
        // requireActivity().getActionBar().setHomeButtonEnabled(true);


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

