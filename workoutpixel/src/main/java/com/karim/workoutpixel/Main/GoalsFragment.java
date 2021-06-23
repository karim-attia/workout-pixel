package com.karim.workoutpixel.Main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karim.workoutpixel.Database.Goal;
import com.karim.workoutpixel.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.karim.workoutpixel.Core.CommonFunctions.cleanGoals;

public class GoalsFragment extends Fragment {
    private static final String TAG = "WORKOUT_PIXEL GoalsFragment";
    private Context context;
    GoalsRecyclerViewAdapter goalsRecyclerViewAdapter;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.context = context;
        goalsRecyclerViewAdapter = new GoalsRecyclerViewAdapter(context);
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
        View view = inflater.inflate(R.layout.goals, container, false);

        // Toolbar
        requireActivity().setTitle("Workout Pixel");
        try {
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        } catch (Exception e) {
            Log.w(TAG, "requireActivity " + e);
        }

        setContent(view);
        return view;
    }

    // setContent is called when the main app is started or resumed after editing an individual widget.
    // It first loads all widgets into an array. Then it fills the recyclerView from the activity_main Layout with those widgets using the RecyclerViewAdapter.
    private void setContent(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(goalsRecyclerViewAdapter);

        // Create the observer which updates the UI.
        // Observe the LiveData, passing this activity as the LifecycleOwner and the observer.
        LiveData<List<Goal>> liveData = InteractWithGoalInDb.loadAllGoalsLiveData(context.getApplicationContext());
        liveData.observe(getViewLifecycleOwner(), goals -> {
            goalsRecyclerViewAdapter.setData(goals);
            recyclerView.setItemAnimator(null);
            liveData.removeObservers(this);
            executeAfterRecyclerViewWasPopulated(goals, view);
        });
    }

    private void executeAfterRecyclerViewWasPopulated (List<Goal> goals, View view) {
        if(goals.size() == 0) {
            CardView noGoalsInstructions = view.findViewById(R.id.no_goals_instructions);
            noGoalsInstructions.setVisibility(View.VISIBLE);
            noGoalsInstructions.setOnClickListener(v -> {
                Navigation.findNavController(view).navigate(GoalsFragmentDirections.actionGoalsFragmentToInstructionsFragment());
            });
        }
        // TODO: Move to activity?
        cleanGoals(context, goals);
    }

}