package ch.karimattia.workoutpixel.screens.allGoals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.karimattia.workoutpixel.R
import ch.karimattia.workoutpixel.core.Screens
import ch.karimattia.workoutpixel.core.testGoals
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.screens.CardWithTitle
import ch.karimattia.workoutpixel.screens.Lambdas

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
fun InstructionsCard(
    navigateTo: () -> Unit,
) {
    CardWithTitle(
        title = stringResource(id = R.string.no_goals_defined_yet),
        onClick = navigateTo,
    ) {
        Text(
            text = stringResource(id = R.string.go_to_instructions_tab),
            style = MaterialTheme.typography.bodyMedium,
        )
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
