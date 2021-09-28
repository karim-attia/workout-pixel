package ch.karimattia.workoutpixel.composables

import android.graphics.Color.argb
import android.graphics.Color.rgb
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import ch.karimattia.workoutpixel.SettingsData
import ch.karimattia.workoutpixel.SettingsRepository
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsViewModel
import ch.karimattia.workoutpixel.ui.theme.TextBlack
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ARGBPickerState
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title


private const val TAG: String = "Settings"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Settings(
	settingsData: SettingsData,
	settingChange: (SettingsData) -> Unit
) {
	val goal = Goal(title = "Select\ncolor")
	Column {
		FreeStandingTitle(text = "Colors", modifier = Modifier.padding(all = 8.dp))
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
		ColorSelection(
			titleText = "Done",
			subtitleText = "Show this color when you reached your goal",
			goal = goal,
			color = settingsData.colorDone(),
			onColorSelected = {	settingChange(SettingsData(settingsData = settingsData, colorDoneInt = argb(it.alpha, it.red, it.green, it.blue))) },
		)
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
		ColorSelection(
			titleText = "First interval",
			subtitleText = "Show this color to gently remind you that it's time to do your thing again",
			goal = goal,
			color = settingsData.colorFirstInterval(),
			onColorSelected = {	settingChange(SettingsData(settingsData = settingsData, colorFirstIntervalInt = argb(it.alpha, it.red, it.green, it.blue))) },
		)
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
		ColorSelection(
			titleText = "Second interval",
			subtitleText = "Show this color when your goal is overdue",
			goal = goal,
			color = settingsData.colorSecondInterval(),
			onColorSelected = {	settingChange(SettingsData(settingsData = settingsData, colorSecondIntervalInt = argb(it.alpha, it.red, it.green, it.blue))) },
		)
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
		ColorSelection(
			titleText = "Initial",
			subtitleText = "Show this color for goals that you have never clicked",
			goal = goal,
			color = settingsData.colorInitial(),
			onColorSelected = {	settingChange(SettingsData(settingsData = settingsData, colorInitialInt = argb(it.alpha, it.red, it.green, it.blue))) },
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
		Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
			Text(text = titleText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
			Text(text = subtitleText, fontSize = 12.sp)
		}
		GoalPreviewWithColor(goal = goal, color = color, onClick = { dialogState.show() })
	}
}


@Preview(name = "Instructions preview", showBackground = true)
@Composable
fun SettingsPreview() {
	Settings(
		settingsData = SettingsData(), settingChange = {}
	)
}