package ch.karimattia.workoutpixel.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.karimattia.workoutpixel.R
import ch.karimattia.workoutpixel.core.Status
import ch.karimattia.workoutpixel.core.dateBeautiful
import ch.karimattia.workoutpixel.core.getColorFromStatus
import ch.karimattia.workoutpixel.core.timeBeautiful
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.PastClickAndGoal
import ch.karimattia.workoutpixel.screens.CardWithTitle
import ch.karimattia.workoutpixel.screens.Lambdas

@Suppress("unused")
private const val TAG: String = "Progress"

@Composable
fun Progress(
	progressViewModel: ProgressViewModel = hiltViewModel(),
	goals: List<Goal>,
	pastClicksAndGoal: List<PastClickAndGoal> = progressViewModel.activePastClicksAndGoalLastWeek,
	lambdas: Lambdas,
) {

	Progress(
		goals = goals,
		pastClicksAndGoal = pastClicksAndGoal,
		lambdas = lambdas,
	)
}

@Composable
fun Progress(
	goals: List<Goal>,
	pastClicksAndGoal: List<PastClickAndGoal>,
	lambdas: Lambdas,
) {
	Column(
		modifier = Modifier
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
			pastClicksAndGoal = pastClicksAndGoal,
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
		val statusDistribution: Map<Status, Int> =
			enumValues<Status>().associateWith { goals.filter { goal -> goal.status() == it }.size }
		Row(
			modifier = Modifier
				.fillMaxWidth()
		) {
			statusDistribution.forEach {
				if (it.value > 0) Text(
					text = it.value.toString(),
					textAlign = TextAlign.Center,
					fontWeight = FontWeight(500),
					color = Color.White,
					modifier = Modifier
						.background(
							color = getColorFromStatus(
								it.key,
								settingsData = lambdas.settingsData
							)
						)
						.padding(vertical = 2.dp)
						.fillMaxSize()
						.weight(it.value.toFloat())//it.value.toFloat())
						.align(Alignment.CenterVertically)
				)
			}
		}
	}
}


@Composable
fun ProgressPastClicks(
	pastClicksAndGoal: List<PastClickAndGoal>,
) {
	CardWithTitle(
		title = "Activities this week",
	) {
		ProgressPastClickList(
			pastClicksAndGoal = pastClicksAndGoal,
		)
	}
}

@Composable
fun ProgressPastClickList(
	pastClicksAndGoal: List<PastClickAndGoal>,
) {
	val numberOfPastClicks = pastClicksAndGoal.size
	if (numberOfPastClicks > 0) {
		Column {
			ProgressPastClickEntry(goal = "Goal", date = "When", time = "", isHeader = true)
			Divider(color = MaterialTheme.colorScheme.primary, thickness = 1.dp)

			for (pastClickAndGoal in pastClicksAndGoal) {
				key(pastClickAndGoal.pastClick.uid) {
					ProgressPastClickEntry(
						pastClickAndGoal = pastClickAndGoal,
					)
					Divider(color = MaterialTheme.colorScheme.primary, thickness = 0.5.dp)
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
) {
	ProgressPastClickEntry(
		goal = pastClickAndGoal.goal.title,
		date = dateBeautiful(pastClickAndGoal.pastClick.workoutTime),
		time = timeBeautiful(pastClickAndGoal.pastClick.workoutTime),
	)
}

@Composable
fun ProgressPastClickEntry(
	goal: String,
	date: String,
	time: String,
	isHeader: Boolean = false,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp)
	) {
		val style = if (isHeader) MaterialTheme.typography.labelSmall
		else MaterialTheme.typography.bodyMedium

		/* Goal title */
		Text(
			text = goal,
			fontSize = 14.sp,
			style = style,
			modifier = Modifier
				.padding(start = 4.dp, end = 12.dp)
				.align(Alignment.CenterVertically)
				.weight(weight = 1f)
		)
		/* Date */
		Text(
			text = date + if (time != "") ", " + time else "",
			fontSize = 14.sp,
			style = style,
			modifier = Modifier
				.width(140.dp)
				.padding(start = 4.dp, end = 12.dp)
				.align(Alignment.CenterVertically)
		)
		/* Time */
		/*Text(
			text = time,
			fontSize = 14.sp,
			style = style,
			modifier = Modifier
				.width(50.dp)
				.padding(end = 12.dp)
				.align(Alignment.CenterVertically)
		)*/
	}
}

