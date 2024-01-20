package ch.karimattia.workoutpixel.screens

import ch.karimattia.workoutpixel.data.PastClick
import java.util.stream.Collectors

@Suppress("unused")
private const val TAG: String = "ReusablesPastClick"

fun lastClickBasedOnActiveClicks(pastClicks: List<PastClick>): Long {
	val activeWorkoutsOrderedByWorkoutTime =
		pastClicks.stream().filter { clickedClick: PastClick -> clickedClick.isActive }.collect(
			Collectors.toList()
		)

	// If there still is an active past workout, take the latest one to set the last workout time
	return if (activeWorkoutsOrderedByWorkoutTime.isNotEmpty()) activeWorkoutsOrderedByWorkoutTime[0].workoutTime
	// Otherwise, set it to 0.
	else 0L
}