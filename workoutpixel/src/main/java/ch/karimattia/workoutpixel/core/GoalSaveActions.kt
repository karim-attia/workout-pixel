package ch.karimattia.workoutpixel.core

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import ch.karimattia.workoutpixel.R
import ch.karimattia.workoutpixel.database.GoalDao
import ch.karimattia.workoutpixel.database.GoalViewModel
import ch.karimattia.workoutpixel.database.InteractWithPastWorkout
import ch.karimattia.workoutpixel.database.KotlinGoalRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlin.math.ceil
import kotlin.math.floor

class GoalSaveActions @AssistedInject constructor(
	@ApplicationContext val context: Context? = null,
	private val kotlinGoalRepository: KotlinGoalRepository? = null,
	@Assisted var goal: Goal,
) {
	private val TAG: String = this.toString()

	@AssistedFactory
	interface Factory {
		fun create(goal: Goal): GoalSaveActions
	}

	fun updateAfterClick() {
		Log.d(TAG, "ACTION_DONE_EXERCISE " + goal.debugString() + "start")
		val numberOfPastWorkouts = InteractWithPastWorkout.getCountOfActivePastWorkouts(context, goal.getUid()) + 1
		Toast.makeText(
			context, "Oh yeah! Already done this " + CommonFunctions.times(numberOfPastWorkouts) + " :)", Toast.LENGTH_SHORT
		).show()

		// Update the widget data with the latest click
		goal.status = CommonFunctions.STATUS_GREEN
		goal.lastWorkout = System.currentTimeMillis()

		// Instruct the widget manager to update the widget with the latest widget data
		runUpdate(false)

		// Add the workout to the database. Technicalities are taken care of in PastWorkoutsViewModel.
		InteractWithPastWorkout.insertClickedWorkout(context, goal.getUid(), goal.lastWorkout)

		// Update the widget data in the db
		kotlinGoalRepository!!.updateGoal(goal)

		Log.d(TAG, "ACTION_DONE_EXERCISE ${goal.debugString()}complete    --------------------------------------------")
	}

	// Can also be called on widget with invalid AppWidgetId
	fun updateWidgetBasedOnStatus() {
		Log.v(TAG, "updateBasedOnStatus: ${goal.debugString()}  --------------------------------------------")

		// Update the widget data in the db (only) when there is a new status.
		if (goal.setNewStatus()) {
			kotlinGoalRepository!!.updateGoal(goal)
		}

		// Instruct the widget manager to update the widget with the latest widget data
		runUpdate(true)
	}

	// Can also be called on widget with invalid AppWidgetId
	fun runUpdate(setOnClickListener: Boolean) {
		// Make sure to always set both the text and the background of the widget because otherwise it gets updated to some random old version.
		val appWidgetManager = AppWidgetManager.getInstance(context)
		if (goal.appWidgetId != null) {
			appWidgetManager.updateAppWidget(
				goal.appWidgetId!!,
				widgetView(context!!, setOnClickListener, appWidgetManager)
			)
		} else {
			Log.d(TAG, "runUpdate: appWidgetId == null " + goal.debugString())
		}
	}

	private fun widgetView(context: Context, setOnClickListener: Boolean, appWidgetManager: AppWidgetManager): RemoteViews {
		val widgetView = RemoteViews(context.packageName, R.layout.widget_layout)
		// Set an onClickListener for every widget: https://stackoverflow.com/questions/30174386/multiple-instances-of-widget-with-separated-working-clickable-imageview
		if (setOnClickListener) {
			widgetView.setOnClickPendingIntent(R.id.appwidget_text, widgetPendingIntent(context))
		}
		// Before updating a widget, the text and background of the view need to be set. Otherwise, the existing not updated properties of the widgetView will be passed.
		widgetView.setTextViewText(R.id.appwidget_text, widgetText())
		widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", CommonFunctions.getDrawableIntFromStatus(goal.status))
		// Set size if available
		// https://stackoverflow.com/questions/25153604/get-the-size-of-my-homescreen-widget
		if (goal.appWidgetId != null) {
			val height = appWidgetManager.getAppWidgetOptions(goal.appWidgetId!!).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 44)
			val paddingInDp = height - 44
			val paddingInPx = (paddingInDp * context.resources.displayMetrics.density + 0.5f).toInt()
			val paddigTopInPx = ceil(paddingInPx / 4.0).toInt()
			val paddigBottomInPx = floor(paddingInPx / 4.0 * 3.0).toInt()
			widgetView.setViewPadding(R.id.widget_container, 0, paddigTopInPx, 0, paddigBottomInPx)
		}
		return widgetView
	}

	// widgetText returns the text of the whole widget based on a Widget object.
	private fun widgetText(): String {
		var widgetText = goal.title
		if (goal.showDate and (goal.status != CommonFunctions.STATUS_NONE)) {
			widgetText += CommonFunctions.dateBeautiful(goal.lastWorkout)
		}
		if (goal.showTime and (goal.status != CommonFunctions.STATUS_NONE)) {
			widgetText += CommonFunctions.timeBeautiful(goal.lastWorkout)
		}
		return widgetText
	}

	// Create an Intent to set the action DONE_EXERCISE. This will be received in onReceive.
	private fun widgetPendingIntent(context: Context): PendingIntent? {
		val intent = Intent(context, WorkoutPixelAppWidgetProvider::class.java)
		intent.action = WorkoutPixelAppWidgetProvider.ACTION_DONE_EXERCISE
		// put the appWidgetId as an extra to the update intent
		if (goal.appWidgetId == null) {
			Log.d(TAG, "widgetPendingIntent: appWidgetId is null where it shouldn't be.")
			return null
		}
		intent.putExtra("goalUid", goal.uid)
		return PendingIntent.getBroadcast(context, goal.appWidgetId!!, intent, PendingIntent.FLAG_UPDATE_CURRENT)
	}
}