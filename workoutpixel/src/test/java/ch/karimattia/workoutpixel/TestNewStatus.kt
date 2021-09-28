package ch.karimattia.workoutpixel

import ch.karimattia.workoutpixel.core.Constants.STATUS_BLUE
import ch.karimattia.workoutpixel.core.Constants.STATUS_GREEN
import ch.karimattia.workoutpixel.core.Constants.STATUS_RED
import ch.karimattia.workoutpixel.core.intervalInMilliseconds
import ch.karimattia.workoutpixel.core.getNewStatus
import org.junit.Assert
import org.junit.Test
import java.util.*

class TestNewStatus {
	private val now = System.currentTimeMillis()
	@Test
	fun testNewStatusGreen() {
		val lastWorkout = now
		val intervalBlue = 2
		val expectedStatus: String = STATUS_GREEN
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	@Test
	fun testNewStatusGreenNormal() {
		val lastWorkout = now - intervalInMilliseconds(1)
		val intervalBlue = 2
		val expectedStatus: String = STATUS_GREEN
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	@Test
	fun testNewStatusBlueNormal() {
		val lastWorkout = now - intervalInMilliseconds(3)
		val intervalBlue = 2
		val expectedStatus: String = STATUS_BLUE
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	// Corner case
	@Test
	fun testNewStatusBlueCornerCase() {
		val lastWorkout = now - intervalInMilliseconds(2)
		val intervalBlue = 2
		val expectedStatus: String = STATUS_BLUE
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	@Test
	fun testNewStatusRedNormal() {
		val lastWorkout = now - intervalInMilliseconds(5)
		val intervalBlue = 2
		val expectedStatus: String = STATUS_RED
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	@Test
	fun testNewStatusRedCornerCase() {
		val lastWorkout = now - intervalInMilliseconds(4)
		val intervalBlue = 2
		val expectedStatus: String = STATUS_RED
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	@Test
	fun testNewStatusBlueLateLastNight() {
		val today3AmCalendar = Calendar.getInstance()
		today3AmCalendar.timeInMillis = now
		val hourOfDay = today3AmCalendar[Calendar.HOUR_OF_DAY]
		today3AmCalendar[Calendar.HOUR_OF_DAY] = 2
		val today2Am = today3AmCalendar.timeInMillis
		val lastWorkout = today2Am - intervalInMilliseconds(0)
		val intervalBlue = 1
		val expectedStatus: String
		if (hourOfDay > 3) {
			expectedStatus = STATUS_BLUE
		} else {
			expectedStatus = STATUS_GREEN
		}
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	private fun testNewStatus(lastWorkout: Long, intervalBlue: Int, expectedStatus: String?) {
		val newStatus = getNewStatus(lastWorkout, intervalBlue)
		Assert.assertEquals(expectedStatus, newStatus)
	}
}