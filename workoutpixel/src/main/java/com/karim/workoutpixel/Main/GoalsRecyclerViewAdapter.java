package com.karim.workoutpixel.Main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.karim.workoutpixel.Database.Goal;
import com.karim.workoutpixel.R;

import java.util.ArrayList;
import java.util.List;

import static com.karim.workoutpixel.Core.CommonFunctions.STATUS_GREEN;
import static com.karim.workoutpixel.Core.CommonFunctions.dateBeautiful;
import static com.karim.workoutpixel.Core.CommonFunctions.getDrawableIntFromStatus;

// RecyclerViewAdapter fills the card view in the MainActivity.
public class GoalsRecyclerViewAdapter extends RecyclerView.Adapter<GoalsRecyclerViewAdapter.WidgetViewHolder> {
    private static final String TAG = "WORKOUT_PIXEL GoalsFragment RVAdapter";
    Context context;
    List<Goal> goals = new ArrayList<>();
    boolean notSetupYet = true;

    GoalsRecyclerViewAdapter(Context context) {
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

        View.OnClickListener goalDetailsOnClickListener = view -> {
            Bundle bundle = new Bundle();
            bundle.putInt("goalUid", goals.get(i).getUid());
            Navigation.findNavController(view).navigate(R.id.action_goalsFragment_to_goalDetailFragment, bundle);
        };

        widgetViewHolder.cardView.setOnClickListener(goalDetailsOnClickListener);
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

        WidgetViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_goal);
            widgetTitle = itemView.findViewById(R.id.widget_title);
            widgetLastWorkout = itemView.findViewById(R.id.widget_last_workout);
            widgetIntervalBlue = itemView.findViewById(R.id.widget_interval);
            widgetPreview = itemView.findViewById(R.id.widget_preview);
        }
    }
}