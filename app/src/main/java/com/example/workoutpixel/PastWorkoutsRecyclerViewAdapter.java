package com.example.workoutpixel;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.example.workoutpixel.CommonFunctions.getDrawableIntFromStatus;
import static com.example.workoutpixel.CommonFunctions.lastWorkoutDateBeautiful;
import static com.example.workoutpixel.CommonFunctions.widgetText;

// RecyclerViewAdapter fills the card view in the MainActivity.
public class PastWorkoutsRecyclerViewAdapter extends RecyclerView.Adapter<PastWorkoutsRecyclerViewAdapter.WidgetViewHolder>{
    private static final String TAG = "WORKOUT_PIXEL Past Workouts RVAdapter";

    private final int MILLISECONDS_IN_A_DAY = 24*60*60*1000;
    Context context;
    List<Widget> widgets = new ArrayList<>();

    private static int[] appWidgetIds(Context context) {
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetFunctions.class.getName());
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(55,0);
        return AppWidgetManager.getInstance(context).getAppWidgetIds(thisAppWidget);
    }

    PastWorkoutsRecyclerViewAdapter(Context context){
        this.context = context;
        for (int appWidgetId : appWidgetIds(context)) {
            widgets.add(ManageSavedPreferences.loadWidget(context, appWidgetId));
        }
    }

    public static class WidgetViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView date;
        TextView time;
        ImageView delete;

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
        return widgets.size();
    }

    @Override
    public WidgetViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View widgetView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.workout_card, viewGroup, false);
        return new WidgetViewHolder(widgetView);
    }

    // onBindViewHolder sets all the parameters for an individual card view.
    @Override
    public void onBindViewHolder(final WidgetViewHolder widgetViewHolder, final int i) {
        widgetViewHolder.date.setText(widgets.get(i).getTitle());
        widgetViewHolder.time.setText("Last click: " + lastWorkoutDateBeautiful(widgets.get(i).getLastWorkout()));
        int intervalInDays = widgets.get(i).getIntervalBlue() / MILLISECONDS_IN_A_DAY;
        String text = "Every " + intervalInDays + " day";
        if (intervalInDays>1) {text += "s";}
        widgetViewHolder.delete.setOnClickListener (v -> {
            // Update the widget the same way as a click on the widget would.
            WidgetFunctions.updateAfterClick(context, widgets.get(i).getAppWidgetId());
            // This also updates the preferences for the widget. This is used to update the according element in the widget array in this class so that the main view also gets updated immediately.
            widgets.set(i, ManageSavedPreferences.loadWidget(context, widgets.get(i).getAppWidgetId()));
            notifyItemChanged(i);
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}