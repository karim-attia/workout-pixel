package com.example.workoutpixel;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.workoutpixel.CommonFunctions.lastWorkoutDateBeautiful;
import static com.example.workoutpixel.CommonFunctions.lastWorkoutTimeBeautiful;

// RecyclerViewAdapter fills the card view in the MainActivity.
public class PastWorkoutsRecyclerViewAdapter extends RecyclerView.Adapter<PastWorkoutsRecyclerViewAdapter.WidgetViewHolder>{
    private static final String TAG = "WORKOUT_PIXEL Past Workouts RVAdapter";
    int appWidgetId;

    Context context;
    List<ClickedWorkout> clickedWorkouts = new ArrayList<>();

    PastWorkoutsRecyclerViewAdapter(Context context, int appWidgetId){
        this.context = context;
        // clickedWorkouts is 1-n and not by uid
        clickedWorkouts = InteractWithClickedWorkouts.getClickedWorkoutsFromDbByAppWidgetId(context, appWidgetId);
        this.appWidgetId = appWidgetId;
    }

    public static class WidgetViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView date;
        TextView time;
        ImageView delete;
        int appWidgetId;

        WidgetViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            date = itemView.findViewById(R.id.workout_date);
            time = itemView.findViewById(R.id.workout_time);
            delete = itemView.findViewById(R.id.workout_delete);
        }
    }
    @Override
    public int getItemCount() {
        return clickedWorkouts.size();
    }

    @Override
    public WidgetViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View widgetView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.workout_card, viewGroup, false);
        return new WidgetViewHolder(widgetView);
    }

    // onBindViewHolder sets all the parameters for an individual card view.
    @Override
    public void onBindViewHolder(final WidgetViewHolder widgetViewHolder, final int i) {
        widgetViewHolder.time.setText(lastWorkoutTimeBeautiful(clickedWorkouts.get(i).getWorkoutTime()));
        widgetViewHolder.date.setText(lastWorkoutDateBeautiful(clickedWorkouts.get(i).getWorkoutTime()));

        boolean isActive = clickedWorkouts.get(i).isActive();

        if(!isActive) {
            widgetViewHolder.date.setPaintFlags(widgetViewHolder.date.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            widgetViewHolder.time.setPaintFlags(widgetViewHolder.date.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            widgetViewHolder.delete.setImageResource(R.drawable.icon_undo);
        }
        else if(isActive) {
            widgetViewHolder.date.setPaintFlags(0);
            widgetViewHolder.time.setPaintFlags(0);
            widgetViewHolder.delete.setImageResource(R.drawable.icon_delete);
        }

        widgetViewHolder.delete.setOnClickListener (v -> {
            // TODO: Set the LastWorkout based on the latest not-deleted workout

            // isActive is true if the workout has been active before the click of the delete button.
            clickedWorkouts.get(i).setActive(!isActive);
            InteractWithClickedWorkouts.updateClickedWorkout(context, clickedWorkouts.get(i));
            notifyItemChanged(i);
            long lastActiveWorkout;

            List<ClickedWorkout> activeWorkoutsOrderedByWorkoutTime = InteractWithClickedWorkouts.workoutDao(context).loadAllActiveByAppWidgetId(appWidgetId);

            if(activeWorkoutsOrderedByWorkoutTime.size() > 0) {
                lastActiveWorkout = activeWorkoutsOrderedByWorkoutTime.get(0).getWorkoutTime();
                Log.v(TAG, "Size: " + InteractWithClickedWorkouts.workoutDao(context).loadAllActiveByAppWidgetId(appWidgetId).size());
            }
            else {
                lastActiveWorkout = 0L;
                Log.v(TAG, "No remaining workout");
            }
            ManageSavedPreferences.saveLastWorkout(context, appWidgetId, lastActiveWorkout);
            WidgetFunctions.updateBasedOnNewStatus(context, appWidgetId);

        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}