package com.example.workoutpixel;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;

import static com.example.workoutpixel.CommonFunctions.STATUS_NONE;
import static com.example.workoutpixel.CommonFunctions.getDrawableIntFromStatus;

public class ViewWorkoutsActivity extends AppCompatActivity {
    private static final String TAG = "WORKOUT_PIXEL ViewWorkoutsActivity";
    final Context context = ViewWorkoutsActivity.this;

    int appWidgetId ;

    public ViewWorkoutsActivity() {super();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "ON_CREATE");
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
            return;
        }

        setContentView(R.layout.view_workouts);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        PastWorkoutsRecyclerViewAdapter pastWorkoutsRecyclerViewAdapter = new PastWorkoutsRecyclerViewAdapter(context);
        recyclerView.setAdapter(pastWorkoutsRecyclerViewAdapter);

        // Bind views

    }
}

