package com.example.workoutpixel.Main;

import android.app.Activity;
import android.util.Log;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.example.workoutpixel.R;
import com.example.workoutpixel.RecyclerViewMatcher;

import org.junit.Rule;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.example.workoutpixel.TestUtils.date;
import static com.example.workoutpixel.TestUtils.time;

public class MainActivityTest {
    static final String TAG = "MainActivityTest";
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void expandoClick() {
        RecyclerView recyclerView = getActivityInstance().findViewById(R.id.recycler_view);
        int itemCount = recyclerView.getAdapter().getItemCount();
        Log.d(TAG, "itemCount: " + itemCount);
        if (itemCount > 0) {
            Log.d(TAG, "itemCount > 0");
            // Click should be here
        }

        // Only passes if there are widgets.
        onView(withId(R.id.instructions_expando_icon)).perform(click());
        onView(withId(R.id.instructions_text)).check(matches(isDisplayed()));


    }

    @Test
    public void recyclerViewCheck() {
        String string1 = "This is a test.";
        String string2 = "This is not a test.";
        getView(R.id.widget_preview).perform(click());
        getView(R.id.widget_last_workout).check(matches(withText(date)));
        getView(R.id.widget_last_workout).check(matches(withText(date)));
        getView(R.id.widget_edit_text).perform(click());
        CheckBox checkBox = getActivityInstance().findViewById(R.id.showDateCheckbox);
        if (checkBox.isChecked()) {
            onView(withId(R.id.showDateCheckbox)).perform(scrollTo(), click());
        }
        checkBox = getActivityInstance().findViewById(R.id.showTimeCheckbox);
        if (checkBox.isChecked()) {
            onView(withId(R.id.showTimeCheckbox)).perform(scrollTo(), click());
        }
        onView(withId(R.id.appwidget_text)).perform(clearText(), typeText(string1));
        onView(withId(R.id.widget_preview)).check(matches(withText(string1)));
        onView(withId(R.id.appwidget_text)).perform(clearText(), typeText(string1));
        onView(isRoot()).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.add_button)).perform(click());
        getView(R.id.widget_title).check(matches(withText(string1)));
        getView(R.id.widget_edit_text).perform(click());
        onView(withId(R.id.appwidget_text)).perform(clearText(), typeText(string2));
        onView(withId(R.id.widget_preview)).check(matches(withText(string2)));
        onView(withId(R.id.appwidget_text)).perform(clearText(), typeText(string2));
        onView(isRoot()).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.add_button)).perform(click());
        getView(R.id.widget_title).check(matches(withText(string2)));
    }

    @Test
    public void lastWorkout() {
        getView(R.id.widget_preview).perform(click());
        getView(R.id.widget_past_workout_text).perform(click());
        getView(R.id.workout_date).check(matches(withText(date)));
        getView(R.id.workout_time).check(matches(withText(time)));
    }

    @Test
    public void deletingWorkouts() {
        // Get item count from recyclerView in PastWorkouts Activity.
        int itemCount = 2;

        getView(R.id.widget_past_workout_text).perform(click());
        for (int i = 0; i < itemCount; i++) {
            getView(R.id.workout_delete, i).perform(click());
        }

        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        getView(R.id.widget_last_workout).check(matches(withText("Never")));
    }


    ViewInteraction getView(int view) {
        return getView(view, 0);
    }

    ViewInteraction getView(int view, int position) {
        return onView(new RecyclerViewMatcher(R.id.recycler_view).atPositionOnView(position, view));
    }

    private Activity getActivityInstance() {
        final Activity[] currentActivity = {null};

        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                Iterator<Activity> it = resumedActivity.iterator();
                currentActivity[0] = it.next();
            }
        });

        return currentActivity[0];
    }
}
