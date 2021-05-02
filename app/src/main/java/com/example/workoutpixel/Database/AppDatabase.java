package com.example.workoutpixel.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {PastWorkout.class, Widget.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final Object sLock = new Object();
    private static AppDatabase pastWorkoutsDb;

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
                "WorkoutPixelDatabase")
                .allowMainThreadQueries()
                .build();
    }

    public abstract WorkoutDao workoutDao();

    public void cleanUp() {
        pastWorkoutsDb = null;
    }

}
