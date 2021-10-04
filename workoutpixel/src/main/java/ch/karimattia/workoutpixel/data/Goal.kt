package ch.karimattia.workoutpixel.data

import android.appwidget.AppWidgetManager
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ch.karimattia.workoutpixel.core.*

@Entity(tableName = "goals")
data class Goal
	(
	@PrimaryKey(autoGenerate = true) var uid: Int = 0,
	@ColumnInfo(name = "appWidgetId") var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID,
	@ColumnInfo(name = "title") var title: String = "",
	@ColumnInfo(name = "lastWorkout") var lastWorkout: Long = 0,
	@ColumnInfo(name = "intervalBlue") var intervalBlue: Int = 2,
	@ColumnInfo(name = "intervalRed") val intervalRed: Int = 2,
	@ColumnInfo(name = "showDate") var showDate: Boolean = false,
	@ColumnInfo(name = "showTime") var showTime: Boolean = false,
) {
	fun status(): Status {
		// Point in time when the widget should change to blue/red as soon as it's night time the next time.
		val timeBlue = lastWorkout + intervalInMilliseconds(intervalBlue - 1)
		val timeRed = timeBlue + intervalInMilliseconds(2)

		return when {
			lastWorkout == 0L -> Status.NONE
			timeRed < last3Am() -> Status.RED
			timeBlue < last3Am() -> Status.BLUE
			else -> Status.GREEN
		}
	}

	fun setNewLastWorkout(lastWorkout: Long): Boolean {
		return if (this.lastWorkout == lastWorkout) {
			false
		} else {
			this.lastWorkout = lastWorkout
			true
		}
	}

	fun hasValidAppWidgetId(): Boolean = appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID
	override fun toString(): String = title + ": " + everyWording()
	fun everyWording(): String = "Every $intervalBlue day${if (intervalBlue > 1) "s" else ""}"
	fun debugString(): String = "widgetUid: $uid, appWidgetId: $appWidgetId, Title: $title: "

	// widgetText returns the text of the whole widget based on a Widget object.
	fun widgetText(settingsData: SettingsData): String {
		var widgetText: String = title
		if ((status() != Status.NONE)) {
			if (showDate) widgetText += "\n${dateBeautiful(lastWorkout, settingsData.dateLocale())}"
			if (showTime) widgetText += "\n${timeBeautiful(lastWorkout, settingsData.timeLocale())}"
		}
		return widgetText
	}
}