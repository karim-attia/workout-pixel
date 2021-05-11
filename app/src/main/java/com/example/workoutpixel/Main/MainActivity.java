package com.example.workoutpixel.Main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutpixel.Database.Widget;
import com.example.workoutpixel.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "WORKOUT_PIXEL_APP";
    private final Context context = MainActivity.this;

    // onCreate is called when the main app is first loaded.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "------------------------------------------\n                                                                              -------------- MainActivity --------------");
        setContentView(R.layout.activity_main);
        setContent();
    }

    // setContent is called when the main app is started or resumed after editing an individual widget.
    // It first loads all widgets into an array. Then it fills the recyclerView from the activity_main Layout with those widgets using the RecyclerViewAdapter.
    public void setContent() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(context);
        recyclerView.setAdapter(recyclerViewAdapter);


        InteractWithWidgetInDb interactWithWidgetInDb = new InteractWithWidgetInDb(this.getApplication());
        // Create the observer which updates the UI.
        // Observe the LiveData, passing this activity as the LifecycleOwner and the observer.
        LiveData<List<Widget>> liveData = interactWithWidgetInDb.loadAllWidgetsLiveData(context.getApplicationContext());
        liveData.observe(this, widgets -> {
            Log.d(TAG, "has observers " + liveData.hasActiveObservers());
            recyclerViewAdapter.setData(widgets);
            recyclerView.setItemAnimator(null);
            liveData.removeObservers(this);
            Log.d(TAG, "has observers " + liveData.hasActiveObservers());
        });
    }
}