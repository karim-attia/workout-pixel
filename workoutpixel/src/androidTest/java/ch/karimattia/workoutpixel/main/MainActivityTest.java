package ch.karimattia.workoutpixel.main;

import android.app.Activity;
import android.widget.CheckBox;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import org.junit.Rule;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import ch.karimattia.workoutpixel.R;
import ch.karimattia.workoutpixel.RecyclerViewMatcher;
import ch.karimattia.workoutpixel.TestUtils;
import ch.karimattia.workoutpixel.old.OldMainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@SuppressWarnings("unused")
public class MainActivityTest {
    static final String TAG = "MainActivityTest";

    @Rule
    public ActivityScenarioRule<OldMainActivity> activityScenarioRule = new ActivityScenarioRule<>(OldMainActivity.class);

    @Test
    public void recyclerViewCheck() {
        String string1 = "This is a test.";
        String string2 = "This is not a test.";
        getView(R.id.widget_preview).perform(click());
        getView(R.id.widget_last_workout).check(matches(ViewMatchers.withText(TestUtils.date)));
        getView(R.id.widget_last_workout).check(matches(ViewMatchers.withText(TestUtils.date)));
        getView(R.id.card_view_goal).perform(click());
        getView(R.id.edit_goal).perform(click());
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
        // Another back missing
        getView(R.id.widget_title).check(matches(withText(string1)));
        getView(R.id.card_view_goal).perform(click());
        getView(R.id.edit_goal).perform(click());
        onView(withId(R.id.appwidget_text)).perform(clearText(), typeText(string2));
        onView(withId(R.id.widget_preview)).check(matches(withText(string2)));
        onView(withId(R.id.appwidget_text)).perform(clearText(), typeText(string2));
        onView(isRoot()).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.add_button)).perform(click());
        // Another back missing
        getView(R.id.widget_title).check(matches(withText(string2)));
    }

    @Test
    public void lastWorkout() {
        getView(R.id.widget_preview).perform(click());
        getView(R.id.card_view_goal).perform(click());
        getView(R.id.workout_date).check(matches(ViewMatchers.withText(TestUtils.date)));
        getView(R.id.workout_time).check(matches(ViewMatchers.withText(TestUtils.time)));
    }

    @Test
    public void deletingWorkouts() {
        // Get item count from recyclerView in PastWorkouts Activity.
        int itemCount = 2;

        getView(R.id.card_view_goal).perform(click());
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

        getInstrumentation().runOnMainSync(() -> {
            Collection<Activity> resumedActivity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
            Iterator<Activity> it = resumedActivity.iterator();
            currentActivity[0] = it.next();
        });

        return currentActivity[0];
    }
}

