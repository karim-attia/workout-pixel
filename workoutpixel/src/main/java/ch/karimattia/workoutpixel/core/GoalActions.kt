package ch.karimattia.workoutpixel.core

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import ch.karimattia.workoutpixel.R
import ch.karimattia.workoutpixel.data.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.ceil
import kotlin.math.floor

private const val TAG: String = "GoalActions"

class GoalActions @AssistedInject constructor(
	@ApplicationContext val context: Context,
	private val goalRepository: GoalRepository,
	private val pastClickRepository: PastClickRepository,
	private val settingsRepository: SettingsRepository,
	@Assisted var goal: Goal,
) {

	@AssistedFactory
	interface Factory {
		fun create(goal: Goal): GoalActions
	}

	suspend fun updateAfterClick() {
		Log.d(TAG, "ACTION_DONE_EXERCISE " + goal.debugString() + "start")
		val numberOfPastWorkouts = pastClickRepository.getCountOfActivePastWorkouts(goal.uid) + 1
		Toast.makeText(
			context, "Oh yeah! Already done this $numberOfPastWorkouts ${plural(numberOfPastWorkouts, "time")} :)", Toast.LENGTH_SHORT
		).show()

		// Update the widget data with the latest click
		goal.lastWorkout = System.currentTimeMillis()

		// Instruct the widget manager to update the widget with the latest widget data
		runUpdate(false)

		// Add the workout to the database. Technicalities are taken care of in PastWorkoutsViewModel.
		pastClickRepository.insertPastClick(PastClick(widgetUid = goal.uid, workoutTime = goal.lastWorkout))

		// Update the widget data in the db
		goalRepository.updateGoal(goal)

		Log.d(TAG, "ACTION_DONE_EXERCISE ${goal.debugString()}complete    --------------------------------------------")
	}

	// Can also be called on widget with invalid AppWidgetId
	fun runUpdate(setOnClickListener: Boolean) {
		if (goal.appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
			CoroutineScope(Dispatchers.IO).launch {
				// Make sure to always set both the text and the background of the widget because otherwise it gets updated to some random old version.
				val appWidgetManager = AppWidgetManager.getInstance(context)
				appWidgetManager.updateAppWidget(
					goal.appWidgetId,
					widgetView(setOnClickListener, appWidgetManager)
				)
			}
		} else {
			Log.d(TAG, "runUpdate: appWidgetId == null " + goal.debugString())
		}
	}

	private suspend fun widgetView(setOnClickListener: Boolean, appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)): RemoteViews {
		val widgetView = RemoteViews(context.packageName, R.layout.widget_layout)
		val settingsData: SettingsData = settingsRepository.getSettingsOnce()
		// Set an onClickListener for every widget: https://stackoverflow.com/questions/30174386/multiple-instances-of-widget-with-separated-working-clickable-imageview
		if (setOnClickListener) {
			widgetView.setOnClickPendingIntent(R.id.appwidget_text, widgetPendingIntent())
		}
		// Before updating a widget, the text and background of the view need to be set. Otherwise, the existing not updated properties of the widgetView will be passed.
		widgetView.setTextViewText(R.id.appwidget_text, goal.widgetText(settingsData = settingsData))

		// widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", getDrawableIntFromStatus(goal.status))
		widgetView.setInt(R.id.appwidget_text,
			"setBackgroundColor",
			getColorFromStatus(status = goal.status(), settingsData = settingsData))

		// Set size if available
		// https://stackoverflow.com/questions/25153604/get-the-size-of-my-homescreen-widget
		val height = appWidgetManager.getAppWidgetOptions(goal.appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 44)
		val paddingInDp = height - 44
		val paddingInPx = (paddingInDp * context.resources.displayMetrics.density + 0.5f).toInt()
		val paddigTopInPx = ceil(paddingInPx / 4.0).toInt()
		val paddigBottomInPx = floor(paddingInPx / 4.0 * 3.0).toInt()
		widgetView.setViewPadding(R.id.widget_container, 0, paddigTopInPx, 0, paddigBottomInPx)

		return widgetView
	}

	// Create an Intent to set the action DONE_EXERCISE. This will be received in onReceive.
	private fun widgetPendingIntent(): PendingIntent? {
		val intent = Intent(context, WorkoutPixelAppWidgetProvider::class.java)
		intent.action = Constants.ACTION_DONE_EXERCISE
		// put the appWidgetId as an extra to the update intent
		if (goal.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			Log.d(TAG, "widgetPendingIntent: appWidgetId is null where it shouldn't be.")
			return null
		}
		intent.putExtra(Constants.GOAL_UID, goal.uid)
		return PendingIntent.getBroadcast(context, goal.appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
	}

	// https://developer.android.com/guide/topics/appwidgets/configuration#pin
	// https://developer.android.com/reference/android/appwidget/AppWidgetManager#requestPinAppWidget(android.content.ComponentName,%20android.os.Bundle,%20android.app.PendingIntent)
	fun pinAppWidget() = runBlocking {
		val appWidgetManager = AppWidgetManager.getInstance(context)
		val myProvider = ComponentName(context, WorkoutPixelAppWidgetProvider::class.java)
		Log.d(TAG, "pinAppWidget ${goal.debugString()}")
		if (appWidgetManager.isRequestPinAppWidgetSupported) {
			// Create the PendingIntent object only if your app needs to be notified
			// that the user allowed the widget to be pinned. Note that, if the pinning
			// operation fails, your app isn't notified. This callback receives the ID
			// of the newly-pinned widget (EXTRA_APPWIDGET_ID).
			val pinIntent = Intent(context, WorkoutPixelAppWidgetProvider::class.java)
			pinIntent.action = Constants.ACTION_SETUP_WIDGET
			pinIntent.putExtra(Constants.GOAL_UID, goal.uid)

			val successCallback: PendingIntent = PendingIntent.getBroadcast(
				/* context = */ context,
				/* requestCode = */ 0,
				/* intent = */ pinIntent,
				/* flags = */ PendingIntent.FLAG_UPDATE_CURRENT)

			val bundle = Bundle()
			bundle.putParcelable(AppWidgetManager.EXTRA_APPWIDGET_PREVIEW, widgetView(false))

			appWidgetManager.requestPinAppWidget(myProvider, bundle, successCallback)
		}
	}
}

