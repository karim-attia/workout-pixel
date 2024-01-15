package ch.karimattia.workoutpixel.data

import android.appwidget.AppWidgetManager
import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import ch.karimattia.workoutpixel.core.*

@Suppress("unused")
private const val TAG: String = "Goal.kt"

@Entity(tableName = "goals")
data class GoalWithoutCount(
	@PrimaryKey(autoGenerate = true) var uid: Int = 0,
	@ColumnInfo(name = "appWidgetId") var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID,
	@ColumnInfo(name = "glanceId", defaultValue = "") var glanceId: String = "",
	@ColumnInfo(name = "title") var title: String = "",
	@ColumnInfo(name = "lastWorkout") var lastWorkout: Long = 0,
	@ColumnInfo(name = "intervalBlue") var intervalBlue: Int = 2,
	@ColumnInfo(name = "intervalRed") var intervalRed: Int = 2,
	@ColumnInfo(name = "showDate") var showDate: Boolean = false,
	@ColumnInfo(name = "showTime") var showTime: Boolean = false
	// Note: 'count' and 'statusOverride' are not included here
)

@DatabaseView("SELECT goals.*, (SELECT COUNT(*) FROM pastWorkouts WHERE widgetUid = goals.uid AND active = '1') AS count FROM goals")
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
	// Count is fetched from the pastClicks and not saved with Goal.
	@ColumnInfo(name = "count")
	@Transient var count: Int = 1,
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

	fun color(settingsData: SettingsData): Color = getColorFromStatus(status(), settingsData)

	fun hasValidAppWidgetId(): Boolean = appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID
	override fun toString(): String = title + ": " + everyWording()
	private fun everyWording(): String = "Every ${if (intervalBlue == 1) "day" else "$intervalBlue days"}"
	fun debugString(): String = "widgetUid: $uid, appWidgetId: $appWidgetId, Title: $title: "

	fun widgetTextDateAndTime(): String {
		var widgetTextDateAndTime = ""
		if ((status() != Status.NONE)) {
			// If statusOverride override is active, use currentTimeMillis to show how the widget will look in the preview.
			val displayTime: Long = if (statusOverride == Status.GREEN) System.currentTimeMillis() else {
				lastWorkout
			}
			if (showDate) widgetTextDateAndTime += dateBeautiful(displayTime)
			if (showDate && showTime) widgetTextDateAndTime += "\n"
			if (showTime) widgetTextDateAndTime += timeBeautiful(displayTime)
			// Log.d(TAG,"widgetTextDateAndTime: $widgetTextDateAndTime")
		}
		return widgetTextDateAndTime
	}

	fun goalWithoutCount(): GoalWithoutCount = GoalWithoutCount(
		uid = uid,
		appWidgetId = appWidgetId,
		glanceId = glanceId,
		title = title,
		lastWorkout = lastWorkout,
		intervalBlue = intervalBlue,
		intervalRed = intervalRed,
		showDate = showDate,
		showTime = showTime
	)
}