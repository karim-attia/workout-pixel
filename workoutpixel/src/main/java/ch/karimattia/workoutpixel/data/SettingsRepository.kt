package ch.karimattia.workoutpixel

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import ch.karimattia.workoutpixel.ui.theme.Blue
import ch.karimattia.workoutpixel.ui.theme.Green
import ch.karimattia.workoutpixel.ui.theme.Purple
import ch.karimattia.workoutpixel.ui.theme.Red

abstract class SettingsRepository {
	// Flow?
	private var settingsData: SettingsData? = null

	fun getSettingsData(context: Context): SettingsData {
		if (null == settingsData) {
			settingsData = getSettingsDataFromPreferences(context)
		}
		return settingsData as SettingsData
	}

	private fun getSettingsDataFromPreferences(context: Context): SettingsData {
		return SettingsData(
			Green = loadDone(context),
			Blue = Color(Blue),
			Red = Color(Red),
			Purple = Color(Purple)
		)
	}

	private val PREFS_NAME = "com.example.WorkoutPixel"
	private val PREF_PREFIX_KEY_DONE = "color_done_"
	// private val prefs(context: Context): SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

	private fun loadDone(context: Context): Color {
		val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)
		return Color(prefs.getInt(PREF_PREFIX_KEY_DONE, Green))
	}

	fun setColorDone(context: Context, color: Color) {
		settingsData?.Green = color
		val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)


	}
}

class SettingsData internal constructor(
	var Green: Color,
	val Blue: Color,
	val Red: Color,
	val Purple: Color,
)

