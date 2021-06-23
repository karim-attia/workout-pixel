package com.karim.workoutpixel.Core;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.karim.workoutpixel.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.karim.workoutpixel.TestUtils.date;
import static com.karim.workoutpixel.TestUtils.time;

public class ConfigureFirstTimeActivityTest {

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), ConfigureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, 2);
        intent.putExtras(bundle);
    }

    @Rule
    public ActivityScenarioRule<Activity> activityScenarioRule = new ActivityScenarioRule<>(intent);

    @Test
    public void checkPreview() {

        onView(withId(R.id.appwidget_text)).perform(typeText("This is a test."));
        onView(withId(R.id.widget_preview)).check(matches(withText("This is a test.")));
        activityScenarioRule.getScenario().onActivity(activity -> {
            CheckBox checkBox = (CheckBox) activity.findViewById(R.id.showDateCheckbox);
            if(checkBox.isChecked()) {onView(withId(R.id.showDateCheckbox)).perform(scrollTo(), click());}
            checkBox = (CheckBox) activity.findViewById(R.id.showTimeCheckbox);
            if(checkBox.isChecked()) {onView(withId(R.id.showTimeCheckbox)).perform(scrollTo(), click());}
        });
        onView(withId(R.id.showDateCheckbox)).perform(scrollTo(), click());
        onView(withId(R.id.widget_preview)).check(matches(withText("This is a test.\n" + date)));
        onView(withId(R.id.showTimeCheckbox)).perform(scrollTo(), click());
        onView(withId(R.id.widget_preview)).check(matches(withText("This is a test.\n" + date + "\n" + time)));
        onView(withId(R.id.showDateCheckbox)).perform(scrollTo(), click());
        onView(withId(R.id.showTimeCheckbox)).perform(scrollTo(), click());
        onView(withId(R.id.widget_preview)).check(matches(withText("This is a test.")));
    }
}