package ch.karimattia.workoutpixel.screens.editGoal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ch.karimattia.workoutpixel.core.Status
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData
import ch.karimattia.workoutpixel.screens.GoalPreview

@Composable
fun WidgetConfigurationPreview(
    editGoalViewGoal: Goal,
    isFirstConfigure: Boolean,
    settingsData: SettingsData,
    modifier: Modifier = Modifier,
) {
    // 12dp because the checkbox above has 4 dp clickable area and then 16 is too much.
    FreeStandingTitle(text = "Preview")
    GoalPreview(
        goal = if (isFirstConfigure) editGoalViewGoal.copy(statusOverride = Status.GREEN) else editGoalViewGoal,
        settingsData = settingsData,
        modifier = modifier,
    )
}