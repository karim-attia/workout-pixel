package ch.karimattia.workoutpixel.core

import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.WidgetAlarm
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import ch.karimattia.workoutpixel.core.WorkoutPixelAppWidgetProvider
import ch.karimattia.workoutpixel.data.GoalRepository
import ch.karimattia.workoutpixel.data.PastClickRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DateFormat
import javax.inject.Inject

// Responsible to start and stop the alarm that updates the widget at 3:00 every day.
// The alarm is started when the first widget is created (by WorkoutPixel.onCreate()) or the device restarts (by WorkoutPixel.onUpdate()).
// WorkoutPixel.onUpdate() may not only be called upon device restarts but also in other cases. Check the documentation for that.

class WidgetAlarm @Inject constructor(
	@ApplicationContext val context: Context,
	) {
	private val tag = this.toString()
	fun startAlarm() {
		CommonFunctions.saveTimeWithStringToSharedPreferences(
			context,
			"WidgetAlarm startAlarm " + CommonFunctions.dateTimeBeautiful(System.currentTimeMillis())
		)

		// RTC does not wake the device up
		alarmManager().setInexactRepeating(AlarmManager.RTC, CommonFunctions.next3Am(), AlarmManager.INTERVAL_DAY, pendingIntent(context))
		Log.d(tag, "ALARM STARTED " + dateTimeString(CommonFunctions.next3Am()))
	}

	fun stopAlarm() {
		CommonFunctions.saveTimeWithStringToSharedPreferences(
			context,
			"WidgetAlarm stopAlarm " + CommonFunctions.dateTimeBeautiful(System.currentTimeMillis())
		)
		alarmManager().cancel(pendingIntent(context))
		Log.d(tag, "ALARM STOPPED")
	}

	// Just for log. For anything else, use the functions in CommonFunctions
	private fun dateTimeString(timeInMillis: Long): String {
		return DateFormat.getDateTimeInstance().format(timeInMillis)
	}

	private fun alarmManager(): AlarmManager {
		return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
	}

	private fun pendingIntent(context: Context): PendingIntent {
		val alarmIntent = Intent(context, WorkoutPixelAppWidgetProvider::class.java)
		alarmIntent.action = WorkoutPixelAppWidgetProvider.ACTION_ALARM_UPDATE
		return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)
	}
}