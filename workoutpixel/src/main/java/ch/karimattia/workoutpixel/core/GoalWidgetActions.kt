package ch.karimattia.workoutpixel.core

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import ch.karimattia.workoutpixel.R
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData
import ch.karimattia.workoutpixel.data.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor

private const val TAG: String = "GoalWidgetActions"

class GoalWidgetActions @AssistedInject constructor(
	@ApplicationContext val context: Context,
	private val settingsRepository: SettingsRepository,
	@Assisted var goal: Goal,
) {
	@AssistedFactory
	interface Factory {
		fun create(goal: Goal): GoalWidgetActions
	}

	// Can also be called on widget with invalid AppWidgetId
	fun runUpdate(setOnClickListener: Boolean) {
		if (goal.appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
			CoroutineScope(Dispatchers.IO).launch {
				// Make sure to always set both the text and the background of the widget because otherwise it gets updated to some random old version.
				val appWidgetManager = AppWidgetManager.getInstance(context)
				appWidgetManager.updateAppWidget(
					goal.appWidgetId,
					widgetView(context, setOnClickListener, appWidgetManager)
				)
			}
		} else {
			Log.d(TAG, "runUpdate: appWidgetId == null " + goal.debugString())
		}
	}


	private suspend fun widgetView(context: Context, setOnClickListener: Boolean, appWidgetManager: AppWidgetManager): RemoteViews {
		val widgetView = RemoteViews(context.packageName, R.layout.widget_layout)
		val settingsData: SettingsData = settingsRepository.getSettingsOnce()
		// Set an onClickListener for every widget: https://stackoverflow.com/questions/30174386/multiple-instances-of-widget-with-separated-working-clickable-imageview
		if (setOnClickListener) {
			widgetView.setOnClickPendingIntent(R.id.appwidget_text, widgetPendingIntent(context))
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
	private fun widgetPendingIntent(context: Context): PendingIntent? {
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
}