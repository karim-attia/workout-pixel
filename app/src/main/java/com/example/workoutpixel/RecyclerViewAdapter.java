package com.example.workoutpixel;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.workoutpixel.CommonFunctions.*;

// RecyclerViewAdapter fills the card view in the MainActivity.
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.WidgetViewHolder>{
    private static final String TAG = "WORKOUT_PIXEL RVAdapter";

    private final int MILLISECONDS_IN_A_DAY = 24*60*60*1000;
    Context context;
    List<Widget> widgets = new ArrayList<>();

    private static int[] appWidgetIds(Context context) {
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WorkoutPixel.class.getName());
        return AppWidgetManager.getInstance(context).getAppWidgetIds(thisAppWidget);
    }

    RecyclerViewAdapter(Context context){
        this.context = context;
        for (int appWidgetId : appWidgetIds(context)) {
            widgets.add(ManageSavedPreferences.loadWidget(context, appWidgetId));
            // Sometimes the onClickListener in the widgets stop working. This is a super stupid way to regularly reset the onClickListener when you open the main app.
            WorkoutPixel.initiateBasedOnStatus(context, appWidgetId);
        }
    }

    public static class WidgetViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView widgetTitle;
        TextView widgetLastWorkout;
        TextView widgetIntervalBlue;
        TextView widgetPreview;
        ImageView widgetEdit;

        WidgetViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
            widgetTitle = (TextView)itemView.findViewById(R.id.widget_title);
            widgetLastWorkout = (TextView)itemView.findViewById(R.id.widget_last_workout);
            widgetIntervalBlue = (TextView)itemView.findViewById(R.id.widget_interval);
            widgetPreview = (TextView)itemView.findViewById(R.id.widget_preview);
            widgetEdit = (ImageView) itemView.findViewById(R.id.widget_edit);
        }
    }
    @Override
    public int getItemCount() {
        return widgets.size();
    }

    @Override
    public WidgetViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View widgetView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview, viewGroup, false);
        return new WidgetViewHolder(widgetView);
    }

    // onBindViewHolder sets all the parameters for an individual card view.
    @Override
    public void onBindViewHolder(final WidgetViewHolder widgetViewHolder, final int i) {
        widgetViewHolder.widgetTitle.setText(widgets.get(i).getTitle());
        widgetViewHolder.widgetLastWorkout.setText("Last click: " + lastWorkoutDateBeautiful(widgets.get(i).getLastWorkout()));
        int intervalInDays = widgets.get(i).getIntervalBlue() / MILLISECONDS_IN_A_DAY;
        String text = "Every " + intervalInDays + " day";
        if (intervalInDays>1) {text += "s";}
        widgetViewHolder.widgetIntervalBlue.setText(text);
        widgetViewHolder.widgetPreview.setBackgroundResource(getDrawableIntFromStatus(widgets.get(i).getStatus()));
        widgetViewHolder.widgetPreview.setText(widgetText(widgets.get(i)));
        widgetViewHolder.widgetPreview.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the widget the same way as a click on the widget would.
                WorkoutPixel.updateAfterClick(context, widgets.get(i).getAppWidgetId());
                // This also updates the preferences for the widget. This is used to update the according element in the widget array in this class so that the main view also gets updated immediately.
                widgets.set(i, ManageSavedPreferences.loadWidget(context, widgets.get(i).getAppWidgetId()));
                notifyItemChanged(i);
            }
        });
        widgetViewHolder.widgetEdit.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int appWidgetId = widgets.get(i).getAppWidgetId();
                Intent intent = new Intent(context, WorkoutPixelConfigureActivity.class);
                intent.setAction("APPWIDGET_RECONFIGURE");
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}