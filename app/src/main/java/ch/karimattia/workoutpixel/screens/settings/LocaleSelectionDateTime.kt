package ch.karimattia.workoutpixel.screens.settings

import androidx.compose.runtime.Composable
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData
import ch.karimattia.workoutpixel.screens.GoalPreview
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.listItemsSingleChoice
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import java.util.Locale

@Suppress("unused")
private const val TAG: String = "LocaleSelectionDateTime"

@Composable
fun LocaleSelectionDateTime(
    goal: Goal,
    settingsData: SettingsData,
    format: Format,
    onChoiceChange: (language: String?, country: String?) -> Unit,
	// All locales
    listLocales: Array<Locale> = Locale.getAvailableLocales(),
) {
	fun transformDateTimeFormatToStringTryCatch(locale: Locale): String = try {
		format.transformDateTimeFormatToString(locale)
	} catch (e: Exception) {
		"errorerrorerrorerrorerror"
	}

	val dialogState = rememberMaterialDialogState()
    MaterialDialog(dialogState = dialogState, buttons = {
        positiveButton("Ok")
        negativeButton("Cancel")
    }) {
        /**
         * Map of all nice strings to their locales
         * map: Make sure, we only include Locales that are re-creatable by language and country.
         * associateBy: Map unique nice example string to their locales.
         * filterKeys: remove entries with strings that are too long.
         */
        /**
         * Map of all nice strings to their locales
         * map: Make sure, we only include Locales that are re-creatable by language and country.
         * associateBy: Map unique nice example string to their locales.
         * filterKeys: remove entries with strings that are too long.
         */
        val mapFormatsToLocales: Map<String, Locale> = listLocales
            .map { Locale(it.language, it.country) }
            .associateBy { transformDateTimeFormatToStringTryCatch(it) }
            .filterKeys { it.length < format.maxStringSizeFilter }
        // Only the list of the nice strings from above map.
        val listOfFormats: List<String> = mapFormatsToLocales.keys.toList()
        // Add the default value at the first position.
        val listOfFormatsWithDefault: List<String> =
            listOf("Default: ${transformDateTimeFormatToStringTryCatch(Locale.getDefault())}").plus(
                listOfFormats
            )

        title(text = "Choose ${format.label} format for widget")
        listItemsSingleChoice(
            list = listOfFormatsWithDefault,
            // 1. Build Locale from data in settingsData. 2. Convert into nice example string. 3. Lookup this string in the list.
            initialSelection = if (format.isLocaleDefault) 0 else listOfFormatsWithDefault.indexOf(
                transformDateTimeFormatToStringTryCatch(format.locale)
            ),
            onChoiceChange = { selectedFormatIndex ->
                // If default is chosen, return null, else look up the value.
                if (selectedFormatIndex == 0) onChoiceChange(null, null) else {
                    val selectedFormat: String = listOfFormatsWithDefault[selectedFormatIndex]
                    // selectedFormat is from list which stems from map, thus should be not null.
                    val chosenLocale: Locale = mapFormatsToLocales[selectedFormat] ?: format.locale
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
            goal = goal.copy(
                title = "Your goal",
                showDate = format.showDate,
                showTime = format.showTime,
                // lastWorkout = remember { System.currentTimeMillis()- intervalInMilliseconds(2) }
            ),
            settingsData = settingsData,
            onClick = { dialogState.show() },
        )
    }
}