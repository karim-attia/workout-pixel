package ch.karimattia.workoutpixel.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
	tableName = "pastWorkouts",
	foreignKeys = [ForeignKey(
		entity = Goal::class,
		parentColumns = arrayOf("uid"),
		childColumns = arrayOf("widgetUid"),
		onDelete = ForeignKey.CASCADE
	)]
)
data class PastWorkout(
	@ColumnInfo(name = "widgetUid", index = true)
	var widgetUid: Int,
	@ColumnInfo(name = "workoutTime")
	var workoutTime: Long,
	@ColumnInfo(name = "active")
	var isActive: Boolean = true,
) {
	@PrimaryKey(autoGenerate = true)
	var uid: Int = 0
}