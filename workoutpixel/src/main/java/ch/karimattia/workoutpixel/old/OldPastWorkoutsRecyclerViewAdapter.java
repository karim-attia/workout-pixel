package ch.karimattia.workoutpixel.old;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.karimattia.workoutpixel.R;
import ch.karimattia.workoutpixel.data.Goal;

// RecyclerViewAdapter fills the card view in the MainActivity.
public class OldPastWorkoutsRecyclerViewAdapter extends RecyclerView.Adapter<OldPastWorkoutsRecyclerViewAdapter.PastWorkoutsViewHolder> {
    private static final String TAG = "WORKOUT_PIXEL Past Workouts RVAdapter";

    final Context context;
    List<OldPastWorkout> pastWorkouts = new ArrayList<>();
    List<OldPastWorkout> activeWorkoutsOrderedByWorkoutTime = new ArrayList<>();
    final Goal goal;

    OldPastWorkoutsRecyclerViewAdapter(Context context, Goal goal) {
        this.context = context;
        this.goal = goal;
    }

    public void setData(List<OldPastWorkout> pastWorkouts) {
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

        pastWorkoutsViewHolder.time.setText(OldCommonFunctions.timeBeautiful(pastWorkouts.get(i).getWorkoutTime()));
        pastWorkoutsViewHolder.date.setText(OldCommonFunctions.dateBeautiful(pastWorkouts.get(i).getWorkoutTime()));

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
            OldInteractWithPastWorkout.updatePastWorkout(context, pastWorkouts.get(i));

            // Update the data in the recycler view
            notifyItemChanged(i);

            // If this change causes a new last workout time, do all the necessary updates.
            if(goal.setNewLastWorkout(lastWorkoutBasedOnActiveWorkouts())) {
                OldGoalViewModel.updateGoal(context, goal);
                // new GoalSaveActions(context, goal).runUpdate(false);
            }
        });
    }

    long lastWorkoutBasedOnActiveWorkouts() {
        activeWorkoutsOrderedByWorkoutTime = pastWorkouts.stream().filter(clickedWorkout -> clickedWorkout.isActive()).collect(Collectors.toList());
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
        final TextView date;
        final TextView time;
        final ImageView delete;

        PastWorkoutsViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.workout_date);
            time = itemView.findViewById(R.id.workout_time);
            delete = itemView.findViewById(R.id.workout_delete);
        }
    }
}