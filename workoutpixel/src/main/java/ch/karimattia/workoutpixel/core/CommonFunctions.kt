package ch.karimattia.workoutpixel.core

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.compose.ui.graphics.Color
import ch.karimattia.workoutpixel.data.SettingsData
import ch.karimattia.workoutpixel.core.Constants.PREFERENCE_NAME
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.GoalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.math.roundToInt

object Constants {
	const val MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000
	const val ACTION_ALARM_UPDATE = "ALARM_UPDATE"
	const val ACTION_DONE_EXERCISE = "DONE_EXERCISE"
	const val PREFERENCE_NAME = "settings"
}

enum class Status {
	NONE, GREEN, BLUE, RED
}

private const val TAG = "WORKOUT_PIXEL COMMON FUNCTIONS"

/**
 * Get new status
 */
// Controls which status to set.
fun getNewStatus(goal: Goal): Status {

	// Point in time when the widget should change to blue/red as soon as it's night time the next time.
	val timeBlue = goal.lastWorkout + intervalInMilliseconds(goal.intervalBlue - 1)
	val timeRed = timeBlue + intervalInMilliseconds(2)

	// Don't change the widget status if this is the first time the alarm runs and thus lastWorkout == 0L.
	return when {
		goal.lastWorkout == 0L -> Status.NONE
		timeRed < last3Am() -> Status.RED
		timeBlue < last3Am() -> Status.BLUE
		else -> Status.GREEN
	}
}

/**
 * Calendar stuff
 */
fun last3Am(): Long {
	var last3Am = today3Am()
	// If it's before 3am, the last 3am way yesterday, thus subtract a day.
	if (!after3Am()) {
		// Can be commented to test alarm -> it will come up more or less instantly after restart.
		last3Am -= intervalInMilliseconds(1)
	}
	return last3Am
}

fun next3Am(): Long {
	var next3Am = today3Am()
	// If it's already after 3am, the next 3am will be tomorrow, thus add a day.
	if (after3Am()) {
		// Can be commented to test alarm -> it will come up more or less instantly after restart.
		next3Am += intervalInMilliseconds(1)
	}
	return next3Am
}

fun today3Am(): Long {
	val calendar = Calendar.getInstance()
	calendar.timeInMillis = System.currentTimeMillis()
	calendar[Calendar.HOUR_OF_DAY] = 3
	calendar[Calendar.MINUTE] = 0
	return calendar.timeInMillis
}

fun after3Am(): Boolean {
	val calendar = Calendar.getInstance()
	calendar.timeInMillis = System.currentTimeMillis()
	return calendar[Calendar.HOUR_OF_DAY] >= 3
}

fun intervalInMilliseconds(intervalInDays: Int): Long {
	return Constants.MILLISECONDS_IN_A_DAY.toLong() * intervalInDays
}
// --Commented out by Inspection START (23.06.21, 20:28):
//    public static int intervalInDays(long intervalInMilliseconds) {
//        return (int) (intervalInMilliseconds / MILLISECONDS_IN_A_DAY);
//    }
// --Commented out by Inspection STOP (23.06.21, 20:28)
/**
 * Match status to background color
 */
fun getColorFromStatusColor(status: Status, settingsData: SettingsData): Color {
	return when (status) {
		Status.GREEN -> settingsData.colorDone()
		Status.BLUE -> settingsData.colorFirstInterval()
		Status.RED -> settingsData.colorSecondInterval()
		Status.NONE -> settingsData.colorInitial()
	}
}

fun getColorFromStatus(status: Status, settingsData: SettingsData): Int {
	return when (status) {
		Status.GREEN -> settingsData.colorDoneInt
		Status.BLUE -> settingsData.colorFirstIntervalInt
		Status.RED -> settingsData.colorSecondIntervalInt
		Status.NONE -> settingsData.colorInitialInt
	}
}


/**
 * Time and date formatting stuff
 */
fun dateBeautiful(date: Long): String {
	return if (date == 0L) {
		"Never"
	} else {
		val lastWorkout = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDateTime()
		val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale("de", "CH"))
		lastWorkout.format(dateFormatter)
	}
}

fun timeBeautiful(date: Long): String {
	return if (date == 0L) "" else {
		val lastWorkout = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDateTime()
		val dateFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale("de", "CH"))
		lastWorkout.format(dateFormatter)
	}
}

fun dateTimeBeautiful(date: Long): String {
	return if (date == 0L) "Never" else {
		val lastWorkout = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDateTime()
		val dateFormatter =
			DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale("de", "CH"))
		lastWorkout.format(dateFormatter)
	}
}

/**
 * Save to shared preferences stuff
 */
// Last alarm
fun saveTimeWithStringToSharedPreferences(context: Context, string: String?) {
	val prefs = context.getSharedPreferences(PREFERENCE_NAME, 0).edit()
	val timeLastWorkoutBeautiful = dateTimeBeautiful(System.currentTimeMillis())
	prefs.putString(string, timeLastWorkoutBeautiful)
	prefs.apply()
}

/**
 * AppWidgetId stuff
 */
class ContextFunctions @Inject constructor(
	@ApplicationContext val context: Context,
) {
	fun appWidgetIds(): IntArray {
		val thisAppWidget = ComponentName(context.packageName, WorkoutPixelAppWidgetProvider::class.java.name)
		return AppWidgetManager.getInstance(context).getAppWidgetIds(thisAppWidget)
	}

	fun goalsWithInvalidAppWidgetId(goals: List<Goal>): List<Goal> {
		return goals.stream().filter { (_, _) ->
			Arrays.stream(appWidgetIds()).noneMatch { appWidgetId: Int -> appWidgetId == appWidgetId }
		}
			.collect(Collectors.toList())
	}

	fun goalsWithInvalidOrNullAppWidgetId(goals: List<Goal>): List<Goal> {
		return goals.stream()
			.filter { (_, appWidgetId) ->
				Arrays.stream(appWidgetIds()).noneMatch { i: Int -> appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && i == appWidgetId }
			}
			.collect(Collectors.toList())
	}
}

/**
 * Wordings
 */
fun times(times: Int): String {
	return when {
		times == 0 -> "0 times"
		times == 1 -> "1 time"
		times > 1 -> "$times times"
		else -> "INVALID NUMBER OF TIMES"
	}
}

fun days(days: Int): String {
	return when {
		days == 0 -> "days."
		days == 1 -> "day."
		days > 1 -> "days."
		else -> "INVALID NUMBER OF DAYS"
	}
}

fun testData(): List<Goal> {
	val testData: MutableList<Goal> = ArrayList()
	testData.add(
		Goal(
			title = "Push ups",
			lastWorkout = today3Am() - intervalInMilliseconds(1),
			intervalBlue = 2,
			//status = Status.GREEN
		)
	)
	testData.add(
		Goal(
			title = "Back exercises",
			lastWorkout = today3Am() - intervalInMilliseconds(2),
			intervalBlue = 7,
			showDate = true,
			//status = Status.GREEN
		)
	)
	testData.add(
		Goal(
			title = "Visualize your day",
			lastWorkout = today3Am() + (intervalInMilliseconds(1) * 0.259).roundToInt(),
			intervalBlue = 1,
			showTime = true,
			//status = Status.GREEN
		)
	)
	testData.add(
		Goal(
			title = "Morning walk",
			lastWorkout = today3Am(),
			intervalBlue = 1,
			//status = Status.GREEN
		)
	)
	testData.add(
		Goal(
			title = "Water plants",
			lastWorkout = today3Am() - intervalInMilliseconds(7),
			intervalBlue = 7,
			showDate = true,
			//status = Status.BLUE
		)
	)
	for (goal in testData) {
		goal.uid = testData.indexOf(goal) + 1
	}
	return testData
}

fun goalFromGoalsByUid(goalUid: Int, goals: List<Goal>): Goal? {
	return goals.firstOrNull { it.uid == goalUid }
}

class DatabaseInteractions @Inject constructor(
	@ApplicationContext val context: Context,
	private val repository: GoalRepository,
	private val contextFunctions: ContextFunctions,
) {
	// @Inject lateinit var contextFunctions: ContextFunctions
	// Sets all appWidgetIds of goals that are not valid to null. Maybe later even reassign some to unassigned widgets.
	fun cleanGoals(goals: List<Goal>) {
		for (goal in contextFunctions.goalsWithInvalidAppWidgetId(goals)) {
			if (goal.hasValidAppWidgetId()) {
				repository.setAppWidgetIdToNullByUid(goal.uid)
			}
		}
	}
}
