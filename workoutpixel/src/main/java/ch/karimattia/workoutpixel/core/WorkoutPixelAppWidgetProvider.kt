package ch.karimattia.workoutpixel.core

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import ch.karimattia.workoutpixel.core.Constants.ACTION_DONE_EXERCISE
import ch.karimattia.workoutpixel.core.Constants.ACTION_SETUP_WIDGET
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.GoalRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val TAG = "WorkoutPixelAppWidgetProvider"

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [ConfigureActivity][ConfigureActivity::class]
 */
@AndroidEntryPoint
class WorkoutPixelAppWidgetProvider : AppWidgetProvider() {

	@Inject
	lateinit var widgetActionsFactory: WidgetActions.Factory
	private fun goalActions(goal: Goal): WidgetActions = widgetActionsFactory.create(goal)

	@Inject
	lateinit var widgetAlarm: WidgetAlarm

	@Inject
	lateinit var otherActions: OtherActions

	@Inject
	lateinit var repository: GoalRepository

	// Entry point
	override fun onReceive(context: Context, intent: Intent) {
		runBlocking {
			super.onReceive(context, intent)
			Log.d(TAG, "ON_RECEIVE ${intent.action}------------------------------------------------------------------------")
			when {
				// Do this if the widget has been clicked
				// TODO: If goal can't be loaded show error instead of crash
				intent.action.equals(ACTION_DONE_EXERCISE) -> {
					val uid = intent.getIntExtra(Constants.GOAL_UID, 0)
					val goal = repository.loadGoalByUid(uid)
					goalActions(goal).updateAfterClick()
				}
				intent.action.equals(ACTION_SETUP_WIDGET) -> {
					val uid = intent.getIntExtra(Constants.GOAL_UID, 0)
					val goal = repository.loadGoalByUid(uid)
					goal.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
					goalActions(goal).runUpdate(true)
					repository.updateGoal(goal)
				}
				else -> {
					// Do this when the alarm hits
					Log.d(TAG, "intent.action: ${intent.action}")
					saveTimeWithStringToSharedPreferences(context,
						"Last ACTION_ALARM_UPDATE ${dateTimeBeautiful(System.currentTimeMillis())}, intent.action: ${intent.action}")
					otherActions.updateAllWidgets()
				}
			}
		}
	}

	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		runBlocking {
			Log.d(TAG, "ON_UPDATE\n------------------------------------------------------------------------")
			// Start alarm
			widgetAlarm.startAlarm()

			otherActions.updateAllWidgets()
		}
	}

	override fun onDeleted(context: Context, appWidgetIds: IntArray) {
		runBlocking {
			// When the user deletes the widget, delete the preference associated with it.
			Log.d(TAG, "ON_DELETED")

			// TODO: This is also called if the configuration is aborted. Then, this database call is useless.
			for (appWidgetId in appWidgetIds) {
				repository.setAppWidgetIdToNullByAppwidgetId(appWidgetId)
			}
		}
	}

	override fun onEnabled(context: Context) {
		runBlocking {

			// Enter relevant functionality for when the first widget is created
			Log.d(TAG, "ON_ENABLED")
			super.onEnabled(context)

			otherActions.updateAllWidgets()

			// Start alarm
			Log.v(TAG, "START_ALARM")
			widgetAlarm.startAlarm()
			Log.v(TAG, "ALARM_STARTED")
		}
	}

	override fun onDisabled(context: Context) {
		runBlocking {
			// Enter relevant functionality for when the last widget is disabled
			// Stop alarm only if all widgets have been disabled
			Log.d(TAG, "ON_DISABLED")
			if (otherActions.appWidgetIds.isEmpty()) {
				// stop alarm
				Log.v(TAG, "STOP_ALARM")
				widgetAlarm.stopAlarm()
				Log.v(TAG, "STOPPED_ALARM")
			}
		}
	}

	override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, bundle: Bundle) {
		runBlocking {
			val goal = repository.loadGoalByAppWidgetId(appWidgetId)
			goalActions(goal).runUpdate(false)
		}
	}
}