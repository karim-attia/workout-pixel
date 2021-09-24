package ch.karimattia.workoutpixel.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Screen metadata for WorkoutPixel.
 */
enum class WorkoutPixelScreen(
	val icon: ImageVector? = null,
	val bottomNavigation: Boolean = false,
	val displayName: String? = null,
	val showBackNavigation: Boolean = false,
	val showEditIcon: Boolean = false,
	val showSettingsIcon: Boolean = false,
) {
	Instructions(
		icon = Icons.Filled.Info,
		bottomNavigation = true,
		displayName = "Instructions",
	),
	GoalsList(
		icon = Icons.Filled.Done,
		bottomNavigation = true,
		displayName = "Your goals",
		showSettingsIcon = true,
	),
	GoalDetailView(
		showBackNavigation = true,
		showEditIcon = true,
	),
	EditGoalView(
		showBackNavigation = true,
	),
	Settings(
		showBackNavigation = true,
		displayName = "Workout Pixel settings",
	),
	;

	companion object {
		fun fromRoute(route: String?): WorkoutPixelScreen =
			when (route?.substringBefore("/")) {
				GoalsList.name -> GoalsList
				Instructions.name -> Instructions
				GoalDetailView.name -> GoalDetailView
				EditGoalView.name -> EditGoalView
				Settings.name -> Settings
				null -> GoalsList
				else -> throw IllegalArgumentException("Route $route is not recognized.")
			}
	}
}