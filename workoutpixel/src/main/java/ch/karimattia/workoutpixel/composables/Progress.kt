package ch.karimattia.workoutpixel.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.karimattia.workoutpixel.R
import ch.karimattia.workoutpixel.core.*
import ch.karimattia.workoutpixel.data.*
import ch.karimattia.workoutpixel.ui.theme.TextBlack

@Suppress("unused")
private const val TAG: String = "Progress"

@Composable
fun Progress(
	// Move into lambdas?
	pastClickViewModelAssistedFactory: PastClickViewModelAssistedFactory,
	pastClickViewModel: PastClickViewModel = viewModel(factory = provideFactory(pastClickViewModelAssistedFactory, Constants.INVALID_GOAL_UID)),
	pastClicks: List<PastClick> = pastClickViewModel.pastClicks,
	goals: List<Goal>,
	lambdas: Lambdas,
) {
	Log.d(TAG, "pastClicks: ${pastClicks.size}")
	val pastClicksAndGoal: List<PastClickAndGoal> = pastClicks.map { pastClick ->
		// Null assertion is safe since DB deletes pastClicks when goal is deleted.
		PastClickAndGoal(pastClick, goalFromGoalsByUid(goalUid = pastClick.widgetUid, goals = goals)!!)
	}
	Progress(
		goals = goals,
		updatePastClick = { updatedPastClick -> pastClickViewModel.updatePastClick(updatedPastClick) },
		pastClicksAndGoal = pastClicksAndGoal,
		lambdas = lambdas,
	)
}

@Composable
fun Progress(
	goals: List<Goal>,
	updatePastClick: (PastClick) -> Unit,
	pastClicksAndGoal: List<PastClickAndGoal>,
	lambdas: Lambdas,
) {
	Column(
		modifier = Modifier
			//.padding(PaddingValues(top = 6.dp, bottom = 40.dp))
			// TODO
			.verticalScroll(rememberScrollState())
	) {
		Spacer(modifier = Modifier.height(6.dp))

		ProgressBar(
			goals = goals,
			lambdas = lambdas,
		)
		Spacer(modifier = Modifier.height(6.dp))

		ProgressPastClicks(
			updatePastClick = updatePastClick,
			pastClicksAndGoal = pastClicksAndGoal,
			lambdas = lambdas,
		)

		Spacer(modifier = Modifier.height(40.dp))
	}
}

@Composable
fun ProgressBar(
	goals: List<Goal>,
	lambdas: Lambdas,
) {
	CardWithTitle(
		title = "Your progress today",
	) {
		val statusDistribution = enumValues<Status>().associateWith { goals.filter { goal -> goal.status() == it }.size }
		Row(modifier = Modifier
			.fillMaxWidth()
		) {
			statusDistribution.forEach {
				if (it.value > 0) Text(
					text = it.value.toString(),
					textAlign = TextAlign.Center,
					fontWeight = FontWeight(500),
					color = Color.White,
					modifier = Modifier
						.background(color = getColorFromStatus(it.key, settingsData = lambdas.settingsData))
						.padding(vertical = 2.dp)
						.fillMaxSize()
						.weight(it.value.toFloat())//it.value.toFloat())
						.align(Alignment.CenterVertically)
						//.wrapContentSize(Alignment.Center)
				)
			}
		}
	}
}


@Composable
fun ProgressPastClicks(
	pastClicksAndGoal: List<PastClickAndGoal>,
	updatePastClick: (PastClick) -> Unit,
	lambdas: Lambdas,
) {
	CardWithTitle(
		title = "Past clicks",
	) {
		ProgressPastClickList(
			updatePastClick = updatePastClick,
			pastClicksAndGoal = pastClicksAndGoal,
			lambdas = lambdas,
		)
	}
}

@Composable
fun ProgressPastClickList(
	pastClicksAndGoal: List<PastClickAndGoal>,
	updatePastClick: (PastClick) -> Unit,
	lambdas: Lambdas,
) {
	val numberOfPastClicks = pastClicksAndGoal.size
	if (numberOfPastClicks > 0) {
		Column {
			ProgressPastClickEntry(goal = "Goal", date = "Date", time = "Time", bold = true)
			Divider(color = Color(TextBlack), thickness = 1.dp)

			for (pastClickAndGoal in pastClicksAndGoal) {
				key(pastClickAndGoal.pastClick.uid) {
					ProgressPastClickEntry(
						pastClickAndGoal = pastClickAndGoal,
						togglePastClick = {},
						lambdas = lambdas,
					)
					Divider(color = Color(TextBlack), thickness = 0.5.dp)
				}
			}
		}
	}
	if (numberOfPastClicks == 0) {
		Text(
			text = stringResource(R.string.you_have_never_completed_this_goal_as_soon_as_you_click_on_the_widget_the_click_will_show_up_here),
			fontSize = 14.sp
		)
	}
	Spacer(modifier = Modifier.padding(end = 6.dp))
}


@Composable
fun ProgressPastClickEntry(
	pastClickAndGoal: PastClickAndGoal,
	togglePastClick: () -> Unit,
	lambdas: Lambdas,
) {
	ProgressPastClickEntry(
		goal = pastClickAndGoal.goal.title,
		date = dateBeautiful(pastClickAndGoal.pastClick.workoutTime, lambdas.settingsData.dateLocale()),
		time = timeBeautiful(pastClickAndGoal.pastClick.workoutTime, lambdas.settingsData.timeLocale()),
		icon = null, /*if (pastClickAndGoal.pastClick.isActive) {
			Icons.Filled.Delete
		} else {
			Icons.Filled.Undo
		}*/
		active = pastClickAndGoal.pastClick.isActive,
		togglePastClick = togglePastClick,
	)
}

@Composable
fun ProgressPastClickEntry(
	goal: String,
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
			text = goal,
			fontSize = 14.sp,
			fontWeight = fontWeight,
			style = TextStyle(textDecoration = textDecoration),
			modifier = Modifier
				.width(90.dp)
				.padding(start = 4.dp, end = 12.dp)
				.align(Alignment.CenterVertically)
		)
		Spacer(modifier = Modifier.weight(1f))
		Text(
			text = date,
			fontSize = 14.sp,
			fontWeight = fontWeight,
			style = TextStyle(textDecoration = textDecoration),
			modifier = Modifier
				.width(90.dp)
				.padding(start = 4.dp, end = 12.dp)
				.align(Alignment.CenterVertically)
		)
		Text(
			text = time,
			fontSize = 14.sp,
			fontWeight = fontWeight,
			style = TextStyle(textDecoration = textDecoration),
			modifier = Modifier
				.width(50.dp)
				.padding(end = 12.dp)
				.align(Alignment.CenterVertically)
		)
	}
}

