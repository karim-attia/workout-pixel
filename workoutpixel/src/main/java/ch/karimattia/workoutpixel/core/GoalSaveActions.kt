package ch.karimattia.workoutpixel.core

import android.content.Context
import android.util.Log
import android.widget.Toast
import ch.karimattia.workoutpixel.database.KotlinGoalRepository
import ch.karimattia.workoutpixel.database.PastClickRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext

class GoalSaveActions @AssistedInject constructor(
	@ApplicationContext val context: Context,
	private val kotlinGoalRepository: KotlinGoalRepository,
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
		val numberOfPastWorkouts = pastClickRepository.getCountOfActivePastWorkouts(goal.getUid()) + 1
		Toast.makeText(
			context, "Oh yeah! Already done this " + CommonFunctions.times(numberOfPastWorkouts) + " :)", Toast.LENGTH_SHORT
		).show()

		// Update the widget data with the latest click
		goal.status = CommonFunctions.STATUS_GREEN
		goal.lastWorkout = System.currentTimeMillis()

		// Instruct the widget manager to update the widget with the latest widget data
		GoalWidgetActions(context, goal).runUpdate(false)

		// Add the workout to the database. Technicalities are taken care of in PastWorkoutsViewModel.
		pastClickRepository.insertClickedWorkout(goal.getUid(), goal.lastWorkout)

		// Update the widget data in the db
		kotlinGoalRepository.updateGoal(goal)

		Log.d(TAG, "ACTION_DONE_EXERCISE ${goal.debugString()}complete    --------------------------------------------")
	}

	// Can also be called on widget with invalid AppWidgetId
	fun updateWidgetBasedOnStatus() {
		Log.v(TAG, "updateBasedOnStatus: ${goal.debugString()}  --------------------------------------------")

		// Update the widget data in the db (only) when there is a new status.
		if (goal.setNewStatus()) {
			kotlinGoalRepository.updateGoal(goal)
		}

		// Instruct the widget manager to update the widget with the latest widget data
		GoalWidgetActions(context, goal).runUpdate(true)
	}


}

