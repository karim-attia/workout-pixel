package com.example.workoutpixel.MainActivity;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutpixel.Core.ConfigureActivity;
import com.example.workoutpixel.Core.WidgetFunctions;
import com.example.workoutpixel.Database.Widget;
import com.example.workoutpixel.PastWorkouts.ViewWorkoutsActivity;
import com.example.workoutpixel.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.workoutpixel.Core.CommonFunctions.getDrawableIntFromStatus;
import static com.example.workoutpixel.Core.CommonFunctions.lastWorkoutDateBeautiful;
import static com.example.workoutpixel.Core.CommonFunctions.widgetText;

// RecyclerViewAdapter fills the card view in the MainActivity.
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.WidgetViewHolder> {
    private static final String TAG = "WORKOUT_PIXEL RVAdapter";

    private final int MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000;
    Context context;
    List<Widget> widgets = new ArrayList<>();

/*
    private static int[] appWidgetIds(Context context) {
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetFunctions.class.getName());
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(55,0);
        return AppWidgetManager.getInstance(context).getAppWidgetIds(thisAppWidget);
    }
*/

    RecyclerViewAdapter(Context context) {
        this.context = context;
/*
        for (int appWidgetId : appWidgetIds(context)) {
            widgets.add(ManageSavedPreferences.loadWidget(context, appWidgetId));
            // Sometimes the onClickListener in the widgets stop working. This is a super stupid way to regularly reset the onClickListener when you open the main app.
            WidgetFunctions.initiateBasedOnStatus(context, appWidgetId);
        }
*/
    }

    public void setData(List<Widget> widgets) {
        this.widgets = widgets;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return widgets.size();
    }

    @NonNull
    @Override
    public WidgetViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View widgetView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview, viewGroup, false);
        return new WidgetViewHolder(widgetView);
    }

    // onBindViewHolder sets all the parameters for an individual card view.
    @Override
    public void onBindViewHolder(final WidgetViewHolder widgetViewHolder, final int i) {
        widgetViewHolder.widgetTitle.setText(widgets.get(i).getTitle());
        widgetViewHolder.widgetLastWorkout.setText(lastWorkoutDateBeautiful(widgets.get(i).getLastWorkout()));
        int intervalInDays = widgets.get(i).getIntervalBlue() / MILLISECONDS_IN_A_DAY;
        String text = "Every " + intervalInDays + " day";
        if (intervalInDays > 1) {
            text += "s";
        }
        widgetViewHolder.widgetIntervalBlue.setText(text);
        widgetViewHolder.widgetPreview.setBackgroundResource(getDrawableIntFromStatus(widgets.get(i).getStatus()));
        widgetViewHolder.widgetPreview.setText(widgetText(widgets.get(i)));
        widgetViewHolder.widgetPreview.setOnClickListener(v -> {
            // Update the widget the same way as a click on the widget would.
            WidgetFunctions.updateAfterClick(context, widgets.get(i));
            // This also updates the preferences for the widget. This is used to update the according element in the widget array in this class so that the main view also gets updated immediately.
            // Not needed after there is an observer on all items anyway
            // widgets.set(i, ManageSavedPreferences.loadWidget(context, widgets.get(i).getAppWidgetId()));
            // Not needed after there is an observer on all items anyway
            // notifyItemChanged(i);
        });

        View.OnClickListener editWidgetOnClickListener = v -> {
            int appWidgetId = widgets.get(i).getAppWidgetId();
            Intent intent = new Intent(context, ConfigureActivity.class);
            intent.setAction("APPWIDGET_RECONFIGURE");
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            context.startActivity(intent);
        };
        View.OnClickListener viewWorkoutsOnClickListener = v -> {
            int appWidgetId = widgets.get(i).getAppWidgetId();
            Intent intent = new Intent(context, ViewWorkoutsActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            context.startActivity(intent);
        };

        widgetViewHolder.widgetEdit.setOnClickListener(editWidgetOnClickListener);
        widgetViewHolder.widgetEditText.setOnClickListener(editWidgetOnClickListener);
        widgetViewHolder.widgetPastWorkouts.setOnClickListener(viewWorkoutsOnClickListener);
        widgetViewHolder.widgetPastWorkoutsText.setOnClickListener(viewWorkoutsOnClickListener);
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
        }
    }
}