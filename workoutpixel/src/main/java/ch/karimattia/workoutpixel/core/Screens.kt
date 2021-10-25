package ch.karimattia.workoutpixel.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Screen metadata for WorkoutPixel.
 */
enum class Screens(
	val icon: ImageVector? = null,
	val bottomNavigation: Boolean = false,
	val showWhenPinningPossible: Boolean = true,
	val displayName: String? = null,
	val topAppBarName: String? = null,
	val showBackNavigation: Boolean = false,
	val showEditIcon: Boolean = false,
	val showSettingsIcon: Boolean = false,
	val showFloatingActionButton: Boolean = false,
	val fullScreen: Boolean = false,
) {
	Onboarding(
		displayName = "Onboarding",
		fullScreen = true,
	),
	Instructions(
		icon = Icons.Filled.Info,
		bottomNavigation = true,
		showWhenPinningPossible = false,
		displayName = "Instructions",
		topAppBarName = "Workout Pixel",
	),
	GoalsList(
		icon = Icons.Filled.Done,
		bottomNavigation = true,
		displayName = "Goals",
		showSettingsIcon = true,
		showFloatingActionButton = true,
		topAppBarName = "Workout Pixel",
	),
	Progress(
		icon = Icons.Filled.TrendingUp,
		bottomNavigation = true,
		displayName = "Progress",
		showSettingsIcon = true,
		topAppBarName = "Workout Pixel",
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
		displayName = "Settings",
	),
	;

	companion object {
		fun fromRoute(route: String?): Screens =
			when (route?.substringBefore("/")) {
				GoalsList.name -> GoalsList
				Onboarding.name -> Onboarding
				Instructions.name -> Instructions
				GoalDetailView.name -> GoalDetailView
				EditGoalView.name -> EditGoalView
				Settings.name -> Settings
				Progress.name -> Progress
				null -> GoalsList
				else -> throw IllegalArgumentException("Route $route is not recognized.")
			}
	}
}