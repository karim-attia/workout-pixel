package ch.karimattia.workoutpixel.composables

import android.appwidget.AppWidgetManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.GoalPreviewWithColor
import ch.karimattia.workoutpixel.SettingsData
import ch.karimattia.workoutpixel.core.Constants.STATUS_NONE
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.ui.theme.*
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ARGBPickerState
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title


private const val TAG: String = "Instructions"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Settings(
    // settingsData: SettingsData
) {
    val goal = Goal(
        uid = 0,
        appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID,
        title = "Select\ncolor",
        lastWorkout = 0,
        intervalBlue = 2,
        intervalRed = 2,
        showDate = false,
        showTime = false,
        status = STATUS_NONE
    )

    val settingsData by remember {
        mutableStateOf(
            SettingsData(
                Green = Color(Green),
                Blue = Color(Blue),
                Red = Color(Red),
                Purple = Color(Purple)
            )
        )
    }

    Column {
        FreeStandingTitle(text = "Colors", modifier = Modifier.padding(all = 8.dp))
        Divider(color = Color(TextBlack), thickness = 0.5.dp)
        ColorSelection(
            titleText = "Done",
            subtitleText = "Show this color for when you reached your goal",
            goal = goal,
            color = settingsData.Green,
            onColorSelected = { settingsData.Green = it },
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
) {
    // Probably higher up and settings object is directly modified.
    // selected color
    // popup open

    val dialogState = rememberMaterialDialogState()
    MaterialDialog(dialogState = dialogState, buttons = {
        positiveButton("Ok")
        negativeButton("Cancel")
    }) {
        title(text = "Choose color")
        colorChooser(
            colors = listOf(color).plus(ColorPalette.Primary),
            argbPickerState = ARGBPickerState.WithAlphaSelector,
            onColorSelected = { onColorSelected(it) },
        )
    }

    Row(modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
        Column(verticalArrangement = Arrangement.Center) {
            Text(text = titleText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = subtitleText, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
        GoalPreviewWithColor(goal = goal, color = color, onClick = { dialogState.show() })
    }
}

@Preview(name = "Instructions preview")
@Composable
fun SettingsPreview() {
    Settings(
        //settingsData = SettingsData(Green = Color(Green), Blue = Color(Blue), Red = Color(Red), Purple = Color(Purple))
    )
}

