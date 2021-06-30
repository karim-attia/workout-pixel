package ch.karimattia.workoutpixel.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import ch.karimattia.workoutpixel.core.Goal;

class GoalRepository {

    private GoalDao goalDao;
    private LiveData<List<Goal>> allGoals;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    GoalRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        goalDao = db.goalDao();
        allGoals = goalDao.loadAllGoalsLiveData();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<Goal>> getAllGoals() {
        return allGoals;
    }

/*
    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(Word word) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> {
            mWordDao.insert(word);
        });
    }
*/
}
