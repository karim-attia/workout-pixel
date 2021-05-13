package com.example.workoutpixel.Main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workoutpixel.Core.WidgetAlarm;
import com.example.workoutpixel.Database.Goal;
import com.example.workoutpixel.R;

import java.util.List;

import static com.example.workoutpixel.Core.CommonFunctions.cleanGoals;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "WORKOUT_PIXEL_APP";
    private final Context context = MainActivity.this;
    private InteractWithGoalInDb widgetViewModel;
    public boolean instructionsExpanded = false;
    ImageView instructionsExpando;
    TextView instructionsTextView;
    GridLayout instructionsGridLayout;
    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(context);

    ImageView instructions_long_click;
    ImageView instructions_widget_selection;
    ImageView instructions_place_widget;
    ImageView instructions_configure_widget;
    ImageView instructions_widget_created;
    ImageView instructions_widget_clicked;
    ImageView instructions_main_app;

    // onCreate is called when the main app is first loaded.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "------------------------------------------\n                                                                              -------------- MainActivity --------------");
        setContentView(R.layout.activity_main);

        instructionsExpando = findViewById(R.id.instructions_expando_icon);
        instructionsTextView = findViewById(R.id.instructions_text);
        instructionsGridLayout = findViewById(R.id.instructions_grid);

        instructions_long_click = findViewById(R.id.instructions_long_click);
        instructions_widget_selection = findViewById(R.id.instructions_widget_selection);
        instructions_place_widget = findViewById(R.id.instructions_place_widget);
        instructions_configure_widget = findViewById(R.id.instructions_configure_widget);
        instructions_widget_created = findViewById(R.id.instructions_widget_created);
        instructions_widget_clicked = findViewById(R.id.instructions_widget_clicked);
        instructions_main_app = findViewById(R.id.instructions_main_app);

        // toggleExpando();

        instructionsExpando.setOnClickListener(v -> {
            instructionsExpanded = !instructionsExpanded;
            toggleExpando();
            });
        
        setContent();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setContent();
        Log.d(TAG, "on Restart");
    }

        private void toggleExpando() {
        if(instructionsExpanded) {
            instructionsTextView.setVisibility(View.VISIBLE);
            instructionsGridLayout.setVisibility(View.VISIBLE);
            instructionsExpando.setBackgroundResource(R.drawable.icon_expand_less);

            instructions_long_click.setImageResource(R.drawable.instructions_long_click);
            instructions_widget_selection.setImageResource(R.drawable.instructions_widget_selection);
            instructions_place_widget.setImageResource(R.drawable.instructions_place_widget);
            instructions_configure_widget.setImageResource(R.drawable.instructions_configure_widget);
            instructions_widget_created.setImageResource(R.drawable.instructions_widget_created);
            instructions_widget_clicked.setImageResource(R.drawable.instructions_widget_clicked);
            instructions_main_app.setImageResource(R.drawable.instructions_main_app);
        }
        else {
            instructionsTextView.setVisibility(View.GONE);
            instructionsGridLayout.setVisibility(View.GONE);
            instructionsExpando.setBackgroundResource(R.drawable.icon_expand_more);
        }
    }


    // setContent is called when the main app is started or resumed after editing an individual widget.
    // It first loads all widgets into an array. Then it fills the recyclerView from the activity_main Layout with those widgets using the RecyclerViewAdapter.
    private void setContent() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(recyclerViewAdapter);

        widgetViewModel = new ViewModelProvider(this).get(InteractWithGoalInDb.class);

        // Create the observer which updates the UI.
        // Observe the LiveData, passing this activity as the LifecycleOwner and the observer.
        LiveData<List<Goal>> liveData = widgetViewModel.loadAllGoalsLiveData(context.getApplicationContext());
        liveData.observe(this, goals -> {
            recyclerViewAdapter.setData(goals);
            recyclerView.setItemAnimator(null);
            liveData.removeObservers(this);
            executeAfterRecyclerViewWasPopulated(goals);
        });
    }

    private void executeAfterRecyclerViewWasPopulated (List<Goal> goals) {
        if (goals.size() == 0) {
            instructionsExpanded = true;
            toggleExpando();
        }
        WidgetAlarm.startAlarm(context);

        cleanGoals(context, goals);

    }
}