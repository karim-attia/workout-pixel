package ch.karimattia.workoutpixel.composables

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.core.colorToInt
import ch.karimattia.workoutpixel.core.dateBeautiful
import ch.karimattia.workoutpixel.core.timeBeautiful
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData
import ch.karimattia.workoutpixel.ui.theme.TextBlack
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ARGBPickerState
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import java.util.*
import java.util.Locale.getAvailableLocales

private const val TAG: String = "Settings"

@Composable
fun Settings(
	settingsData: SettingsData,
	settingChange: (SettingsData) -> Unit,
) {
	val goal = Goal(title = "Select\ncolor")
	Log.d(TAG, "dateLanguage: ${settingsData.dateLanguage} / dateCountry: ${settingsData.dateCountry}")
	Log.d(TAG, "timeLanguage: ${settingsData.timeLanguage} / timeCountry: ${settingsData.timeCountry}")
	Column {
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
		SettingsTitle(text = "Colors")
		ColorSelection(
			titleText = "Done goals",
			subtitleText = "Color of goals that you recently reached",
			goal = goal,
			color = settingsData.colorDone(),
			onColorSelected = { settingChange(settingsData.copy(colorDoneInt = colorToInt(it))) },
			settingsData = settingsData,
		)
		ColorSelection(
			titleText = "Pending goals",
			subtitleText = "Color to remind you to reach this goal",
			goal = goal,
			color = settingsData.colorFirstInterval(),
			onColorSelected = { settingChange(settingsData.copy(colorFirstIntervalInt = colorToInt(it))) },
			settingsData = settingsData,
		)
		ColorSelection(
			titleText = "Due goals",
			subtitleText = "Color of goals that are pending since 2+ days",
			goal = goal,
			color = settingsData.colorSecondInterval(),
			onColorSelected = { settingChange(settingsData.copy(colorSecondIntervalInt = colorToInt(it))) },
			settingsData = settingsData,
		)
		ColorSelection(
			titleText = "New goals",
			subtitleText = "Color of goals that you never clicked",
			goal = goal,
			color = settingsData.colorInitial(),
			onColorSelected = { settingChange(settingsData.copy(colorInitialInt = colorToInt(it))) },
			settingsData = settingsData,
		)
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

@Composable
fun ColorSelection(
	titleText: String,
	subtitleText: String,
	goal: Goal,
	color: Color,
	onColorSelected: (Color) -> Unit,
	settingsData: SettingsData,
) {
	val dialogState = rememberMaterialDialogState()
	MaterialDialog(dialogState = dialogState, buttons = {
		positiveButton("Ok")
		negativeButton("Cancel")
	}) {
		title(text = "Choose color for ${titleText.lowercase(Locale.getDefault())}")
		colorChooser(
			colors = listOf(color).plus(ColorPalette.Primary),
			argbPickerState = ARGBPickerState.WithAlphaSelector,
			onColorSelected = { onColorSelected(it) },
		)
	}

	SettingsEntry(titleText = titleText, subtitleText = subtitleText, onClick = { dialogState.show() }) {
		GoalPreview(goal = goal, backgroundColor = color, settingsData = settingsData)
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
		transformDateTimeFormatToString = { dateBeautiful(2010651132000L, it) },
		maxStringSizeFilter = 10,
		showDate = true,
	),
	val TIME: Format = Format(
		label = "time",
		locale = settingsData.timeLocale(),
		isLocaleDefault = settingsData.isTimeLocaleDefault(),
		transformDateTimeFormatToString = { timeBeautiful(8460000L, it) + " / " + timeBeautiful(date = 8460000L + 12 * 60 * 60 * 1000, it) },
		maxStringSizeFilter = 23,
		showTime = true,
	),
)

// This is super ugly, but also super unimportant, so I kinda don't want to touch it. :D
@Composable
fun LocaleSelectionDateTime(
	goal: Goal,
	settingsData: SettingsData,
	format: Format,
	onChoiceChange: (language: String?, country: String?) -> Unit,
	// All locales
	listLocales: Array<Locale> = getAvailableLocales(),
) {
	fun transformDateTimeFormatToStringTryCatch(locale: Locale): String = try {
		format.transformDateTimeFormatToString(locale)
	} catch (e: Exception) {
		"errorerrorerrorerrorerror"
	}

	val dialogState = rememberMaterialDialogState()
	Log.d(TAG, "size of listLocales: ${listLocales.size}")
	MaterialDialog(dialogState = dialogState, buttons = {
		positiveButton("Ok")
		negativeButton("Cancel")
	}) {
		// Map of all nice strings to their locales
		// map: Make sure, we only include Locales that are recreatable by language and country.
		// associateBy: Map unique nice example string to their locales.
		// remove entries with strings that are too long.
		val mapFormatsToLocales: Map<String, Locale> = listLocales
			.map { Locale(it.language, it.country) }
			.associateBy { transformDateTimeFormatToStringTryCatch(it) }
			.filterKeys { it.length < format.maxStringSizeFilter }
		// Only the list of the nice strings from above map.
		val listOfFormats: List<String> = mapFormatsToLocales.keys.toList()
		// Add the default value at the first position.
		val listOfFormatsWithDefault: List<String> = listOf("Default: ${transformDateTimeFormatToStringTryCatch(Locale.getDefault())}").plus(listOfFormats)

		title(text = "Choose ${format.label} format for widget")
		listItemsSingleChoice(
			list = listOfFormatsWithDefault,
			// 1. Build Locale from data in settingsData. 2. Convert into nice example string. 3. Lookup this string in the list.
			initialSelection = if (format.isLocaleDefault) 0 else listOfFormatsWithDefault.indexOf(transformDateTimeFormatToStringTryCatch(format.locale)),
			onChoiceChange = { selectedFormatIndex ->
				// If default is chosen, return null, else look up the value.
				if (selectedFormatIndex == 0) onChoiceChange(null, null) else {
					val selectedFormat: String = listOfFormatsWithDefault[selectedFormatIndex]
					// selectedFormat is from list which stems from map, thus should be not null.
					val chosenLocale: Locale = if (mapFormatsToLocales[selectedFormat] != null) mapFormatsToLocales[selectedFormat] as Locale else format.locale
					onChoiceChange(chosenLocale.language, chosenLocale.country)
				}
			}
		)
	}

	SettingsEntry(
		titleText = "${format.label.replaceFirstChar { it.uppercase() }} format",
		subtitleText = format.transformDateTimeFormatToString(format.locale),
		onClick = { dialogState.show() },
	) {
		GoalPreview(
			goal = goal.copy(title = "Your goal",
				showDate = format.showDate,
				showTime = format.showTime,
				lastWorkout = remember { System.currentTimeMillis() }),
			settingsData = settingsData,
		)
	}
}

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
			Text(text = titleText, fontWeight = FontWeight.Medium, fontSize = 16.sp)
			Text(text = subtitleText, fontSize = 14.sp)
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
		color = MaterialTheme.colors.primary,
		modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp))
}

@Preview(name = "Instructions preview", showBackground = true)
@Composable
fun SettingsPreview() {
	Settings(
		settingsData = SettingsData(), settingChange = {}
	)
}