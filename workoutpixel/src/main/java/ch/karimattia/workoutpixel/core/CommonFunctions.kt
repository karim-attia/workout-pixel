package ch.karimattia.workoutpixel.core

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import ch.karimattia.workoutpixel.R
import ch.karimattia.workoutpixel.core.Constants.PREFS_NAME
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.GoalRepository
import ch.karimattia.workoutpixel.ui.theme.Blue
import ch.karimattia.workoutpixel.ui.theme.Green
import ch.karimattia.workoutpixel.ui.theme.Purple
import ch.karimattia.workoutpixel.ui.theme.Red
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.math.roundToInt

object Constants {
	const val STATUS_NONE = "NO STATUS"
	const val STATUS_GREEN = "GREEN"
	const val STATUS_BLUE = "BLUE"
	const val STATUS_RED = "RED"
	const val MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000
	const val ACTION_ALARM_UPDATE = "ALARM_UPDATE"
	const val ACTION_DONE_EXERCISE = "DONE_EXERCISE"
	const val PREFS_NAME = "com.example.WorkoutPixel"
}

private val TAG = "WORKOUT_PIXEL COMMON FUNCTIONS"

// TODO: Change to enum or int 0-3. enum not great for room DB.
val executorService: ExecutorService = Executors.newSingleThreadExecutor()
// private static final int NUMBER_OF_THREADS = 4;
// public static final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
/**
 * Get new status
 */
// Controls which status to set.
fun getNewStatus(lastWorkout: Long, intervalBlue: Int): String {

	// Point in time when the widget should change to blue/red as soon as it's night time the next time.
	val timeBlue = lastWorkout + intervalInMilliseconds(intervalBlue - 1)
	val timeRed = timeBlue + intervalInMilliseconds(2)

	// Don't change the widget status if this is the first time the alarm runs and thus lastWorkout == 0L.
	when {
		lastWorkout == 0L -> {
			// Log.d(TAG, "GetNewStatus: " + STATUS_NONE);
			return Constants.STATUS_NONE
		}
		timeRed < last3Am() -> {
			// Log.d(TAG, "GetNewStatus: " + STATUS_RED);
			return Constants.STATUS_RED
		}
		timeBlue < last3Am() -> {
			// Log.d(TAG, "GetNewStatus: " + STATUS_BLUE);
			return Constants.STATUS_BLUE
		}
		// Log.d(TAG, "GetNewStatus: " + STATUS_GREEN);
		else -> return Constants.STATUS_GREEN
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
fun getDrawableIntFromStatus(status: String?): Int {
	return when (status) {
		Constants.STATUS_GREEN -> {
			Log.v(TAG, "set to green")
			R.drawable.rounded_corner_green
		}
		Constants.STATUS_BLUE -> {
			Log.v(TAG, "set to blue")
			R.drawable.rounded_corner_blue
		}
		Constants.STATUS_RED -> {
			Log.v(TAG, "set to red")
			R.drawable.rounded_corner_red
		}
		Constants.STATUS_NONE -> {
			Log.v(TAG, "set to purple")
			R.drawable.rounded_corner_purple
		}
		else -> {
			Log.d(TAG, "getDrawableIntFromStatus: status not correctly assigned.")
			0
		}
	}
}

fun getColorFromStatus(status: String?): Int {
	return when (status) {
		Constants.STATUS_GREEN -> Green
		Constants.STATUS_BLUE -> Blue
		Constants.STATUS_RED -> Red
		Constants.STATUS_NONE -> Purple
		else -> {
			Log.d(TAG, "getColorFromStatus: status not correctly assigned.")
			0
		}
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
		val dateFormatter =
			DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale("de", "CH"))
		lastWorkout.format(dateFormatter)
	}
}

fun timeBeautiful(date: Long): String {
	return if (date == 0L) {
		""
	} else {
		val lastWorkout = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDateTime()
		val dateFormatter =
			DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale("de", "CH"))
		lastWorkout.format(dateFormatter)
	}
}

fun dateTimeBeautiful(date: Long): String {
	return if (date == 0L) {
		"Never"
	} else {
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
	val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
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

/*
	fun goalsWithInvalidOrNullAppWidgetId(goals: List<Goal>): List<Goal> {
		return goals.stream()
			.filter { (_, appWidgetId) -> Arrays.stream(appWidgetIds()).noneMatch { i: Int -> appWidgetId != null && i == appWidgetId } }
			.collect(Collectors.toList())
	}
*/

	fun goalsWithInvalidOrNullAppWidgetId(goals: List<Goal>): List<Goal> {
		return goals.stream().filter { (appWidgetId) ->
			Arrays.stream(appWidgetIds()).noneMatch { i: Int -> appWidgetId != null && i == appWidgetId }
		}.collect(Collectors.toList())
	}

}

/**
 * Wordings
 */
fun times(times: Int): String {
	return when {
		times == 0 -> {
			"0 times"
		}
		times == 1 -> {
			"1 time"
		}
		times > 1 -> {
			"$times times"
		}
		else -> "INVALID NUMBER OF TIMES"
	}
}

fun days(days: Int): String {
	return when {
		days == 0 -> {
			"days."
		}
		days == 1 -> {
			"day."
		}
		days > 1 -> {
			"days."
		}
		else -> "INVALID NUMBER OF DAYS"
	}
}

fun testData(): List<Goal> {
	val testData: MutableList<Goal> = ArrayList()
	testData.add(Goal(0, null, "Push ups", today3Am() - intervalInMilliseconds(1), 2, 2, false, false, Constants.STATUS_GREEN))
	testData.add(Goal(0, null, "Back exercises", today3Am() - intervalInMilliseconds(2), 7, 2, true, false, Constants.STATUS_GREEN))
	testData.add(Goal(0, null, "Visualize your day", today3Am() + (intervalInMilliseconds(1) * 0.259).roundToInt(), 1, 2, false, true, Constants.STATUS_GREEN))
	testData.add(Goal(0, null, "Morning walk", today3Am(), 1, 2, false, false, Constants.STATUS_GREEN))
	testData.add(Goal(0, null, "Water plants", today3Am() - intervalInMilliseconds(7), 7, 2, true, false, Constants.STATUS_BLUE))
	for (goal in testData) {
		goal.uid = testData.indexOf(goal) + 1
	}
	return testData
}

fun goalFromGoalsByUid(goalUid: Int?, goals: List<Goal>): Goal? {
	return if (goalUid == null || goalUid == -1) {
		Log.d("goalFromGoalsByUid", "null or -1")
		null
	} else {
		Log.d("goalFromGoalsByUid", "$goalUid")
		goalFromGoalsByUid(goals = goals, goalUid = goalUid)
	}
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
