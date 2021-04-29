package com.example.workoutpixel.MainActivity;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutpixel.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "WORKOUT_PIXEL_APP";
    final Context context = MainActivity.this;

    // onCreate is called when the main app is first loaded.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

}