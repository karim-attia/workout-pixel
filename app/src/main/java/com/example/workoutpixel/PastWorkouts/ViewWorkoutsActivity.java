package com.example.workoutpixel.PastWorkouts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutpixel.Database.PastWorkout;
import com.example.workoutpixel.Database.Goal;
import com.example.workoutpixel.Main.InteractWithWidgetInDb;
import com.example.workoutpixel.R;

import java.util.List;

public class ViewWorkoutsActivity extends AppCompatActivity {
    private static final String TAG = "WORKOUT_PIXEL ViewWorkoutsActivity";
    final Context context = ViewWorkoutsActivity.this;

    int uid;

    public ViewWorkoutsActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ON_CREATE");

        // Find the widget id  from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            uid = extras.getInt("widgetUid", 0);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (uid == 0) {
            finishAndRemoveTask();
            Log.d(TAG, "uid invalid.");
            return;
        }

        Goal goal = InteractWithWidgetInDb.loadWidgetByUid(context, uid);
        setContentView(R.layout.view_workouts);

        // Bind views and set them
        TextView title = findViewById(R.id.widget_title);
        title.setText(goal.getTitle());
        // ManageSavedPreferences.loadTitleByAppWidgetId(context, appWidgetId).observe(this, title::setText);
        TextView date = findViewById(R.id.workout_date);
        date.setTypeface(null, Typeface.BOLD);
        TextView time = findViewById(R.id.workout_time);
        time.setTypeface(null, Typeface.BOLD);
        ImageView delete = findViewById(R.id.workout_delete);
        delete.setImageResource(0);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        PastWorkoutsRecyclerViewAdapter pastWorkoutsRecyclerViewAdapter = new PastWorkoutsRecyclerViewAdapter(context, goal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(pastWorkoutsRecyclerViewAdapter);

        // Create the observer which updates the UI.
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        LiveData<List<PastWorkout>> liveData = InteractWithPastWorkout.getPastWorkouts(context.getApplicationContext(), goal.uid);
        InteractWithPastWorkout.getPastWorkouts(context, goal.uid).observe(this, pastWorkouts -> {
            pastWorkoutsRecyclerViewAdapter.setData(pastWorkouts);
            liveData.removeObservers(this);
        });
    }
}

