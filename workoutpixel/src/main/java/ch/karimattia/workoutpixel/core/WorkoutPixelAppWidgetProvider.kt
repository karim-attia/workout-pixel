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
class WorkoutPixelAppWidgetProvider : AppWidgetProvider() {
	private val tag = "WorkoutPixelAppWidgetProvider"

	@Inject
	lateinit var goalSaveActionsFactory: GoalSaveActions.Factory
	private fun goalSaveActions(goal: Goal): GoalSaveActions = goalSaveActionsFactory.create(goal)

	@Inject
	lateinit var goalWidgetActionsFactory: GoalWidgetActions.Factory
	private fun goalWidgetActions(goal: Goal): GoalWidgetActions = goalWidgetActionsFactory.create(goal)

	@Inject
	lateinit var widgetAlarm: WidgetAlarm

	@Inject
	lateinit var contextFunctions: ContextFunctions

	@Inject
	lateinit var repository: GoalRepository

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
			saveTimeWithStringToSharedPreferences(context, "Last ACTION_ALARM_UPDATE " + dateTimeBeautiful(System.currentTimeMillis()))
			// Not the most correct way...
			goalSaveActions(Goal()).updateAllWidgetsBasedOnStatus()
		}
	}

	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

		//CommonFunctions.executorService.execute(() -> {
		Log.d(tag, "ON_UPDATE\n------------------------------------------------------------------------")
		// Start alarm
		widgetAlarm.startAlarm()

		// Not the most correct way...
		goalSaveActions(Goal()).updateAllWidgetsBasedOnStatus()

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

		// Not the most correct way...
		goalSaveActions(Goal()).updateAllWidgetsBasedOnStatus()

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
		goalWidgetActions(goal).runUpdate(false)
	}
}