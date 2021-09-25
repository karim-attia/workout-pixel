package ch.karimattia.workoutpixel.core

import android.content.Context
import android.util.Log
import android.widget.Toast
import ch.karimattia.workoutpixel.core.Constants.STATUS_GREEN
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.GoalRepository
import ch.karimattia.workoutpixel.data.PastClickRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext

class GoalSaveActions @AssistedInject constructor(
	@ApplicationContext val context: Context,
	private val goalRepository: GoalRepository,
	private val pastClickRepository: PastClickRepository,
	@Assisted var goal: Goal,
) {
	private val TAG: String = this.toString()

	@AssistedFactory
	interface Factory {
		fun create(goal: Goal): GoalSaveActions
	}

	fun updateAfterClick() {
		Log.d(TAG, "ACTION_DONE_EXERCISE " + goal.debugString() + "start")
		val numberOfPastWorkouts = pastClickRepository.getCountOfActivePastWorkouts(goal.uid) + 1
		Toast.makeText(
			context, "Oh yeah! Already done this " + times(numberOfPastWorkouts) + " :)", Toast.LENGTH_SHORT
		).show()

		// Update the widget data with the latest click
		goal.status = STATUS_GREEN
		goal.lastWorkout = System.currentTimeMillis()

		// Instruct the widget manager to update the widget with the latest widget data
		GoalWidgetActions(context, goal).runUpdate(false)

		// Add the workout to the database. Technicalities are taken care of in PastWorkoutsViewModel.
		pastClickRepository.insertClickedWorkout(goal.uid, goal.lastWorkout)

		// Update the widget data in the db
		goalRepository.updateGoal(goal)

		Log.d(TAG, "ACTION_DONE_EXERCISE ${goal.debugString()}complete    --------------------------------------------")
	}

	// Can also be called on widget with invalid AppWidgetId
	fun updateWidgetBasedOnStatus() {
		Log.v(TAG, "updateBasedOnStatus: ${goal.debugString()}  --------------------------------------------")

		// Update the widget data in the db (only) when there is a new status.
		if (goal.setNewStatus()) {
			goalRepository.updateGoal(goal)
		}

		// Instruct the widget manager to update the widget with the latest widget data
		GoalWidgetActions(context, goal).runUpdate(true)
	}


}

