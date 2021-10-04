package ch.karimattia.workoutpixel.composables

import android.graphics.Color.argb
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

private const val TAG: String = "Settings"

@Composable
fun Settings(
	settingsData: SettingsData,
	settingChange: (SettingsData) -> Unit,
) {
	val goal = Goal(title = "Select\ncolor")
	Log.d(TAG, settingsData.dateLanguage + " " + settingsData.dateCountry)
	Log.d(TAG, settingsData.timeLanguage + " " + settingsData.timeCountry)
	Column {
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
		SettingsTitle(text = "Colors")
		ColorSelection(
			titleText = "Done goals",
			subtitleText = "Color of goals that you recently reached",
			goal = goal,
			color = settingsData.colorDone(),
			onColorSelected = { settingChange(settingsData.copy(colorDoneInt = argb(it.alpha, it.red, it.green, it.blue))) },
			settingsData = settingsData,
		)
		ColorSelection(
			titleText = "Pending goals",
			subtitleText = "Color to remind you to reach this goal",
			goal = goal,
			color = settingsData.colorFirstInterval(),
			onColorSelected = { settingChange(settingsData.copy(colorFirstIntervalInt = argb(it.alpha, it.red, it.green, it.blue))) },
			settingsData = settingsData,
		)
		ColorSelection(
			titleText = "Due goals",
			subtitleText = "Color of goals that are pending since 2+ days",
			goal = goal,
			color = settingsData.colorSecondInterval(),
			onColorSelected = { settingChange(settingsData.copy(colorSecondIntervalInt = argb(it.alpha, it.red, it.green, it.blue))) },
			settingsData = settingsData,
		)
		ColorSelection(
			titleText = "New goals",
			subtitleText = "Color of goals that you never clicked",
			goal = goal,
			color = settingsData.colorInitial(),
			onColorSelected = { settingChange(settingsData.copy(colorInitialInt = argb(it.alpha, it.red, it.green, it.blue))) },
			settingsData = settingsData,
		)
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
		SettingsTitle(text = "Date and time format")

		LocaleSelectionDate(
			goal = goal,
			settingsData = settingsData,
			onChoiceChange = { language: String, country: String ->
				settingChange(settingsData.copy(dateLanguage = language,
					dateCountry = country))
			},
		)
		LocaleSelectionTime(
			goal = goal,
			settingsData = settingsData,
			onChoiceChange = { language: String, country: String ->
				settingChange(settingsData.copy(timeLanguage = language,
					dateCountry = country))
			},
		)

		Divider(color = Color(TextBlack), thickness = 0.5.dp)
	}
}

@Composable
fun LocaleSelectionDate(
	goal: Goal,
	settingsData: SettingsData,
	onChoiceChange: (language: String, country: String) -> Unit,
) {
	val dialogState = rememberMaterialDialogState()
	val listLocales = Locale.getAvailableLocales()
	val listDateFormats: List<String> = listLocales.map {
		try {
			dateBeautiful(2010651132000L, it)
		} catch (e: Exception) {
			"errorerror"
		}
	}
	val listDateFormatsWithoutDuplicates: List<String> = ArrayList(LinkedHashSet(listDateFormats.filter { it.length < 9 })).toList()
	MaterialDialog(dialogState = dialogState, buttons = {
		positiveButton("Ok")
		negativeButton("Cancel")
	}) {
		Log.d(TAG, "listTimeFormatsWithoutDuplicates: $listDateFormatsWithoutDuplicates")
		title(text = "Choose date format")
		listItemsSingleChoice(
			list = listDateFormatsWithoutDuplicates,
			initialSelection = 0, //listDateFormatsWithoutDuplicates.indexOf(settingsData.dateCountry),
			onChoiceChange = { timeFormat ->
				Log.d(TAG, "timeFormat: $timeFormat")
				Log.d(TAG, "listTimeFormatsWithoutDuplicates[timeFormat]: ${listDateFormatsWithoutDuplicates[timeFormat]}")
				Log.d(TAG,
					"listTimeFormats.indexOf(listTimeFormatsWithoutDuplicates[timeFormat]): ${listDateFormats.indexOf(listDateFormatsWithoutDuplicates[timeFormat])}")
				val thisLocale = listLocales[listDateFormats.indexOf(listDateFormatsWithoutDuplicates[timeFormat])]
				Log.d(TAG, "thisLocale: $thisLocale")
				onChoiceChange(thisLocale.language, thisLocale.country)
				Log.d(TAG, "thisLocale.language: ${thisLocale.language}")
			},
		)
	}

	SettingsEntry(
		titleText = "Date format",
		subtitleText = dateBeautiful(2010651132000L, settingsData.dateLocale()),
		onClick = { dialogState.show() },
	) {
		GoalPreview(
			goal = goal.copy(
				title = "Your goal",
				showDate = true,
				lastWorkout = remember { System.currentTimeMillis() }),
			settingsData = settingsData,
		)
	}
}

@Composable
fun LocaleSelectionTime(
	goal: Goal,
	settingsData: SettingsData,
	onChoiceChange: (language: String, country: String) -> Unit,
) {
	val dialogState = rememberMaterialDialogState()
	val listLocales = Locale.getAvailableLocales()
	val listTimeFormats: List<String> = listLocales.map {
		try {
			timeBeautiful(8460000L, it) + " / " + timeBeautiful(date = 8460000L + 12 * 60 * 60 * 1000, it)
		} catch (e: Exception) {
			"errorerrorerrorerrorerrorerror"
		}
	}
	val listTimeFormatsWithoutDuplicates: List<String> = ArrayList(LinkedHashSet(listTimeFormats.filter { it.length < 23 })).toList()
	MaterialDialog(dialogState = dialogState, buttons = {
		positiveButton("Ok")
		negativeButton("Cancel")
	}) {
		Log.d(TAG, "listTimeFormatsWithoutDuplicates: $listTimeFormatsWithoutDuplicates")
		title(text = "Choose time format for widget")
		listItemsSingleChoice(
			list = listTimeFormatsWithoutDuplicates,
			initialSelection = 0, //listTimeFormatsWithoutDuplicates.indexOf(settingsData.country),
			onChoiceChange = { timeFormat ->
				Log.d(TAG, "timeFormat: $timeFormat")
				Log.d(TAG, "listTimeFormatsWithoutDuplicates[timeFormat]: ${listTimeFormatsWithoutDuplicates[timeFormat]}")
				Log.d(TAG,
					"listTimeFormats.indexOf(listTimeFormatsWithoutDuplicates[timeFormat]): ${listTimeFormats.indexOf(listTimeFormatsWithoutDuplicates[timeFormat])}")
				val thisLocale = listLocales[listTimeFormats.indexOf(listTimeFormatsWithoutDuplicates[timeFormat])]
				Log.d(TAG, "thisLocale: $thisLocale")
				onChoiceChange(thisLocale.language, thisLocale.country)
				Log.d(TAG, "thisLocale.language: ${thisLocale.language}")
			},
		)
	}

	SettingsEntry(
		titleText = "Time format",
		subtitleText = timeBeautiful(8460000L, settingsData.timeLocale()) + " / " + timeBeautiful(date = 8460000L + 12 * 60 * 60 * 1000,
			settingsData.timeLocale()),
		onClick = { dialogState.show() },
	) {
		GoalPreview(
			goal = goal.copy(title = "Your goal",
				showTime = true,
				lastWorkout = remember { System.currentTimeMillis() }),
			settingsData = settingsData,
		)
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