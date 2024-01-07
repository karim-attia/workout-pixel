package ch.karimattia.workoutpixel.screens.allGoals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.karimattia.workoutpixel.R
import ch.karimattia.workoutpixel.core.Screens
import ch.karimattia.workoutpixel.core.dateBeautiful
import ch.karimattia.workoutpixel.core.last3Am
import ch.karimattia.workoutpixel.core.testGoals
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.screens.CardWithTitle
import ch.karimattia.workoutpixel.screens.GoalPreview
import ch.karimattia.workoutpixel.screens.Lambdas
import java.util.*

@Suppress("unused")
private const val TAG: String = "AllGoals"

@Composable
fun GoalList(
    goals: List<Goal>,
    lambdas: Lambdas,
) {
    Column(
        modifier = Modifier
			.verticalScroll(rememberScrollState())
			.padding(top = 6.dp, bottom = 90.dp)
    ) {
        if (goals.isNotEmpty()) {
            for (goal in goals) {
                // contentPadding = PaddingValues(top = 6.dp, bottom = 40.dp),
                GoalCard(
                    goal = goal,
                    lambdas = lambdas
                )
            }
        } else {
            InstructionsCard(navigateTo = {
                lambdas.navigateTo(
                    Screens.Instructions.name,
                    null,
                    true
                )
            })
        }

    }
}

@Composable
fun GoalCard(
    goal: Goal,
    lambdas: Lambdas,
) {

    CardWithTitle(
        title = goal.title,
        onClick = { lambdas.navigateTo(Screens.GoalDetailView.name, goal, false) })
    {
        // Preview and rest
        Row(
            verticalAlignment = Alignment.Top,
            // modifier = Modifier.padding(all = 1.dp),
        ) {
/*
			GoalPreview(
				goal = goal,
				onClick = { lambdas.updateAfterClick(goal) },
				settingsData = lambdas.settingsData,
				modifier = Modifier.padding(all = 4.dp)
			)
*/
            Row(
                // horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
					.fillMaxWidth()
					.padding(top = 1.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StatisticsCount(goal = goal)
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatisticsLastDone(goal = goal)
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatisticsInterval(goal = goal)
                }
            }
        }

    }
}

@Composable
fun StatisticsInterval(goal: Goal) {
    Statistics(
        label = "Every",
        info = goal.intervalBlue.toString(),
        unit = if (goal.intervalBlue == 1) "day" else "days"
    )
}

@Composable
fun StatisticsLastDone(goal: Goal) {
    Statistics(
        label = "Last done",
        info = dateBeautiful(
            date = goal.lastWorkout,
            agoWording = false,
            locale = Locale.getDefault()
        ),
        unit = if (goal.lastWorkout < last3Am()) "ago" else "",
    )
}

@Composable
fun StatisticsCount(goal: Goal) {
    Statistics(
        label = "Done",
        info = "43",
        unit = "times"
    )
}

@Composable
fun Statistics(
    label: String,
    info: String,
    unit: String,
) {
    val infoAndUnit: AnnotatedString = buildAnnotatedString {
        withStyle(MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Medium).toSpanStyle()) { append(info) }
        withStyle(MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium).toSpanStyle()) { append(" $unit") }
    }

    Column()
    {
        Text(
            text = label.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall,
			color = Color.Gray,

            modifier = Modifier
            //.padding(top = 2.dp, start = 2.dp, end = 2.dp)
        )

        Text(
            text = infoAndUnit,
            fontWeight = FontWeight.Bold,
            )

    }
}


@Composable
fun InstructionsCard(
    navigateTo: () -> Unit,
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
    GoalList(
        goals = testGoals,
        lambdas = Lambdas()
    )
}
