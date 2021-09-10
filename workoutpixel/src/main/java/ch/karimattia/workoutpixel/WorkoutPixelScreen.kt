package ch.karimattia.workoutpixel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
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
) {
	Instructions(
		icon = Icons.Filled.Info,
		bottomNavigation = true,
		displayName = "Instructions"
	),
	GoalsList(
		icon = Icons.Filled.Done,
		bottomNavigation = true,
		displayName = "Your goals"
	),
	GoalDetailView(
		showBackNavigation = true,
		showEditIcon = true,
	),
	EditGoalView(
		showBackNavigation = true,
	)
	;

	companion object {
		fun fromRoute(route: String?): WorkoutPixelScreen =
			when (route?.substringBefore("/")) {
				GoalsList.name -> GoalsList
				Instructions.name -> Instructions
				GoalDetailView.name -> GoalDetailView
				EditGoalView.name -> EditGoalView
				null -> GoalsList
				else -> throw IllegalArgumentException("Route $route is not recognized.")
			}
	}
}

enum class AppBarAction(
	val icon: ImageVector,
	val bottomNavigation: Boolean,
) {
	EditGoal(
		icon = Icons.Filled.Edit,
		bottomNavigation = true
	),
}
