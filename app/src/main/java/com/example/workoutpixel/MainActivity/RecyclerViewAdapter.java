package com.example.workoutpixel.MainActivity;

import android.appwidget.AppWidgetManager;
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

import com.example.workoutpixel.Core.CommonFunctions;
import com.example.workoutpixel.Core.ConfigureActivity;
import com.example.workoutpixel.Database.Widget;
import com.example.workoutpixel.PastWorkouts.ViewWorkoutsActivity;
import com.example.workoutpixel.R;

import java.util.List;

import static com.example.workoutpixel.Core.CommonFunctions.STATUS_GREEN;
import static com.example.workoutpixel.Core.CommonFunctions.getDrawableIntFromStatus;
import static com.example.workoutpixel.Core.CommonFunctions.lastWorkoutDateBeautiful;

// RecyclerViewAdapter fills the card view in the MainActivity.
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.WidgetViewHolder> {
    private static final String TAG = "WORKOUT_PIXEL RVAdapter";
    Context context;
    List<Widget> widgets;

/*
    private static int[] appWidgetIds(Context context) {
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetFunctions.class.getName());
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(55,0);
        return AppWidgetManager.getInstance(context).getAppWidgetIds(thisAppWidget);
    }
*/

    RecyclerViewAdapter(Context context) {
        this.context = context;
        widgets = ManageSavedPreferences.loadAllWidgets(context);

        for (Widget widget : widgets) {
            // Sometimes the onClickListener in the widgets stop working. This is a super stupid way to regularly reset the onClickListener when you open the main app.
            widget.updateWidgetBasedOnNewStatus(context);
        }
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
        widgetViewHolder.widgetIntervalBlue.setText(widgets.get(i).everyWording());
        widgetViewHolder.widgetPreview.setBackgroundResource(getDrawableIntFromStatus(widgets.get(i).getStatus()));
        widgetViewHolder.widgetPreview.setText(widgets.get(i).widgetText());
        widgetViewHolder.widgetPreview.setOnClickListener(v -> {
            // Update the widget the same way as a click on the widget would.
            widgets.get(i).updateAfterClick(context);
            widgets.get(i).setLastWorkout(System.currentTimeMillis());
            widgets.get(i).setStatus(STATUS_GREEN);
            // Not needed if there is an observer on all items in the MainActivity
            notifyItemChanged(i);
        });

        View.OnClickListener editWidgetOnClickListener = v -> {
            // TODO: Also replace with uid?
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

        if(!CommonFunctions.doesWidgetHaveValidAppWidgetId(context, widgets.get(i))) {
            Log.d(TAG, "connectInfo.setVisibility VISIBLE " + widgets.get(i).debugString());
            widgetViewHolder.connectInfo.setVisibility(View.VISIBLE);
        }
        else {
            Log.d(TAG, "connectInfo.setVisibility GONE " + widgets.get(i).debugString());
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
        }
    }
}