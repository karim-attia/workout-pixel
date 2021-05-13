package com.example.workoutpixel.PastWorkouts;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutpixel.Database.PastWorkout;
import com.example.workoutpixel.Database.Goal;
import com.example.workoutpixel.Main.InteractWithGoalInDb;
import com.example.workoutpixel.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.workoutpixel.Core.CommonFunctions.dateBeautiful;
import static com.example.workoutpixel.Core.CommonFunctions.timeBeautiful;

// RecyclerViewAdapter fills the card view in the MainActivity.
public class PastWorkoutsRecyclerViewAdapter extends RecyclerView.Adapter<PastWorkoutsRecyclerViewAdapter.PastWorkoutsViewHolder> {
    private static final String TAG = "WORKOUT_PIXEL Past Workouts RVAdapter";

    Context context;
    List<PastWorkout> pastWorkouts = new ArrayList<>();
    List<PastWorkout> activeWorkoutsOrderedByWorkoutTime = new ArrayList<>();
    Goal goal;

    PastWorkoutsRecyclerViewAdapter(Context context, Goal goal) {
        this.context = context;
        this.goal = goal;
    }

    public void setData(List<PastWorkout> pastWorkouts) {
        this.pastWorkouts = pastWorkouts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return pastWorkouts.size();
    }

    @NonNull
    @Override
    public PastWorkoutsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View PastWorkoutsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_past_workout, viewGroup, false);
        return new PastWorkoutsViewHolder(PastWorkoutsView);
    }

    // onBindViewHolder sets all the parameters for an individual card view.
    @Override
    public void onBindViewHolder(final PastWorkoutsViewHolder pastWorkoutsViewHolder, final int i) {

        pastWorkoutsViewHolder.time.setText(timeBeautiful(pastWorkouts.get(i).getWorkoutTime()));
        pastWorkoutsViewHolder.date.setText(dateBeautiful(pastWorkouts.get(i).getWorkoutTime()));

        boolean isActive = pastWorkouts.get(i).isActive();

        if (!isActive) {
            pastWorkoutsViewHolder.date.setPaintFlags(pastWorkoutsViewHolder.date.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            pastWorkoutsViewHolder.time.setPaintFlags(pastWorkoutsViewHolder.date.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            pastWorkoutsViewHolder.delete.setImageResource(R.drawable.icon_undo);
        } else {
            pastWorkoutsViewHolder.date.setPaintFlags(0);
            pastWorkoutsViewHolder.time.setPaintFlags(0);
            pastWorkoutsViewHolder.delete.setImageResource(R.drawable.icon_delete);
        }

        pastWorkoutsViewHolder.delete.setOnClickListener(v -> {
            // isActive is true if the workout has been active before the click of the delete button.
            pastWorkouts.get(i).setActive(!isActive);
            InteractWithPastWorkout.updatePastWorkout(context, pastWorkouts.get(i));

            // Update the data in the recycler view
            notifyItemChanged(i);

            // If this change causes a new last workout time, do all the necessary updates.
            if(goal.setNewLastWorkout(lastWorkoutBasedOnActiveWorkouts())) {
                InteractWithGoalInDb.updateGoal(context, goal);
                goal.runUpdate(context, false);
            }
        });
    }

    long lastWorkoutBasedOnActiveWorkouts() {
        activeWorkoutsOrderedByWorkoutTime = pastWorkouts.stream().filter(clickedWorkout -> clickedWorkout.active).collect(Collectors.toList());
        // If there still is an active past workout, take the latest one to set the last workout time
        if (activeWorkoutsOrderedByWorkoutTime.size() > 0) {
            Log.v(TAG, "Size: " + activeWorkoutsOrderedByWorkoutTime.size());
            return activeWorkoutsOrderedByWorkoutTime.get(0).getWorkoutTime();
        }
        // Otherwise, set it to 0.
        else {
            Log.v(TAG, "Size: " + activeWorkoutsOrderedByWorkoutTime.size() + ", no remaining workout");
            return 0L;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PastWorkoutsViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView date;
        TextView time;
        ImageView delete;

        PastWorkoutsViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            date = itemView.findViewById(R.id.workout_date);
            time = itemView.findViewById(R.id.workout_time);
            delete = itemView.findViewById(R.id.workout_delete);
        }
    }
}