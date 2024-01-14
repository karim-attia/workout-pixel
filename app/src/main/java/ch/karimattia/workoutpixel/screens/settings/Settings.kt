package ch.karimattia.workoutpixel.screens.settings

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.karimattia.workoutpixel.core.colorToInt
import ch.karimattia.workoutpixel.core.dateBeautiful
import ch.karimattia.workoutpixel.core.timeBeautiful
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData
import ch.karimattia.workoutpixel.screens.Lambdas
import ch.karimattia.workoutpixel.ui.theme.*
import java.util.*

@Suppress("unused")
private const val TAG: String = "Settings"

@Composable
fun Settings(
	lambdas: Lambdas,
	settingsData: SettingsData = lambdas.settingsData,
	settingChange: (SettingsData) -> Unit = lambdas.settingChange,
) {
	val goal = Goal(title = "Select color", lastWorkout = 1663506300000L, intervalBlue=10000)
	Log.d(TAG, "dateLanguage: ${settingsData.dateLanguage} / dateCountry: ${settingsData.dateCountry}")
	Log.d(TAG, "timeLanguage: ${settingsData.timeLanguage} / timeCountry: ${settingsData.timeCountry}")
	Column {
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
		SettingsTitle(text = "Colors")
		ColorSelection(
			titleText = "Done goals",
			subtitleText = "Color of goals that you recently reached",
			goal = goal,
			currentColor = settingsData.colorDone(),
			resetColor = Color(Green),
			onColorSelected = { settingChange(settingsData.copy(colorDoneInt = colorToInt(it))) },
			settingsData = settingsData,
		)
		ColorSelection(
			titleText = "Pending goals",
			subtitleText = "Color to remind you to reach this goal",
			goal = goal,
			currentColor = settingsData.colorFirstInterval(),
			resetColor = Color(Blue),
			onColorSelected = { settingChange(settingsData.copy(colorFirstIntervalInt = colorToInt(it))) },
			settingsData = settingsData,
		)
		ColorSelection(
			titleText = "Due goals",
			subtitleText = "Color of goals that are pending since 2+ days",
			goal = goal,
			currentColor = settingsData.colorSecondInterval(),
			resetColor = Color(Red),
			onColorSelected = { settingChange(settingsData.copy(colorSecondIntervalInt = colorToInt(it))) },
			settingsData = settingsData,
		)
		ColorSelection(
			titleText = "New goals",
			subtitleText = "Color of goals that you never clicked",
			goal = goal,
			currentColor = settingsData.colorInitial(),
			resetColor = Color(Grey),
			onColorSelected = { settingChange(settingsData.copy(colorInitialInt = colorToInt(it))) },
			settingsData = settingsData,
		)
		// DATE format
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
		SettingsTitle(text = "Date and time format")
		LocaleSelectionDateTime(
			goal = goal,
			settingsData = settingsData,
			onChoiceChange = { language: String?, country: String? ->
				settingChange(settingsData.copy(dateLanguage = language, dateCountry = country))
			},
			format = Formats(settingsData = settingsData).DATE
		)
		// TIME format
		LocaleSelectionDateTime(
			goal = goal,
			settingsData = settingsData,
			onChoiceChange = { language: String?, country: String? ->
				settingChange(settingsData.copy(timeLanguage = language, timeCountry = country))
			},
			format = Formats(settingsData = settingsData).TIME
		)
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
	}
}

data class Format(
	val label: String,
	val locale: Locale,
	val isLocaleDefault: Boolean,
	// Function to convert a locale into a nice example string
	val transformDateTimeFormatToString: (locale: Locale) -> String,
	val maxStringSizeFilter: Int,
	val showDate: Boolean = false,
	val showTime: Boolean = false,
)

class Formats(
	val settingsData: SettingsData,
	val DATE: Format = Format(
		label = "date",
		locale = settingsData.dateLocale(),
		isLocaleDefault = settingsData.isDateLocaleDefault(),
		transformDateTimeFormatToString = { dateBeautiful(1663506300000L, it, agoWording = false) },
		maxStringSizeFilter = 10,
		showDate = true,
	),
	val TIME: Format = Format(
		label = "time",
		locale = settingsData.timeLocale(),
		isLocaleDefault = settingsData.isTimeLocaleDefault(),
		transformDateTimeFormatToString = { timeBeautiful(1663506300000L, it) + " / " + timeBeautiful(date = 1663506300000L + 12 * 60 * 60 * 1000, it) },
		maxStringSizeFilter = 23,
		showTime = true,
	),
)

@Composable
fun SettingsEntry(
	titleText: String,
	subtitleText: String,
	onClick: () -> Unit,
	onTheRight: @Composable () -> Unit = {
		Box(modifier = Modifier
			.width(66.dp)
			.height(44.dp))
	},
) {
	Row(modifier = Modifier
		.clickable { onClick() }
		.padding(vertical = 4.dp, horizontal = 8.dp)
		.defaultMinSize(minHeight = 48.dp)) {
		Column(modifier = Modifier
			.weight(1f)
			.padding(end = 4.dp)) {
			Text(
				text = titleText,
				//fontWeight = FontWeight.Medium,
				style = MaterialTheme.typography.labelLarge,
			)
			Text(
				text = subtitleText,
				style = MaterialTheme.typography.bodyMedium,
			)
		}
		onTheRight()
	}
}

@Composable
fun SettingsTitle(
	text: String,
) {
	Text(text = text,
		fontWeight = FontWeight.Medium,
		style = MaterialTheme.typography.titleMedium,
		// color = MaterialTheme.colors.primary,
		modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp))
}

@Preview(name = "Instructions preview", showBackground = true)
@Composable
fun SettingsPreview() {
	Settings(
		lambdas = Lambdas()
	)
}