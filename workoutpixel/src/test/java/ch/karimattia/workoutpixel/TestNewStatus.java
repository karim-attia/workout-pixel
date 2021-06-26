package ch.karimattia.workoutpixel;

import org.testng.annotations.Test;

import java.util.Calendar;

import ch.karimattia.workoutpixel.core.CommonFunctions;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("UnnecessaryLocalVariable")
public class TestNewStatus {
    final long now = System.currentTimeMillis();

    @Test
    public void testNewStatusGreen () {
        long lastWorkout = now;
        int intervalBlue = 2;
        String expectedStatus = CommonFunctions.STATUS_GREEN;
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    @Test
    public void testNewStatusGreenNormal () {
        long lastWorkout = now - CommonFunctions.intervalInMilliseconds(1);
        int intervalBlue = 2;
        String expectedStatus = CommonFunctions.STATUS_GREEN;
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    @Test
    public void testNewStatusBlueNormal () {
        long lastWorkout = now - CommonFunctions.intervalInMilliseconds(3);
        int intervalBlue = 2;
        String expectedStatus = CommonFunctions.STATUS_BLUE;
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    // Corner case
    @Test
    public void testNewStatusBlueCornerCase () {
        long lastWorkout = now - CommonFunctions.intervalInMilliseconds(2);
        int intervalBlue = 2;
        String expectedStatus = CommonFunctions.STATUS_BLUE;
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    @Test
    public void testNewStatusRedNormal () {
        long lastWorkout = now - CommonFunctions.intervalInMilliseconds(5);
        int intervalBlue = 2;
        String expectedStatus = CommonFunctions.STATUS_RED;
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    @Test
    public void testNewStatusRedCornerCase () {
        long lastWorkout = now - CommonFunctions.intervalInMilliseconds(4);
        int intervalBlue = 2;
        String expectedStatus = CommonFunctions.STATUS_RED;
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    @Test
    public void testNewStatusBlueLateLastNight () {
        Calendar today3AmCalendar = Calendar.getInstance();
        today3AmCalendar.setTimeInMillis(now);
        int hourOfDay = today3AmCalendar.get(Calendar.HOUR_OF_DAY);
        today3AmCalendar.set(Calendar.HOUR_OF_DAY, 2);
        long today2Am = today3AmCalendar.getTimeInMillis();

        long lastWorkout = today2Am - CommonFunctions.intervalInMilliseconds(0);
        int intervalBlue = 1;
        String expectedStatus;
        if(hourOfDay > 3) {expectedStatus = CommonFunctions.STATUS_BLUE;}
        else {expectedStatus = CommonFunctions.STATUS_GREEN;}
        testNewStatus(lastWorkout, intervalBlue, expectedStatus);
    }

    public void testNewStatus(long lastWorkout, int intervalBlue, String expectedStatus) {
        String newStatus = CommonFunctions.getNewStatus(lastWorkout, intervalBlue);
        assertEquals(expectedStatus, newStatus);
    }


}