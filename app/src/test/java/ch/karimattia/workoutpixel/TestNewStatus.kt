package ch.karimattia.workoutpixel

import ch.karimattia.workoutpixel.core.Status
import ch.karimattia.workoutpixel.core.daysToMilliseconds
import ch.karimattia.workoutpixel.data.Goal
import org.junit.Assert
import org.junit.Test
import java.util.*

class TestNewStatus {
	private val now = System.currentTimeMillis()

	@Test
	fun testNewStatusGreen() {
		val lastWorkout = now
		val intervalBlue = 2
		val expectedStatus: Status = Status.GREEN
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	@Test
	fun testNewStatusGreenNormal() {
		val lastWorkout = now - daysToMilliseconds(1)
		val intervalBlue = 2
		val expectedStatus: Status = Status.GREEN
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	@Test
	fun testNewStatusBlueNormal() {
		val lastWorkout = now - daysToMilliseconds(3)
		val intervalBlue = 2
		val expectedStatus: Status = Status.BLUE
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	// Corner case
	@Test
	fun testNewStatusBlueCornerCase() {
		val lastWorkout = now - daysToMilliseconds(2)
		val intervalBlue = 2
		val expectedStatus: Status = Status.BLUE
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	@Test
	fun testNewStatusRedNormal() {
		val lastWorkout = now - daysToMilliseconds(5)
		val intervalBlue = 2
		val expectedStatus: Status = Status.RED
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	@Test
	fun testNewStatusRedCornerCase() {
		val lastWorkout = now - daysToMilliseconds(4)
		val intervalBlue = 2
		val expectedStatus: Status = Status.RED
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	@Test
	fun testNewStatusBlueLateLastNight() {
		val today3AmCalendar = Calendar.getInstance()
		today3AmCalendar.timeInMillis = now
		val hourOfDay = today3AmCalendar[Calendar.HOUR_OF_DAY]
		today3AmCalendar[Calendar.HOUR_OF_DAY] = 2
		val today2Am = today3AmCalendar.timeInMillis
		val lastWorkout = today2Am - daysToMilliseconds(0)
		val intervalBlue = 1
		val expectedStatus: Status
		expectedStatus = if (hourOfDay > 3) {
			Status.BLUE
		} else {
			Status.GREEN
		}
		testNewStatus(lastWorkout, intervalBlue, expectedStatus)
	}

	private fun testNewStatus(lastWorkout: Long, intervalBlue: Int, expectedStatus: Status) {
		val newStatus = Goal(lastWorkout = lastWorkout, intervalBlue = intervalBlue).status()
		Assert.assertEquals(expectedStatus, newStatus)
	}
}