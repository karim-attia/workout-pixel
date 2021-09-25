package ch.karimattia.workoutpixel.core

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import ch.karimattia.workoutpixel.core.Constants.ACTION_ALARM_UPDATE
import ch.karimattia.workoutpixel.core.Constants.ACTION_DONE_EXERCISE
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.GoalRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [ConfigureActivity][ConfigureActivity::class]
 */
@AndroidEntryPoint
class WorkoutPixelAppWidgetProvider @Inject constructor(
	private val repository: GoalRepository
) : AppWidgetProvider() {
	private val tag = "WorkoutPixelAppWidgetProvider"

	@Inject
	lateinit var goalSaveActionsFactory: GoalSaveActions.Factory
	private fun goalSaveActions(goal: Goal): GoalSaveActions {
		return goalSaveActionsFactory.create(goal)
	}
	@Inject	lateinit var widgetAlarm: WidgetAlarm
	@Inject	lateinit var contextFunctions: ContextFunctions

	// Entry point
	override fun onReceive(context: Context, intent: Intent) {
		super.onReceive(context, intent)
		Log.d(tag, "ON_RECEIVE ${intent.action}------------------------------------------------------------------------")

		// Do this if the widget has been clicked
		if (ACTION_DONE_EXERCISE == intent.action) {
			val uid = intent.getIntExtra("goalUid", 0)
			val goal = repository.loadGoalByUid(uid)
			goalSaveActions(goal).updateAfterClick()
		}

		// Do this when the alarm hits
		if (ACTION_ALARM_UPDATE == intent.action) {
			saveTimeWithStringToSharedPreferences(
				context, "Last ACTION_ALARM_UPDATE " + dateTimeBeautiful(
					System.currentTimeMillis()
				)
			)
			val goalList = repository.loadAllGoals()
			for (goal in goalList) {
				goalSaveActions(goal).updateWidgetBasedOnStatus()
			}
		}
	}

	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

		//CommonFunctions.executorService.execute(() -> {
		Log.d(tag, "ON_UPDATE\n------------------------------------------------------------------------")
		// Start alarm
		widgetAlarm.startAlarm()

		// TODO: Understand
		// TODO: Replaced iteration through appWidgetIds with data from DB. Insert check that this is the same and fix if not. Maybe before it only iterated through some widgets. But I don't think it matters.
		// TODO: Could check with CommonFunctions.widgetsWithValidAppWidgetId whether they are the same and at least log if not.
		val goalList = repository.loadGoalsWithValidAppWidgetId()
		for (goal in goalList) {
			// Tell the AppWidgetManager to perform an update on the current app widget
			Log.d(tag, "ON_UPDATE: " + goal.debugString())
			goalSaveActions(goal).updateWidgetBasedOnStatus()
		}

		// CommonFunctions.executorService.shutdown();

		//});
	}

	override fun onDeleted(context: Context, appWidgetIds: IntArray) {
		// When the user deletes the widget, delete the preference associated with it.
		Log.d(tag, "ON_DELETED")

		// TODO: This is also called if the configuration is aborted. Then, this database call is useless.
		for (appWidgetId in appWidgetIds) {
			repository.setAppWidgetIdToNullByAppwidgetId(appWidgetId)
		}
	}

	override fun onEnabled(context: Context) {
		// Enter relevant functionality for when the first widget is created
		Log.d(tag, "ON_ENABLED")
		super.onEnabled(context)
		val goalList = repository.loadGoalsWithValidAppWidgetId()
		for (goal in goalList) {
			// Tell the AppWidgetManager to perform an update on the current app widget
			Log.d(tag, "ON_ENABLED: " + goal.debugString())
			goalSaveActions(goal).updateWidgetBasedOnStatus()
		}

		// Start alarm
		Log.v(tag, "START_ALARM")
		widgetAlarm.startAlarm()
		Log.v(tag, "ALARM_STARTED")
	}

	override fun onDisabled(context: Context) {
		// Enter relevant functionality for when the last widget is disabled
		// Stop alarm only if all widgets have been disabled
		Log.d(tag, "ON_DISABLED")
		if (contextFunctions.appWidgetIds().isEmpty()) {
			// stop alarm
			Log.v(tag, "STOP_ALARM")
			widgetAlarm.stopAlarm()
			Log.v(tag, "STOPPED_ALARM")
		}
	}

	// Entry point
	override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, bundle: Bundle) {
		val goal = repository.loadGoalByAppWidgetId(appWidgetId)
		GoalWidgetActions(context, goal).runUpdate(false)
	}
}