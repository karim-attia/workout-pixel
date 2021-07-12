package ch.karimattia.workoutpixel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.Goal
import ch.karimattia.workoutpixel.ui.theme.InfoColor

@Composable
fun GoalDetailView(
	goal: Goal,
	updateAfterClick: () -> Unit,
	deleteGoal: () -> Unit,
	setAppBarTitle: (appBarText: String) -> Unit,
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
			Infobox(text = "There is no widget for this goal on your homescreen. Add a new widget and connect it to this goal to keep the data.")
			Button(
				onClick = { deleteGoal() },
				modifier = Modifier.padding(top = 8.dp)
			) {
				Text(text = "Delete goal")
			}
			// TODO
		}
	}
}

@Composable
fun Infobox(
	text: String
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.clip(shape = RoundedCornerShape(4.dp))
			.background(Color(InfoColor))
			.padding(4.dp)
	) {
		Icon(
			imageVector = Icons.Filled.Info,
			contentDescription = "Info icon",
			modifier = Modifier
				.size(32.dp)
		)
		Text(
			text = text,
			fontSize = 14.sp,
			modifier = Modifier.padding(start = 4.dp)
		)
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
