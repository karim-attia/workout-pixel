package ch.karimattia.workoutpixel.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ch.karimattia.workoutpixel.core.Goal;

@Database(entities = {PastWorkout.class, Goal.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    // private static final Object sLock = new Object();
    private static AppDatabase workoutPixelDb;

    // Return database - if there already exists one, return this one, otherwise create one.
    public static AppDatabase getDatabase(Context context) {
        // synchronized (sLock) {
            if (null == workoutPixelDb) {
                workoutPixelDb = buildDatabaseInstance(context);
            }
            return workoutPixelDb;
        }
    // }

    private static AppDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class,
                "WorkoutPixelDatabase")
                .allowMainThreadQueries()
                //.addMigrations(MIGRATION_1_2)
                //.fallbackToDestructiveMigration()
                .build();
    }

    public abstract WorkoutDao workoutDao();

// --Commented out by Inspection START (23.06.21, 20:29):
//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE pastWorkouts ADD widgetUid int;");
//        }
//    };
// --Commented out by Inspection STOP (23.06.21, 20:29)

}
