package ch.karimattia.workoutpixel.core

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import ch.karimattia.workoutpixel.R
import ch.karimattia.workoutpixel.data.Goal
import kotlin.math.ceil
import kotlin.math.floor

private const val TAG: String = "GoalWidgetActions"

class GoalWidgetActions(
	val context: Context,
	var goal: Goal,
) {
	// Can also be called on widget with invalid AppWidgetId
	fun runUpdate(setOnClickListener: Boolean) {
		// Make sure to always set both the text and the background of the widget because otherwise it gets updated to some random old version.
		val appWidgetManager = AppWidgetManager.getInstance(context)
		if (goal.appWidgetId != null) {
			appWidgetManager.updateAppWidget(
				goal.appWidgetId!!,
				widgetView(context, setOnClickListener, appWidgetManager)
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
		widgetView.setTextViewText(R.id.appwidget_text, goal.widgetText())
		widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", getDrawableIntFromStatus(goal.status))
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

	// Create an Intent to set the action DONE_EXERCISE. This will be received in onReceive.
	private fun widgetPendingIntent(context: Context): PendingIntent? {
		val intent = Intent(context, WorkoutPixelAppWidgetProvider::class.java)
		intent.action = Constants.ACTION_DONE_EXERCISE
		// put the appWidgetId as an extra to the update intent
		if (goal.appWidgetId == null) {
			Log.d(TAG, "widgetPendingIntent: appWidgetId is null where it shouldn't be.")
			return null
		}
		intent.putExtra("goalUid", goal.uid)
		return PendingIntent.getBroadcast(context, goal.appWidgetId!!, intent, PendingIntent.FLAG_UPDATE_CURRENT)
	}
}