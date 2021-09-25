package ch.karimattia.workoutpixel;

import static org.junit.Assert.assertEquals;
import static ch.karimattia.workoutpixel.core.CommonFunctionsKt.getNewStatus;
import static ch.karimattia.workoutpixel.core.CommonFunctionsKt.intervalInMilliseconds;
import static ch.karimattia.workoutpixel.core.Constants.STATUS_BLUE;
import static ch.karimattia.workoutpixel.core.Constants.STATUS_GREEN;
import static ch.karimattia.workoutpixel.core.Constants.STATUS_RED;

import org.testng.annotations.Test;

import java.util.Calendar;

@SuppressWarnings("UnnecessaryLocalVariable")
public class TestNewStatus {
    final long now = System.currentTimeMillis();

    @Test
    public void testNewStatusGreen() {
        long lastWorkout = now;
        int intervalBlue = 2;
        String expectedStatus = STATUS_GREEN;
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    @Test
    public void testNewStatusGreenNormal() {
        long lastWorkout = now - intervalInMilliseconds(1);
        int intervalBlue = 2;
        String expectedStatus = STATUS_GREEN;
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    @Test
    public void testNewStatusBlueNormal() {
        long lastWorkout = now - intervalInMilliseconds(3);
        int intervalBlue = 2;
        String expectedStatus = STATUS_BLUE;
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    // Corner case
    @Test
    public void testNewStatusBlueCornerCase() {
        long lastWorkout = now - intervalInMilliseconds(2);
        int intervalBlue = 2;
        String expectedStatus = STATUS_BLUE;
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    @Test
    public void testNewStatusRedNormal() {
        long lastWorkout = now - intervalInMilliseconds(5);
        int intervalBlue = 2;
        String expectedStatus = STATUS_RED;
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    @Test
    public void testNewStatusRedCornerCase() {
        long lastWorkout = now - intervalInMilliseconds(4);
        int intervalBlue = 2;
        String expectedStatus = STATUS_RED;
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    @Test
    public void testNewStatusBlueLateLastNight() {
        Calendar today3AmCalendar = Calendar.getInstance();
        today3AmCalendar.setTimeInMillis(now);
        int hourOfDay = today3AmCalendar.get(Calendar.HOUR_OF_DAY);
        today3AmCalendar.set(Calendar.HOUR_OF_DAY, 2);
        long today2Am = today3AmCalendar.getTimeInMillis();

        long lastWorkout = today2Am - intervalInMilliseconds(0);
        int intervalBlue = 1;
        String expectedStatus;
        if (hourOfDay > 3) {
            expectedStatus = STATUS_BLUE;
        } else {
            expectedStatus = STATUS_GREEN;
        }
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    public void testNewStatus(long lastWorkout, int intervalBlue, String expectedStatus) {
        String newStatus = getNewStatus(lastWorkout, intervalBlue);
        assertEquals(expectedStatus, newStatus);
    }


}