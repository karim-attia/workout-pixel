package ch.karimattia.workoutpixel.composables

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.karimattia.workoutpixel.*
import ch.karimattia.workoutpixel.R
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.Goal
import ch.karimattia.workoutpixel.data.*
import ch.karimattia.workoutpixel.ui.theme.TextBlack
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import java.util.stream.Collectors

private const val TAG: String = "GoalDetailView"

@Composable
fun GoalDetailView(
	goal: Goal,
	updateAfterClick: () -> Unit,
	deleteGoal: (Goal) -> Unit,
	updateGoal: (updatedGoal: Goal, navigateUp: Boolean) -> Unit,
	pastClickViewModel: PastClickViewModel,
) {
	val pastClicks by pastClickViewModel.pastClicks(goal.uid).observeAsState(initial = listOf())

	Column(
		modifier = Modifier
			//.padding(PaddingValues(top = 6.dp, bottom = 40.dp))
			// TODO
			.verticalScroll(rememberScrollState())
	) {
		Spacer(modifier = Modifier.height(6.dp))
		GoalOverviewView(
			goal = goal,
			updateAfterClick = updateAfterClick
		)
		if (!goal.hasValidAppWidgetId()) {
			GoalDetailNoWidgetCard(goal = goal, deleteGoal = deleteGoal)
		}
		PastClicks(
			goal = goal,
			updateGoal = updateGoal,
			updatePastClick = { pastClickViewModel.updatePastClick(it) },
			pastClicks = pastClicks
		)
		Spacer(modifier = Modifier.height(40.dp))
	}
}

@Composable
fun GoalOverviewView(
	goal: Goal,
	updateAfterClick: () -> Unit,
) {
	CardWithTitle(
		title = "Goal overview",
	)
	{
		// Preview and rest
		Row(verticalAlignment = Alignment.CenterVertically) {
			GoalPreview(
				goal = goal, onClick = updateAfterClick
			)
			// Rest
			Column(
				modifier = Modifier.padding(start = 6.dp, top = 3.dp)
			)
			{
				IntervalIconAndText(goal)
				LastDoneIconAndText(goal)
			}
		}
	}
}

@Composable
fun GoalDetailNoWidgetCard(
	goal: Goal,
	deleteGoal: (Goal) -> Unit,
) {
	CardWithTitle(
		title = "No widget for this goal"
	) {
		val dialogState = rememberMaterialDialogState()

		Infobox(text = "There is no widget for this goal on your homescreen. Add a new widget and connect it to this goal to keep the data.")
		Button(
			onClick = { dialogState.show() },
			modifier = Modifier.padding(top = 8.dp)
		) {
			Text(text = "Delete goal", modifier = Modifier.padding(end = 16.dp))
			Icon(imageVector = Icons.Filled.Backspace, contentDescription = null)
		}
		MaterialDialog(dialogState = dialogState, buttons = {
			positiveButton(text = "Confirm", onClick = { deleteGoal(goal) })
			negativeButton(text = "Cancel")
		}) {
			title(text = "Do you really want to delete this goal?")
			message(text = "This will irreversibly remove the goal including all its data like past clicks.")
		}
	}
}

@Composable
fun PastClicks(
	goal: Goal,
	updateGoal: (updatedGoal: Goal, navigateUp: Boolean) -> Unit,
	updatePastClick: (PastWorkout) -> Unit,
	pastClicks: List<PastWorkout>
) {
	CardWithTitle(
		// TODO: If numberOfPastClicks > 50, declare it. Or implement some paging.
		title = "Past clicks",
	) {
		PastClickList(
			goal = goal,
			updateGoal = updateGoal,
			updatePastClick = updatePastClick,
			pastClicks = pastClicks
		)
	}
}

@Composable
fun PastClickList(
	goal: Goal,
	updateGoal: (updatedGoal: Goal, navigateUp: Boolean) -> Unit,
	updatePastClick: (PastWorkout) -> Unit,
	pastClicks: List<PastWorkout>
) {
	val numberOfPastClicks = pastClicks.size
	if (numberOfPastClicks > 0) {
		Column {
			PastClickEntry(date = "Date", time = "Time", bold = true)
			Divider(color = Color(TextBlack), thickness = 1.dp)

			for (i in 0 until minOf(numberOfPastClicks, 50)) {
				PastClickEntry(
					pastClick = pastClicks[i],
					togglePastClick = {
						updatePastClick(it)
						// If this change causes a new last workout time, do all the necessary updates.
						// setNewLastWorkout not needed because this is done in updateGoal. Instead, lastWorkout could be set directly.
						if (goal.setNewLastWorkout(lastWorkoutBasedOnActiveWorkouts(pastClicks))) {
							updateGoal(goal, false)
						}
					}
				)
				Divider(color = Color(TextBlack), thickness = 0.5.dp)
			}
		}
	} else {
		Text(
			text = stringResource(R.string.you_have_never_completed_this_goal_as_soon_as_you_click_on_the_widget_the_click_will_show_up_here),
			fontSize = 14.sp
		)
	}
	Spacer(modifier = Modifier.padding(end = 6.dp))
}

@Composable
fun PastClickEntry(
	pastClick: PastWorkout,
	togglePastClick: (PastWorkout) -> Unit,
) {
	PastClickEntry(
		date = CommonFunctions.dateBeautiful(pastClick.workoutTime),
		time = CommonFunctions.timeBeautiful(pastClick.workoutTime),
		icon = if (pastClick.active) {
			Icons.Filled.Delete
		} else {
			Icons.Filled.Undo
		},
		active = pastClick.active,
		togglePastClick = {
			pastClick.isActive = !pastClick.isActive
			togglePastClick(pastClick)
		},
	)
}

@Composable
fun PastClickEntry(
	date: String,
	time: String,
	icon: ImageVector? = null,
	bold: Boolean = false,
	active: Boolean = true,
	togglePastClick: () -> Unit = { },
) {
	Row {
		val fontWeight = if (bold) {
			FontWeight.Bold
		} else {
			null
		}
		val textDecoration = if (!active) {
			TextDecoration.LineThrough
		} else {
			null
		}
		Text(
			text = date,
			fontSize = 14.sp,
			fontWeight = fontWeight,
			style = TextStyle(textDecoration = textDecoration),
			modifier = Modifier
				.width(72.dp)
				.padding(start = 4.dp)
				.align(Alignment.CenterVertically)
		)
		Text(
			text = time,
			fontSize = 14.sp,
			fontWeight = fontWeight,
			style = TextStyle(textDecoration = textDecoration),
			modifier = Modifier
				.width(72.dp)
				.padding(end = 12.dp)
				.align(Alignment.CenterVertically)
		)
		Spacer(modifier = Modifier.weight(1f))
		if (icon != null) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.align(alignment = Alignment.CenterVertically)
					.clickable { togglePastClick() }
			)
		}
	}
}

fun lastWorkoutBasedOnActiveWorkouts(pastClicks: List<PastWorkout>): Long {
	val activeWorkoutsOrderedByWorkoutTime = pastClicks.stream()
		.filter { clickedWorkout: PastWorkout -> clickedWorkout.active }.collect(
			Collectors.toList()
		)

	// If there still is an active past workout, take the latest one to set the last workout time
	return if (activeWorkoutsOrderedByWorkoutTime.isNotEmpty()) {
		Log.v(TAG, "Size: " + activeWorkoutsOrderedByWorkoutTime.size)
		activeWorkoutsOrderedByWorkoutTime[0].getWorkoutTime()
	}
	// Otherwise, set it to 0.
	else {
		Log.v(TAG, "Size: " + activeWorkoutsOrderedByWorkoutTime.size + ", no remaining workout")
		0L
	}
}

@Preview("GoalDetailViewPreview")
@Composable
fun GoalDetailViewPreview() {
	GoalDetailView(
		goal = CommonFunctions.testData()[0],
		updateAfterClick = {},
		deleteGoal = {},
		updateGoal = { _, _ -> },
		pastClickViewModel = viewModel()
	)
}