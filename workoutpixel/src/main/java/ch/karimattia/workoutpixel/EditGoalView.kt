package ch.karimattia.workoutpixel

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.Goal

@Composable
fun EditGoalView(
	goal: Goal,
	setAppBarTitle: (appBarText: String) -> Unit,
) {
	setAppBarTitle(goal.title)
	Text(text = "This is the EditGoalView for goal " + goal.title)
}


@Preview("EditGoalView preview")
@Composable
fun EditGoalViewPreview() {
	EditGoalView(
		goal = CommonFunctions.testData()[0],
		setAppBarTitle = {},
	)
}
