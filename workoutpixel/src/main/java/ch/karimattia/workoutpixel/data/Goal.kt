package ch.karimattia.workoutpixel.data

import android.appwidget.AppWidgetManager
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ch.karimattia.workoutpixel.core.Constants.STATUS_NONE
import ch.karimattia.workoutpixel.core.dateBeautiful
import ch.karimattia.workoutpixel.core.getNewStatus
import ch.karimattia.workoutpixel.core.timeBeautiful

@Entity(tableName = "goals")
data class Goal
	(
	@PrimaryKey(autoGenerate = true) var uid: Int = 0,
	@Nullable
	@ColumnInfo(name = "appWidgetId") var appWidgetId: Int? = AppWidgetManager.INVALID_APPWIDGET_ID,
	@ColumnInfo(name = "title") var title: String? = "",
	@ColumnInfo(name = "lastWorkout") var lastWorkout: Long = 0,
	@ColumnInfo(name = "intervalBlue") var intervalBlue: Int = 2,
	@ColumnInfo(name = "intervalRed") val intervalRed: Int = 2,
	@ColumnInfo(name = "showDate") var showDate: Boolean = false,
	@ColumnInfo(name = "showTime") var showTime: Boolean = false,
	@ColumnInfo(name = "status") var status: String? = STATUS_NONE,
) {
	fun setNewLastWorkout(lastWorkout: Long): Boolean {
		return if (this.lastWorkout == lastWorkout) {
			false
		} else {
			this.lastWorkout = lastWorkout
			setNewStatus()
			true
		}
	}

	fun setNewStatus(): Boolean {
		val newStatus = getNewStatus(lastWorkout, intervalBlue)
		return if (status == newStatus) {
			false
		} else {
			status = newStatus
			true
		}
	}

	fun hasValidAppWidgetId(): Boolean {
		val hasValidAppWidgetId = appWidgetId != null
		return hasValidAppWidgetId
	}

	override fun toString(): String {
		return title + ": " + everyWording()
	}

	fun everyWording(): String {
		var everyWording = "Every $intervalBlue day"
		if (intervalBlue > 1) {
			everyWording += "s"
		}
		return everyWording
	}

	fun debugString(): String {
		return "widgetUid: $uid, appWidgetId: $appWidgetId, Title: $title: "
	}

	// widgetText returns the text of the whole widget based on a Widget object.
	fun widgetText(): String? {
		var widgetText = title
		if (showDate and (status != STATUS_NONE)) {
			widgetText += "\n${dateBeautiful(lastWorkout)}"
		}
		if (showTime and (status != STATUS_NONE)) {
			widgetText += "\n${timeBeautiful(lastWorkout)}"
		}
		return widgetText
	}
}