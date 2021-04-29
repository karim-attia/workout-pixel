package com.example.workoutpixel;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ClickedWorkout.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WorkoutDao workoutDao();

    private static AppDatabase pastWorkoutsDb;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static final Object sLock = new Object();

    // Return database - if there already exists one, return this one, otherwise create one.
    public static AppDatabase getDatabase(Context context) {
        synchronized (sLock) {
            if (null == pastWorkoutsDb) {
                pastWorkoutsDb = buildDatabaseInstance(context);
            }
            return pastWorkoutsDb;
        }
    }

    private static AppDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class,
                "clickedWorkout")
                .build();
    }

    public void cleanUp(){
        pastWorkoutsDb = null;
    }

}
