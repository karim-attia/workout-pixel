package ch.karimattia.workoutpixel.core

import android.content.Context
import android.util.Log
import android.widget.Toast
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.GoalRepository
import ch.karimattia.workoutpixel.data.PastClick
import ch.karimattia.workoutpixel.data.PastClickRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext

private const val TAG: String = "GoalSaveActions"

class GoalSaveActions @AssistedInject constructor(
	@ApplicationContext val context: Context,
	private val goalRepository: GoalRepository,
	private val pastClickRepository: PastClickRepository,
	private var goalWidgetActionsFactory: GoalWidgetActions.Factory,
	@Assisted var goal: Goal,
) {

	@AssistedFactory
	interface Factory {
		fun create(goal: Goal): GoalSaveActions
	}

	private fun goalWidgetActions(goal: Goal): GoalWidgetActions = goalWidgetActionsFactory.create(goal)

	fun updateAfterClick() {
		Log.d(TAG, "ACTION_DONE_EXERCISE " + goal.debugString() + "start")
		val numberOfPastWorkouts = pastClickRepository.getCountOfActivePastWorkouts(goal.uid) + 1
		Toast.makeText(
			context, "Oh yeah! Already done this " + times(numberOfPastWorkouts) + " :)", Toast.LENGTH_SHORT
		).show()

		// Update the widget data with the latest click
		goal.lastWorkout = System.currentTimeMillis()

		// Instruct the widget manager to update the widget with the latest widget data
		goalWidgetActions(goal).runUpdate(false)

		// Add the workout to the database. Technicalities are taken care of in PastWorkoutsViewModel.
		pastClickRepository.insertPastClick(PastClick(widgetUid = goal.uid, workoutTime = goal.lastWorkout))

		// Update the widget data in the db
		goalRepository.updateGoal(goal)

		Log.d(TAG, "ACTION_DONE_EXERCISE ${goal.debugString()}complete    --------------------------------------------")
	}

	// Can also be called on widget with invalid AppWidgetId
	fun updateWidgetBasedOnStatus() {
		Log.v(TAG, "updateBasedOnStatus: ${goal.debugString()}  --------------------------------------------")

		// Instruct the widget manager to update the widget with the latest widget data
		goalWidgetActions(goal).runUpdate(true)
	}

	// TODO: Understand
	// TODO: Replaced iteration through appWidgetIds with data from DB. Insert check that this is the same and fix if not. Maybe before it only iterated through some widgets. But I don't think it matters.
	// TODO: Could check with CommonFunctions.widgetsWithValidAppWidgetId whether they are the same and at least log if not.
	fun updateAllWidgetsBasedOnStatus() {
		val goalList = goalRepository.loadGoalsWithValidAppWidgetId()
		for (goal in goalList) {
			// Not the most correct way...
			this.goal = goal
			// Tell the AppWidgetManager to perform an update on the current app widget
			updateWidgetBasedOnStatus()
		}
	}
}

