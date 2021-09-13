package ch.karimattia.workoutpixel

import android.appwidget.AppWidgetManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.Goal
import ch.karimattia.workoutpixel.ui.theme.Blue
import ch.karimattia.workoutpixel.ui.theme.Green
import ch.karimattia.workoutpixel.ui.theme.Purple
import ch.karimattia.workoutpixel.ui.theme.Red

private const val TAG: String = "Instructions"

@Composable
fun Settings(
	settingsData: SettingsData
) {
	val goal = Goal(
		AppWidgetManager.INVALID_APPWIDGET_ID,
		"",
		0,
		2,
		2,
		false,
		false,
		CommonFunctions.STATUS_NONE
	)

	Column() {
		FreeStandingTitle(text = "Colors")
		ColorSelection(
			titleText = "Done",
			subtitleText = "Show this color for when you reached your goal",
			goal = goal,
			color = settingsData.Green,
			)
	}
}

@Composable
fun ColorSelection(
	titleText: String,
	subtitleText: String,
	goal: Goal,
	color: Int,
) {
	Row() {
		Column() {
			Text(text = titleText, fontWeight = FontWeight.Bold)
			Text(text = subtitleText, fontSize = 12.sp)
		}
		GoalPreviewWithColor(goal = goal, color = color)
	}
}

@Preview(name = "Instructions preview")
@Composable
fun SettingsPreview() {
	Settings(
		settingsData = SettingsData(Green = Green, Blue = Blue, Red = Red, Purple = Purple, )
	)
}

class SettingsData internal constructor(
	val Green: Int,
	val Blue: Int,
	val Red: Int,
	val Purple: Int,
)

