package ch.karimattia.workoutpixel.database;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MyViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private int goalUid;

    public MyViewModelFactory(Application application, int goalUid) {
        this.application = application;
        this.goalUid = goalUid;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new PastClickViewModel(application, goalUid);
    }
}
