package com.example.workoutpixel.Main;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.workoutpixel.Core.WidgetAlarm;
import com.example.workoutpixel.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "WORKOUT_PIXEL MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = MainActivity.this;

        setContentView(R.layout.fragment_activity);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // TODO: Better use navigate up? Does it matter?
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        NavController navController = Navigation.findNavController(this, R.id.activity_main_nav_host_fragment);
        navController.setGraph(R.navigation.nav_graph);
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation_view);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        if (InteractWithGoalInDb.getCountOfGoals(context) == 0) {
            navController.navigate(R.id.instructionsFragment);
            navController.popBackStack();
        }

/*
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_nav_host_fragment);
        NavController navController = navHost.getNavController();

        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R.navigation.nav_graph);

        if (InteractWithGoalInDb.getCountOfGoals(context) > 0) {
            graph.setStartDestination(R.id.goalsFragment);
        } else {
            graph.setStartDestination(R.id.instructionsFragment);
        }

        navController.setGraph(graph);

        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation_view);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
*/



        WidgetAlarm.startAlarm(context);
    }
}
