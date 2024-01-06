package ch.karimattia.workoutpixel.data

import android.appwidget.AppWidgetManager
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import ch.karimattia.workoutpixel.core.*
import kotlin.math.log

@Suppress("unused")
private const val TAG: String = "Goal.kt"
@Entity(tableName = "goals")
data class Goal(
	@PrimaryKey(autoGenerate = true) var uid: Int = 0,
	@ColumnInfo(name = "appWidgetId") var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID,
	@ColumnInfo(name = "glanceId", defaultValue = "") var glanceId: String = "",
	@ColumnInfo(name = "title") var title: String = "",
	@ColumnInfo(name = "lastWorkout") var lastWorkout: Long = 0,
	@ColumnInfo(name = "intervalBlue") var intervalBlue: Int = 2,
	@ColumnInfo(name = "intervalRed") var intervalRed: Int = 2,
	@ColumnInfo(name = "showDate") var showDate: Boolean = false,
	@ColumnInfo(name = "showTime") var showTime: Boolean = false,
	// Is just needed for preview and thus not needed to save to database.
	@Ignore var statusOverride: Status? = null,
) {
	fun status(): Status {
		// Point in time when the widget should change to blue/red as soon as it's night time the next time.
		val timeBlue = lastWorkout + intervalInMilliseconds(intervalBlue - 1)
		val timeRed = timeBlue + intervalInMilliseconds(2)

		return when {
			// If there is a statusOverride, return the statusOverride.
			statusOverride != null -> statusOverride!!
			lastWorkout == 0L -> Status.NONE
			timeRed < last3Am() -> Status.RED
			timeBlue < last3Am() -> Status.BLUE
			else -> Status.GREEN
		}
	}

	fun color(settingsData: SettingsData): Color =
		Color(colorInt(settingsData = settingsData))

	fun colorInt(settingsData: SettingsData): Int = getColorIntFromStatus(status(), settingsData)

/*	fun setNewLastWorkout(lastWorkout: Long): Boolean {
		return if (this.lastWorkout == lastWorkout) {
			false
		} else {
			this.lastWorkout = lastWorkout
			true
		}
	}*/

	fun hasValidAppWidgetId(): Boolean = appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID
	override fun toString(): String = title + ": " + everyWording()
	fun everyWording(): String = "Every ${if (intervalBlue == 1) "day" else "$intervalBlue days"}"
	fun debugString(): String = "widgetUid: $uid, appWidgetId: $appWidgetId, Title: $title: "

	// widgetText returns the text of the whole widget based on a goal.
/*	fun widgetText(settingsData: SettingsData): String {
		var widgetText: String = title
		if (showDate || showTime) widgetText += "\n"
		widgetText += widgetTextDateAndTime(settingsData)
		return widgetText
	}*/

	fun widgetTextDateAndTime(settingsData: SettingsData): String {
		var widgetTextDateAndTime = ""
		if ((status() != Status.NONE)) {
			// If statusOverride override is active, use currentTimeMillis to show how the widget will look in the preview.
			val displayTime: Long = if (statusOverride == Status.GREEN) System.currentTimeMillis() else {
				lastWorkout
			}
			if (showDate) widgetTextDateAndTime += dateBeautiful(displayTime, settingsData.dateLocale())
			if (showDate && showTime) widgetTextDateAndTime += "\n"
			if (showTime) widgetTextDateAndTime += timeBeautiful(displayTime, settingsData.timeLocale())
			Log.d(TAG,"widgetTextDateAndTime: $widgetTextDateAndTime")
		}
		return widgetTextDateAndTime
	}
}