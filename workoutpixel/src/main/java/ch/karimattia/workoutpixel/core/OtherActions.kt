package ch.karimattia.workoutpixel.core

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.GoalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject

private const val TAG: String = "OtherActions"

class OtherActions @Inject constructor(
	@ApplicationContext val context: Context,
	private val goalRepository: GoalRepository,
) {
	@Inject
	lateinit var goalActionsFactory: GoalActions.Factory
	private fun goalActions(goal: Goal): GoalActions = goalActionsFactory.create(goal)

	// TODO: Understand
	// TODO: Replaced iteration through appWidgetIds with data from DB. Insert check that this is the same and fix if not. Maybe before it only iterated through some widgets. But I don't think it matters.
	// TODO: Could check with CommonFunctions.widgetsWithValidAppWidgetId whether they are the same and at least log if not.
	// TODO: Find different home for this.
	suspend fun updateAllWidgets() {
		Log.d(TAG, "updateAllWidgets  --------------------------------------------")
		val goalList = goalRepository.loadGoalsWithValidAppWidgetId()
		for (goal in goalList) {
			// Tell the AppWidgetManager to perform an update on the current app widget
			// Instruct the widget manager to update the widget with the latest widget data
			goalActions(goal).runUpdate(true)
		}
	}

	// @Inject lateinit var contextFunctions: ContextFunctions
	// Sets all appWidgetIds of goals that are not valid to null. Maybe later even reassign some to unassigned widgets.
	suspend fun cleanGoals(goals: List<Goal>) {
		for (goal in goalsWithInvalidAppWidgetId(goals)) {
			if (goal.hasValidAppWidgetId()) {
				goalRepository.setAppWidgetIdToNullByUid(goal.uid)
			}
		}
	}

	/**
	 * AppWidgetId stuff
	 */
	fun appWidgetIds(): IntArray =
		AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context.packageName, WorkoutPixelAppWidgetProvider::class.java.name))

	fun goalsWithInvalidAppWidgetId(goals: List<Goal>): List<Goal> {
		return goals.stream().filter { (_, _) ->
			Arrays.stream(appWidgetIds()).noneMatch { appWidgetId: Int -> appWidgetId == appWidgetId }
		}
			.collect(Collectors.toList())
	}

	@Suppress("unused")
	fun goalsWithInvalidOrNullAppWidgetId(goals: List<Goal>): List<Goal> {
		return goals.stream()
			.filter { (_, appWidgetId) ->
				Arrays.stream(appWidgetIds()).noneMatch { i: Int -> appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && i == appWidgetId }
			}
			.collect(Collectors.toList())
	}
}