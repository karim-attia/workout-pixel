package com.example.workoutpixel.Main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutpixel.Core.ConfigureActivity;
import com.example.workoutpixel.Database.Goal;
import com.example.workoutpixel.PastWorkouts.ViewWorkoutsActivity;
import com.example.workoutpixel.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.workoutpixel.Core.CommonFunctions.STATUS_GREEN;
import static com.example.workoutpixel.Core.CommonFunctions.getDrawableIntFromStatus;
import static com.example.workoutpixel.Core.CommonFunctions.dateBeautiful;

// RecyclerViewAdapter fills the card view in the MainActivity.
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.WidgetViewHolder> {
    private static final String TAG = "WORKOUT_PIXEL MainActivity RVAdapter";
    Context context;
    List<Goal> goals = new ArrayList<>();
    boolean notSetupYet = true;

/*
    private static int[] appWidgetIds(Context context) {
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetFunctions.class.getName());
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(55,0);
        return AppWidgetManager.getInstance(context).getAppWidgetIds(thisAppWidget);
    }
*/

    RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();

        if (notSetupYet) {
            for (Goal goal : goals) {
                // Sometimes the onClickListener in the widgets stop working. This is a super stupid way to regularly reset the onClickListener when you open the main app.
                if (goal.hasValidAppWidgetId()) {
                    goal.updateWidgetBasedOnStatus(context);
                }
            }
        }
        // Otherwise all widgets get set up with every change.
        notSetupYet = false;
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    @NonNull
    @Override
    public WidgetViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View widgetView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_goal, viewGroup, false);
        return new WidgetViewHolder(widgetView);
    }

    // onBindViewHolder sets all the parameters for an individual card view.
    @Override
    public void onBindViewHolder(final WidgetViewHolder widgetViewHolder, final int i) {
        widgetViewHolder.widgetTitle.setText(goals.get(i).getTitle());
        widgetViewHolder.widgetLastWorkout.setText(dateBeautiful(goals.get(i).getLastWorkout()));
        widgetViewHolder.widgetIntervalBlue.setText(goals.get(i).everyWording());
        widgetViewHolder.widgetPreview.setBackgroundResource(getDrawableIntFromStatus(goals.get(i).getStatus()));
        widgetViewHolder.widgetPreview.setText(goals.get(i).widgetText());
        widgetViewHolder.widgetPreview.setOnClickListener(v -> {
            // Update the widget the same way as a click on the widget would.
            goals.get(i).updateAfterClick(context);
            goals.get(i).setLastWorkout(System.currentTimeMillis());
            goals.get(i).setStatus(STATUS_GREEN);

            // Update the data in the recycler view
            notifyItemChanged(i);
        });

        View.OnClickListener editWidgetOnClickListener = v -> {
            Intent intent = new Intent(context, ConfigureActivity.class);
            intent.setAction("APPWIDGET_RECONFIGURE");
            intent.putExtra("widgetUid", goals.get(i).getUid());
            context.startActivity(intent);
        };
        View.OnClickListener viewWorkoutsOnClickListener = v -> {
            Intent intent = new Intent(context, ViewWorkoutsActivity.class);
            intent.putExtra("widgetUid", goals.get(i).getUid());
            context.startActivity(intent);
        };

        widgetViewHolder.widgetEdit.setOnClickListener(editWidgetOnClickListener);
        widgetViewHolder.widgetEditText.setOnClickListener(editWidgetOnClickListener);
        widgetViewHolder.widgetPastWorkouts.setOnClickListener(viewWorkoutsOnClickListener);
        widgetViewHolder.widgetPastWorkoutsText.setOnClickListener(viewWorkoutsOnClickListener);

        if (!goals.get(i).hasValidAppWidgetId()) {
            // if(!CommonFunctions.doesWidgetHaveValidAppWidgetId(context, widgets.get(i))) {
            Log.v(TAG, "connectInfo.setVisibility VISIBLE " + goals.get(i).debugString());
            widgetViewHolder.connectInfo.setVisibility(View.VISIBLE);
            widgetViewHolder.deleteGoal.setOnClickListener(v -> {
                InteractWithGoalInDb.deleteGoal(context, goals.get(i));
                goals.remove(i);
                notifyDataSetChanged();
            });
        } else {
            Log.v(TAG, "connectInfo.setVisibility GONE " + goals.get(i).debugString());
            widgetViewHolder.connectInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class WidgetViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView widgetTitle;
        TextView widgetLastWorkout;
        TextView widgetIntervalBlue;
        TextView widgetPreview;
        ImageView widgetEdit;
        TextView widgetEditText;
        ImageView widgetPastWorkouts;
        TextView widgetPastWorkoutsText;
        LinearLayout connectInfo;
        ImageView deleteGoal;

        WidgetViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            widgetTitle = itemView.findViewById(R.id.widget_title);
            widgetLastWorkout = itemView.findViewById(R.id.widget_last_workout);
            widgetIntervalBlue = itemView.findViewById(R.id.widget_interval);
            widgetPreview = itemView.findViewById(R.id.widget_preview);
            widgetEdit = itemView.findViewById(R.id.widget_edit);
            widgetEditText = itemView.findViewById(R.id.widget_edit_text);
            widgetPastWorkouts = itemView.findViewById(R.id.widget_past_workouts);
            widgetPastWorkoutsText = itemView.findViewById(R.id.widget_past_workout_text);
            connectInfo = itemView.findViewById(R.id.connect_info);
            deleteGoal = itemView.findViewById(R.id.delete_goal);
        }
    }
}