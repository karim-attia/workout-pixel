package ch.karimattia.workoutpixel

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.Goal

@Composable
fun GoalDetailView(
	goal: Goal,
	updateAfterClick: () -> Unit,
	deleteGoal: () -> Unit,
	setAppBarTitle: (appBarText:String) -> Unit,
) {
	setAppBarTitle(goal.title)
	Column(
		modifier = Modifier.padding(PaddingValues(top = 6.dp, bottom = 40.dp))
	) {
		GoalOverviewView(
			goal = goal,
			updateAfterClick = updateAfterClick
		)
		if (!goal.hasValidAppWidgetId()) {
			GoalDetailNoWidgetCard(goal = goal, deleteGoal = deleteGoal)
		}
	}
}

@Composable
fun GoalOverviewView(
	goal: Goal,
	updateAfterClick: () -> Unit,
) {
	Card(
		backgroundColor = Color.White,
		elevation = 4.dp,
		modifier = Modifier
			.padding(vertical = 4.dp, horizontal = 8.dp)
			.fillMaxWidth()
	) {
		Column(
			modifier = Modifier
				// Card values
				.padding(
					top = 4.dp,
					bottom = 8.dp,
					start = 8.dp,
					end = 8.dp,
				)
		) {
			CardTitle(
				text = "Goal overview"
			)
			// Preview and rest
			Row {
				GoalPreview(
					goal = goal, updateAfterClick = updateAfterClick
				)
				// Rest
				Column(
					modifier = Modifier.padding(start = 4.dp)
				)
				{
					IntervalIconAndText(goal)
					LastDoneIconAndText(goal)
				}
			}
		}
	}
}

@Composable
fun CardTitle(
	text: String
) {
	Text(
		text = text,
		fontSize = 20.sp,
		fontWeight = FontWeight.Bold,
		modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
	)

}

@Composable
fun GoalDetailNoWidgetCard(
	goal: Goal,
	deleteGoal: () -> Unit,
) {
	Card(
		backgroundColor = Color.White,
		elevation = 4.dp,
		modifier = Modifier
			.padding(vertical = 4.dp, horizontal = 8.dp)
			.fillMaxWidth()
	) {
		Column(
			modifier = Modifier
				// Card values
				.padding(
					top = 4.dp,
					bottom = 8.dp,
					start = 8.dp,
					end = 8.dp,
				)
		) {
			CardTitle(
				text = "No widget for this goal"
			)
			// TODO
		}
	}
}


@Preview("MyScreen preview")
@Composable
fun GoalDetailViewPreview() {
	GoalDetailView(
		goal = CommonFunctions.testData()[0],
		updateAfterClick = {},
		deleteGoal = {},
		setAppBarTitle = {},
	)
}
