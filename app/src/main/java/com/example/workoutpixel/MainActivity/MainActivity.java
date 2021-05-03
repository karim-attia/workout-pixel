package com.example.workoutpixel.MainActivity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutpixel.Core.WidgetFunctions;
import com.example.workoutpixel.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "WORKOUT_PIXEL_APP";
    final Context context = MainActivity.this;

    // onCreate is called when the main app is first loaded.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addWidget = findViewById(R.id.add_button);
        addWidget.setVisibility(View.GONE);
        addWidget.setOnClickListener(v -> {

            Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
            pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 184);
            startActivityForResult(pickIntent, 0);
        });


        setContent();
    }

    // onResume is called when the user returns to the main screen. It is used to reload the card view in case a widget has been updated.
    @Override
    protected void onResume() {
        super.onResume();
        setContent();
    }

    // setContent is called when the main app is started or resumed after editing an individual widget.
    // It first loads all widgets into an array. Then it fills the recyclerView from the activity_main Layout with those widgets using the RecyclerViewAdapter.
    public void setContent() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(context);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Create the observer which updates the UI.
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        // ManageSavedPreferences.loadAllWidgetsLiveData(context).observe(this, recyclerViewAdapter::setData);
    }
}