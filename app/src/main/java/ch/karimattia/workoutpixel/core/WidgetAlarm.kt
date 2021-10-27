package ch.karimattia.workoutpixel.core

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import ch.karimattia.workoutpixel.core.Constants.ACTION_ALARM_UPDATE
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DateFormat
import javax.inject.Inject

/**
 * Responsible to start and stop the alarm that updates the widget at 3:00 every day.
 * The alarm is started when the first widget is created (by WorkoutPixel.onCreate()) or the device restarts (by WorkoutPixel.onUpdate()).
 * WorkoutPixel.onUpdate() may not only be called upon device restarts but also in other cases. Check the documentation for that.
 */

private const val TAG: String = "WidgetAlarm"

class WidgetAlarm @Inject constructor(
	@ApplicationContext val context: Context,
) {
	fun startAlarm() {
		saveTimeWithStringToSharedPreferences(
			context,
			"WidgetAlarm startAlarm " + dateTimeBeautiful(System.currentTimeMillis())
		)

		// RTC does not wake the device up
		alarmManager.setInexactRepeating(AlarmManager.RTC, next3Am(), AlarmManager.INTERVAL_DAY, pendingIntent(context, ACTION_ALARM_UPDATE))
		Log.d(TAG, "ALARM STARTED " + dateTimeString(next3Am()))
	}

	fun stopAlarm() {
		saveTimeWithStringToSharedPreferences(
			context,
			"WidgetAlarm stopAlarm " + dateTimeBeautiful(System.currentTimeMillis())
		)
		alarmManager.cancel(pendingIntent(context, ACTION_ALARM_UPDATE))
		Log.d(TAG, "ALARM STOPPED")
	}

	// Just for log. For anything else, use the functions in CommonFunctions
	private fun dateTimeString(timeInMillis: Long): String = DateFormat.getDateTimeInstance().format(timeInMillis)

	private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

	private fun pendingIntent(context: Context, action: String): PendingIntent {
		val alarmIntent = Intent(context, WorkoutPixelAppWidgetProvider::class.java)
		alarmIntent.action = action
		return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
	}
}