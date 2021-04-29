package com.example.workoutpixel.ClickedWorkouts;

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

import com.example.workoutpixel.ManageSavedPreferences;
import com.example.workoutpixel.R;
import com.example.workoutpixel.WidgetFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.workoutpixel.CommonFunctions.lastWorkoutDateBeautiful;
import static com.example.workoutpixel.CommonFunctions.lastWorkoutTimeBeautiful;

// RecyclerViewAdapter fills the card view in the MainActivity.
public class PastWorkoutsRecyclerViewAdapter extends RecyclerView.Adapter<PastWorkoutsRecyclerViewAdapter.PastWorkoutsViewHolder>{
    private static final String TAG = "WORKOUT_PIXEL Past Workouts RVAdapter";
    int appWidgetId;

    Context context;
    List<ClickedWorkout> clickedWorkouts = new ArrayList<>();
    List<ClickedWorkout> activeWorkoutsOrderedByWorkoutTime = new ArrayList<>();

    PastWorkoutsRecyclerViewAdapter(Context context, int appWidgetId){
        this.context = context;
        this.appWidgetId = appWidgetId;
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

    public void setData(List<ClickedWorkout> clickedWorkouts) {
        this.clickedWorkouts = clickedWorkouts;
        //this.activeWorkoutsOrderedByWorkoutTime = activeWorkoutsOrderedByWorkoutTime;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return clickedWorkouts.size();
    }

    @NonNull
    @Override
    public PastWorkoutsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View PastWorkoutsView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.workout_card, viewGroup, false);
        return new PastWorkoutsViewHolder(PastWorkoutsView);
    }

    // onBindViewHolder sets all the parameters for an individual card view.
    @Override
    public void onBindViewHolder(final PastWorkoutsViewHolder pastWorkoutsViewHolder, final int i) {

        pastWorkoutsViewHolder.time.setText(lastWorkoutTimeBeautiful(clickedWorkouts.get(i).getWorkoutTime()));
        pastWorkoutsViewHolder.date.setText(lastWorkoutDateBeautiful(clickedWorkouts.get(i).getWorkoutTime()));

        boolean isActive = clickedWorkouts.get(i).isActive();

        if(!isActive) {
            pastWorkoutsViewHolder.date.setPaintFlags(pastWorkoutsViewHolder.date.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            pastWorkoutsViewHolder.time.setPaintFlags(pastWorkoutsViewHolder.date.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            pastWorkoutsViewHolder.delete.setImageResource(R.drawable.icon_undo);
        }
        else {
            pastWorkoutsViewHolder.date.setPaintFlags(0);
            pastWorkoutsViewHolder.time.setPaintFlags(0);
            pastWorkoutsViewHolder.delete.setImageResource(R.drawable.icon_delete);
        }

        pastWorkoutsViewHolder.delete.setOnClickListener (v -> {
            // isActive is true if the workout has been active before the click of the delete button.
            clickedWorkouts.get(i).setActive(!isActive);
            ClickedWorkoutViewModel.updateClickedWorkout(context, clickedWorkouts.get(i));
            // notifyItemChanged(i);

/*
            activeWorkoutsOrderedByWorkoutTime.clear();
            for (ClickedWorkout clickedWorkout  : clickedWorkouts) {
                    if(clickedWorkout.active) activeWorkoutsOrderedByWorkoutTime.add(clickedWorkout);
            }
*/
            activeWorkoutsOrderedByWorkoutTime = clickedWorkouts.stream().filter(clickedWorkout -> clickedWorkout.active).collect(Collectors.toList());

            long lastActiveWorkout;
            if(activeWorkoutsOrderedByWorkoutTime.size() > 0) {
                lastActiveWorkout = activeWorkoutsOrderedByWorkoutTime.get(0).getWorkoutTime();
                Log.v(TAG, "Size: " + activeWorkoutsOrderedByWorkoutTime.size());
            }
            else {
                lastActiveWorkout = 0L;
                Log.v(TAG, "Size: " + activeWorkoutsOrderedByWorkoutTime.size() + ", no remaining workout");
            }
            ManageSavedPreferences.saveLastWorkout(context, appWidgetId, lastActiveWorkout);
            WidgetFunctions.updateBasedOnNewStatus(context, appWidgetId);
        });

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}