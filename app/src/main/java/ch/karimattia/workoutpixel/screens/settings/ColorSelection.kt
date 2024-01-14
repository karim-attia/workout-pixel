package ch.karimattia.workoutpixel.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData
import ch.karimattia.workoutpixel.screens.GoalPreview
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ARGBPickerState
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import java.util.ArrayList
import java.util.LinkedHashSet
import java.util.Locale

@Suppress("unused")
private const val TAG: String = "ColorSelection"

@Composable
fun ColorSelection(
    titleText: String,
    subtitleText: String,
    goal: Goal,
    currentColor: Color,
    resetColor: Color,
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
            // First two colors are currentColor, resetColor. If any of the colorPalette colors are the same, don't show them.
            colors = ArrayList(
                LinkedHashSet(
                    listOf(
                        currentColor,
                        resetColor
                    ).plus(ColorPalette.Primary).plus(ColorPalette.Accent)
                )
            ),
            argbPickerState = ARGBPickerState.WithAlphaSelector,
            onColorSelected = { onColorSelected(it) },
        )
    }

    SettingsEntry(
        titleText = titleText,
        subtitleText = subtitleText,
        onClick = { dialogState.show() }) {
        GoalPreview(
            goal = goal,
            backgroundColor = currentColor,
            settingsData = settingsData,
            onClick = { dialogState.show() })
    }
}