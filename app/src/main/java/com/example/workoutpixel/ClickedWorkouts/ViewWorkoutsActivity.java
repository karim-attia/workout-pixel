package com.example.workoutpixel.ClickedWorkouts;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutpixel.ManageSavedPreferences;
import com.example.workoutpixel.R;

public class ViewWorkoutsActivity extends AppCompatActivity {
    private static final String TAG = "WORKOUT_PIXEL ViewWorkoutsActivity";
    final Context context = ViewWorkoutsActivity.this;

    int appWidgetId ;

    public ViewWorkoutsActivity() {super();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "ON_CREATE");
        super.onCreate(savedInstanceState);

        // Find the widget id  from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finishAndRemoveTask();
            Log.d(TAG, "AppWidgetId invalid.");
            return;
        }

        setContentView(R.layout.view_workouts);
        // Bind views and set them
        TextView title = findViewById(R.id.widget_title);
        title.setText(ManageSavedPreferences.loadTitle(context, appWidgetId));
        TextView date = findViewById(R.id.workout_date);
        date.setTypeface(null, Typeface.BOLD);
        TextView time = findViewById(R.id.workout_time);
        time.setTypeface(null, Typeface.BOLD);
        ImageView delete = findViewById(R.id.workout_delete);
        delete.setImageResource(0);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        PastWorkoutsRecyclerViewAdapter pastWorkoutsRecyclerViewAdapter = new PastWorkoutsRecyclerViewAdapter(context, appWidgetId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(pastWorkoutsRecyclerViewAdapter);

        // Get Viewmodel
        ClickedWorkoutViewModel clickedWorkoutViewModel = new ClickedWorkoutViewModel(getApplication());
        // Create the observer which updates the UI.
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        ClickedWorkoutViewModel.getClickedWorkoutsFromDbByAppWidgetId(context, appWidgetId).observe(this, pastWorkoutsRecyclerViewAdapter::setData);
    }


}

