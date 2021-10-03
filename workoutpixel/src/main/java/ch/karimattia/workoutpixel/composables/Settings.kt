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
import ch.karimattia.workoutpixel.core.dateTimeBeautiful
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
	Column {
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
		SettingsTitle(text = "Colors")
		ColorSelection(
			titleText = "Done goals",
			subtitleText = "Color of goals that you recently reached",
			goal = goal,
			color = settingsData.colorDone(),
			onColorSelected = { settingChange(settingsData.copy(colorDoneInt = argb(it.alpha, it.red, it.green, it.blue))) },
		)
		ColorSelection(
			titleText = "Pending goals",
			subtitleText = "Color to remind you to reach this goal",
			goal = goal,
			color = settingsData.colorFirstInterval(),
			onColorSelected = { settingChange(settingsData.copy(colorFirstIntervalInt = argb(it.alpha, it.red, it.green, it.blue))) },
		)
		ColorSelection(
			titleText = "Due goals",
			subtitleText = "Color of goals that are pending since 2+ days",
			goal = goal,
			color = settingsData.colorSecondInterval(),
			onColorSelected = { settingChange(settingsData.copy(colorSecondIntervalInt = argb(it.alpha, it.red, it.green, it.blue))) },
		)
		ColorSelection(
			titleText = "New goals",
			subtitleText = "Color of goals that you never clicked",
			goal = goal,
			color = settingsData.colorInitial(),
			onColorSelected = { settingChange(settingsData.copy(colorInitialInt = argb(it.alpha, it.red, it.green, it.blue))) },
		)
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
		SettingsTitle(text = "Date and time format")
		CountrySelection(
			goal = goal,
			settingsData = settingsData,
			onChoiceChange = { settingChange(settingsData.copy(country = it)) },
		)
		LanguageSelection(
			goal = goal,
			settingsData = settingsData,
			onChoiceChange = { settingChange(settingsData.copy(language = it)) },
		)
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
		SettingsTitle(text = "Date and time format")

		LocaleSelectionDate(
			locale = settingsData.country,
			goal = goal,
			settingsData = settingsData,
			onChoiceChange = { settingChange(settingsData.copy(country = it)) },
		)
		LocaleSelectionTime(
			locale = settingsData.country,
			goal = goal,
			settingsData = settingsData,
			onChoiceChange = { settingChange(settingsData.copy(country = it)) },
		)



		Divider(color = Color(TextBlack), thickness = 0.5.dp)
	}
}

@Composable
fun CountrySelection(
	goal: Goal,
	settingsData: SettingsData,
	onChoiceChange: (String) -> Unit,
) {
	val dialogState = rememberMaterialDialogState()
	val listCountries: List<String> = Locale.getISOCountries().asList()
	val listDisplayCountry: List<String> = listCountries.map { Locale("", it).displayCountry }
	MaterialDialog(dialogState = dialogState, buttons = {
		positiveButton("Ok")
		negativeButton("Cancel")
	}) {
		Log.d(TAG, "listCountries: $listCountries")
		Log.d(TAG, "listDisplayCountry: $listDisplayCountry")
		title(text = "Choose country")
		listItemsSingleChoice(
			list = listDisplayCountry,
			initialSelection = listCountries.indexOf(settingsData.country),
			onChoiceChange = { onChoiceChange(listCountries[it]) },
		)
	}

	SettingsEntry(
		titleText = "Country",
		subtitleText = "${settingsData.country} - ${
			dateTimeBeautiful((System.currentTimeMillis()),
				Locale(settingsData.language, settingsData.country))
		}",
		onClick = { dialogState.show() },
	) {
		GoalPreview(
			goal = goal.copy(title = "Your goal",
				showDate = true,
				showTime = true,
				lastWorkout = remember { System.currentTimeMillis() }),
			settingsData = settingsData,
		)
	}
}

@Composable
fun LanguageSelection(
	goal: Goal,
	settingsData: SettingsData,
	onChoiceChange: (String) -> Unit,
) {
	val dialogState = rememberMaterialDialogState()
	val all = Locale.getAvailableLocales()
	fun listLanguages(): List<String> {
		val list = mutableListOf<String>()
		for (locale: Locale in all) {
			if (locale.country == settingsData.country) {
				list += locale.language
			}
		}
		return list
	}
	MaterialDialog(dialogState = dialogState, buttons = {
		positiveButton("Ok")
		negativeButton("Cancel")
	}) {
		Log.d(TAG, "listCountries: ${listLanguages()}")
		title(text = "Choose language")
		listItemsSingleChoice(
			list = listLanguages(),
			initialSelection = listLanguages().indexOf(settingsData.country),
			onChoiceChange = { onChoiceChange(listLanguages()[it]) },
		)
	}

	SettingsEntry(
		titleText = "Language",
		subtitleText = "${settingsData.country} - ${
			dateTimeBeautiful((System.currentTimeMillis()),
				Locale(settingsData.language, settingsData.country))
		}",
		onClick = { dialogState.show() },
	) {
		GoalPreview(
			goal = goal.copy(title = "Your goal",
				showDate = true,
				showTime = true,
				lastWorkout = remember { System.currentTimeMillis() }),
			settingsData = settingsData,
		)
	}
}

@Composable
fun LocaleSelectionDate(
	locale: String,
	goal: Goal,
	settingsData: SettingsData,
	onChoiceChange: (String) -> Unit,
) {
	val dialogState = rememberMaterialDialogState()
	val listDisplayName: List<String> = Locale.getAvailableLocales().map {
		it.displayName + try {
			dateTimeBeautiful((System.currentTimeMillis()), it)
		} catch (e: Exception) {
		}
	}.toList()
	val listLocales = Locale.getAvailableLocales()
	val listDateFormats: List<String> = listLocales.map {
		try {
			dateBeautiful((System.currentTimeMillis()), it)
		} catch (e: Exception) {
			"errorerrorerrorerrorerrorerror"
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
			initialSelection = listDateFormatsWithoutDuplicates.indexOf(settingsData.country),
			onChoiceChange = { timeFormat ->
				Log.d(TAG, "timeFormat: $timeFormat")
				Log.d(TAG, "listTimeFormatsWithoutDuplicates[timeFormat]: ${listDateFormatsWithoutDuplicates[timeFormat]}")
				Log.d(TAG,
					"listTimeFormats.indexOf(listTimeFormatsWithoutDuplicates[timeFormat]): ${listDateFormats.indexOf(listDateFormatsWithoutDuplicates[timeFormat])}")
				val thisLocale = listLocales[listDateFormats.indexOf(listDateFormatsWithoutDuplicates[timeFormat])]
				Log.d(TAG, "thisLocale: $thisLocale")
				onChoiceChange(thisLocale.language)
				Log.d(TAG, "thisLocale.language: ${thisLocale.language}")
			},
		)
	}

	SettingsEntry(
		titleText = "Choose date format for widget",
		subtitleText = "$locale - ${dateTimeBeautiful((System.currentTimeMillis()), Locale(locale))}",
		onClick = { dialogState.show() },
	) {
		GoalPreview(
			goal = goal.copy(title = "Your goal",
				showDate = true,
				showTime = true,
				lastWorkout = remember { System.currentTimeMillis() }),
			settingsData = settingsData,
		)
	}
}

@Composable
fun LocaleSelectionTime(
	locale: String,
	goal: Goal,
	settingsData: SettingsData,
	onChoiceChange: (String) -> Unit,
) {
	val dialogState = rememberMaterialDialogState()
	val listDisplayName: List<String> = Locale.getAvailableLocales().map {
		it.displayName + try {
			dateTimeBeautiful((System.currentTimeMillis()), it)
		} catch (e: Exception) {
		}
	}.toList()
	val listLocales = Locale.getAvailableLocales()
	val listTimeFormats: List<String> = listLocales.map {
		try {
			timeBeautiful((System.currentTimeMillis()), it) + " / " + timeBeautiful((System.currentTimeMillis() + 12 * 60 * 60 * 1000), it)
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
			initialSelection = listTimeFormatsWithoutDuplicates.indexOf(settingsData.country),
			onChoiceChange = { timeFormat ->
				Log.d(TAG, "timeFormat: $timeFormat")
				Log.d(TAG, "listTimeFormatsWithoutDuplicates[timeFormat]: ${listTimeFormatsWithoutDuplicates[timeFormat]}")
				Log.d(TAG,
					"listTimeFormats.indexOf(listTimeFormatsWithoutDuplicates[timeFormat]): ${listTimeFormats.indexOf(listTimeFormatsWithoutDuplicates[timeFormat])}")
				val thisLocale = listLocales[listTimeFormats.indexOf(listTimeFormatsWithoutDuplicates[timeFormat])]
				Log.d(TAG, "thisLocale: $thisLocale")
				onChoiceChange(thisLocale.language)
				Log.d(TAG, "thisLocale.language: ${thisLocale.language}")
			},
		)
	}

	SettingsEntry(
		titleText = "Date and time format in widget",
		subtitleText = "$locale - ${dateTimeBeautiful((System.currentTimeMillis()), Locale(locale))}",
		onClick = { dialogState.show() },
	) {
		GoalPreview(
			goal = goal.copy(title = "Your goal",
				showDate = true,
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
		GoalPreviewWithColor(goal = goal, color = color)
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