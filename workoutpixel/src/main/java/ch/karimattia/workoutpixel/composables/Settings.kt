package ch.karimattia.workoutpixel.composables

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
	settingsViewModel: SettingsViewModel
) {
	// val settingsViewModel: SettingsViewModel = SettingsViewModel(settingsRepository = SettingsModule.providesDataStore(context = LocalContext.current))

	val settingsData by settingsViewModel.settingsData.observeAsState()
	settingsData?.let {
		Settings(
			settingsData = it,
			// settingChange = {_,_ ->} // setting, int -> settingsViewModel.saveIntSetting(setting, int) }
			settingChange = { settingsViewModel.updateColorDone(it) }
		)
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Settings(
	settingsData: SettingsData,
	settingChange: (Int) -> Unit
) {
	val goal = Goal(title = "Select\ncolor")

	// Log.d(TAG, "low : ${ if(settingsData.colorDoneInt == Green){"Green"}else{settingsData.colorDoneInt}}")

	Column {
		FreeStandingTitle(text = "Colors", modifier = Modifier.padding(all = 8.dp))
		Divider(color = Color(TextBlack), thickness = 0.5.dp)
		ColorSelection(
			titleText = "Done",
			subtitleText = "Show this color for when you reached your goal",
			goal = goal,
			color = settingsData.colorDone(),
			onColorSelected = { settingChange(rgb(it.red, it.green, it.blue)) },
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

/*
@Preview(name = "Instructions preview")
@Composable
fun SettingsPreview() {
	Settings(
		settingsData = null, settingChange = { _, _ -> }
	)
}

*/
