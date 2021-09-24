package ch.karimattia.workoutpixel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.Goal
import ch.karimattia.workoutpixel.core.WorkoutPixelScreen
import java.util.*

private const val TAG: String = "AllGoals"

@Composable
fun AllGoals(
	goals: List<Goal>,
	updateAfterClick: (Goal) -> Unit,
	navigateTo: (destination: String, goal: Goal?) -> Unit,
) {
	GoalList(
		goals = goals,
		updateAfterClick = updateAfterClick,
		navigateTo = navigateTo,
	)
}

@Composable
fun GoalList(
	goals: List<Goal>,
	updateAfterClick: (Goal) -> Unit,
	navigateTo: (destination: String, goal: Goal?) -> Unit,
) {
	Column(
		modifier = Modifier
			.verticalScroll(rememberScrollState())
			.padding(top = 6.dp, bottom = 40.dp)
	) {
		if (goals.isNotEmpty()) {
			for (i in goals.indices) {
				// contentPadding = PaddingValues(top = 6.dp, bottom = 40.dp),
				GoalCard(
					goal = goals[i],
					updateAfterClick = { updateAfterClick(goals[i]) },
					navigateTo = navigateTo
				)
			}
		} else {
			InstructionsCard(navigateTo = { navigateTo(WorkoutPixelScreen.Instructions.name, null) })
		}

	}
}

@Composable
fun GoalCard(
	goal: Goal,
	updateAfterClick: () -> Unit,
	navigateTo: (destination: String, goal: Goal?) -> Unit,
) {
	Card(
		backgroundColor = Color.White,
		elevation = 4.dp,
		modifier = Modifier
			.padding(vertical = 4.dp, horizontal = 8.dp)
			.fillMaxWidth()
			.clickable {
				navigateTo(WorkoutPixelScreen.GoalDetailView.name, goal)
			}
	) {
		// Preview and rest
		Row(
			verticalAlignment = Alignment.Top,
			modifier = Modifier.padding(all = 1.dp),
		) {
			GoalPreview(
				goal = goal,
				onClick = updateAfterClick,
				modifier = Modifier.padding(all = 4.dp)
			)
			// Rest
			Column(modifier = Modifier.padding(start = 4.dp))
			{
				GoalTitle(goal)
				Row(modifier = Modifier.padding(top = 1.dp)) {
					IntervalIconAndText(goal)
					LastDoneIconAndText(goal)
				}
			}
		}
	}
}

@Composable
fun GoalTitle(goal: Goal) {
	Text(
		text = goal.title.uppercase(Locale.getDefault()),
		fontSize = 16.sp,
		fontWeight = FontWeight.Bold,
		modifier = Modifier
			.padding(top = 2.dp, start = 2.dp, end = 2.dp)
	)
}

@Composable
fun IntervalIconAndText(goal: Goal) {
	IconAndText(
		Icons.Filled.DateRange,
		20, 2,
		goal.everyWording()
	)
}

@Composable
fun LastDoneIconAndText(goal: Goal) {
	IconAndText(
		Icons.Filled.Done,
		22, 0,
		CommonFunctions.dateBeautiful(goal.lastWorkout)
	)
}

@Composable
fun IconAndText(icon: ImageVector, size: Int, iconPaddingLeft: Int, text: String) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Icon(
			imageVector = icon, contentDescription = null,
			Modifier
				.padding(end = iconPaddingLeft.dp)
				.height(size.dp)
				.width(size.dp)
		)
		Text(
			text = text,
			fontSize = 14.sp,
			modifier = Modifier
				.defaultMinSize(100.dp)
				.padding(start = 5.dp)
		)
	}
}

@Composable
fun InstructionsCard(
	navigateTo: () -> Unit
) {
	CardWithTitle(
		title = stringResource(id = R.string.no_goals_defined_yet),
		onClick = navigateTo,
	) {
		Text(text = stringResource(id = R.string.go_to_instructions_tab))
	}
}

@Preview(name = "MyScreen preview")
@Composable
fun AllGoalsPreview() {
	AllGoals(
		goals = CommonFunctions.testData(),
		updateAfterClick = {},
		navigateTo = { _, _ -> },
	)
}
