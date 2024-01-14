package ch.karimattia.workoutpixel.core

import android.content.Context
import androidx.compose.ui.graphics.Color
import ch.karimattia.workoutpixel.core.Constants.PREFERENCE_NAME
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.PastClick
import ch.karimattia.workoutpixel.data.SettingsData
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.math.roundToInt

@Suppress("unused")
private const val TAG = "WORKOUT_PIXEL COMMON FUNCTIONS"

object Constants {
	const val MILLISECONDS_IN_A_DAY = 24 * 60 * 60 * 1000
	const val ACTION_ALARM_UPDATE = "ALARM_UPDATE"
	const val ACTION_DONE_EXERCISE = "DONE_EXERCISE"
	const val ACTION_SETUP_WIDGET = "SETUP_WIDGET"
	const val PREFERENCE_NAME = "SharedPreferences"
	const val GOAL_UID = "goalUid"
	const val INVALID_GOAL_UID = 0

	const val colorDoneInt = "colorDoneInt"
	const val colorFirstIntervalInt = "colorFirstIntervalInt"
	const val colorSecondIntervalInt = "colorSecondIntervalInt"
	const val colorInitialInt = "colorInitialInt"
	const val dateLanguage = "dateLanguage"
	const val dateCountry = "dateCountry"
	const val timeLanguage = "timeLanguage"
	const val timeCountry = "timeCountry"
}

enum class Status {
	GREEN, BLUE, RED, NONE
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
 * Color stuff
 */
fun getColorFromStatus(status: Status, settingsData: SettingsData): Color =
	Color(getColorIntFromStatus(status = status, settingsData = settingsData))

fun getColorIntFromStatus(status: Status, settingsData: SettingsData): Int {
	return when (status) {
		Status.GREEN -> settingsData.colorDoneInt
		Status.BLUE -> settingsData.colorFirstIntervalInt
		Status.RED -> settingsData.colorSecondIntervalInt
		Status.NONE -> settingsData.colorInitialInt
	}
}

fun colorToInt(color: Color): Int = android.graphics.Color.argb(color.alpha, color.red, color.green, color.blue)

/**
 * Time and date formatting stuff
 */
fun dateBeautiful(date: Long, locale: Locale, agoWording: Boolean=true): String {

	fun daysAgo(date: Long): Int {
		val now = System.currentTimeMillis()
		return ((now - date) / (1000 * 60 * 60 * 24)).toInt()
	}

	// If today or yesterday, show "today" or "yesterday" instead of the date.
	var dateBeautiful = when {
		date == 0L -> "Never"
		date > last3Am() -> "Today"
		date > last3Am() - intervalInMilliseconds(1) -> if (agoWording) "Yesterday" else "1d"
		else -> {
			val daysAgo = daysAgo(date)
			"${daysAgo}d"
		}
	}

	if (agoWording && date < last3Am() - intervalInMilliseconds(1)) dateBeautiful += " ago"
	return dateBeautiful
}

fun timeBeautiful(date: Long, locale: Locale): String {
	return if (date == 0L) "" else {
		val lastWorkout: LocalDateTime = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDateTime()
		val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
		lastWorkout.format(dateFormatter)
	}
}

fun dateTimeBeautiful(date: Long, locale: Locale = Locale("de", "CH")): String {
	return if (date == 0L) "Never" else {
		val lastWorkout: LocalDateTime = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDateTime()
		val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale)
		lastWorkout.format(dateFormatter)
	}
}

/**
 * Save to shared preferences stuff
 */
// Last alarm
fun saveTimeWithStringToSharedPreferences(context: Context, string: String) {
	val prefs = context.getSharedPreferences(PREFERENCE_NAME, 0).edit()
	prefs.putString(string, "")
	prefs.apply()
}

/**
 * Wordings
 */
fun plural(times: Int, word: String): String {
	return when {
		times == 0 -> word + "s"
		times == 1 -> word
		times > 1 -> word + "s"
		else -> "INVALID NUMBER OF $word"
	}
}

/**
 * goalFromGoalsByUid
 */
fun goalFromGoalsByUid(goalUid: Int, goals: List<Goal>): Goal? = goals.firstOrNull { it.uid == goalUid }

/**
 * Test data
 */
val testGoals: List<Goal> = listOf(
	Goal(
		uid = 1,
		title = "Your goal \n(done) ",
		statusOverride = Status.GREEN,
	),
	Goal(
		uid = 2,
		title = "Your goal \n(todo)",
		statusOverride = Status.BLUE,
	),
	Goal(
		uid = 3,
		title = "Push ups",
		lastWorkout = today3Am() - intervalInMilliseconds(1),
		intervalBlue = 2,
		//status = Status.GREEN
	),
	Goal(
		uid = 4,
		title = "Back exercises",
		lastWorkout = today3Am() - intervalInMilliseconds(2),
		intervalBlue = 7,
		showDate = true,
		//status = Status.GREEN
	),
	Goal(
		uid = 5,
		title = "Visualize your day",
		lastWorkout = today3Am() + (intervalInMilliseconds(1) * 0.259).roundToInt(),
		intervalBlue = 1,
		showTime = true,
		//status = Status.GREEN
	),
	Goal(
		uid = 6,
		title = "Morning walk",
		lastWorkout = today3Am(),
		intervalBlue = 1,
		//status = Status.GREEN
	),
	Goal(
		uid = 7,
		title = "Water plants",
		lastWorkout = today3Am() - intervalInMilliseconds(7),
		intervalBlue = 7,
		showDate = true,
		//status = Status.BLUE
	)
)

val testPastClicks: List<PastClick> = listOf(
	PastClick(
		uid = 1,
		widgetUid = 1,
		workoutTime = today3Am() - intervalInMilliseconds(1) + (intervalInMilliseconds(1) * 0.259).roundToInt(),
	),
	PastClick(
		uid = 2,
		widgetUid = 1,
		workoutTime = today3Am() - intervalInMilliseconds(2) - (intervalInMilliseconds(1) * 0.259).roundToInt(),
	),
)

