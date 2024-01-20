package ch.karimattia.workoutpixel.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
	tableName = "pastWorkouts",
	foreignKeys = [ForeignKey(
		entity = GoalWithoutCount::class,
		parentColumns = arrayOf("uid"),
		childColumns = arrayOf("widgetUid"),
		onDelete = ForeignKey.CASCADE
	)]
)
data class PastClick(
	@PrimaryKey(autoGenerate = true)
	var uid: Int = 0,
	@ColumnInfo(name = "widgetUid", index = true)
	var widgetUid: Int,
	@ColumnInfo(name = "workoutTime")
	var workoutTime: Long,
	@ColumnInfo(name = "active")
	var isActive: Boolean = true,
)

/*data class GoalAndPastClicks (
	@Embedded
	var goal: Goal? = null,

	@Relation(
		parentColumn = "uid",
		entityColumn = "widgetUid",
	)
	var pastClicks: List<PastClick> = ArrayList()
)*/

data class PastClickAndGoal(
	@Embedded
	var pastClick: PastClick,

	@Relation(
		parentColumn = "widgetUid",
		entityColumn = "uid",
	)
	var goal: Goal,
)