package ch.karimattia.workoutpixel.old;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ch.karimattia.workoutpixel.core.Goal;
import ch.karimattia.workoutpixel.data.GoalDao;
import ch.karimattia.workoutpixel.data.PastWorkout;

@Database(entities = {PastWorkout.class, Goal.class}, version = 2)
public abstract class OldAppDatabase extends RoomDatabase {
    // private static final Object sLock = new Object();
    private static OldAppDatabase workoutPixelDb;

    // Return database - if there already exists one, return this one, otherwise create one.
    public static OldAppDatabase getDatabase(Context context) {
        // synchronized (sLock) {
            if (null == workoutPixelDb) {
                workoutPixelDb = buildDatabaseInstance(context);
            }
            return workoutPixelDb;
        }
    // }

    private static OldAppDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                OldAppDatabase.class,
                "WorkoutPixelDatabase")
                .allowMainThreadQueries()
                //.addMigrations(MIGRATION_1_2)
                //.fallbackToDestructiveMigration()
                .build();
    }

    public abstract GoalDao goalDao();

// --Commented out by Inspection START (23.06.21, 20:29):
//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE pastWorkouts ADD widgetUid int;");
//        }
//    };
// --Commented out by Inspection STOP (23.06.21, 20:29)

}
