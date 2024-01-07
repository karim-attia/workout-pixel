package ch.karimattia.workoutpixel.screens.editGoal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.karimattia.workoutpixel.core.plural
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.screens.GoalTitleTextField
import ch.karimattia.workoutpixel.screens.SwitchWithText

@ExperimentalComposeUiApi
@Composable
fun SetUpYourWidget(
    setUpYourWidgetGoal: Goal,
    setUpYourWidgetGoalChange: (Goal) -> Unit,
    isFirstConfigure: Boolean,
    modifier: Modifier = Modifier,
    connectExistingGoal: Boolean = false,
) {
    FreeStandingTitle(
        text = if (!isFirstConfigure) {
            "Edit your goal"
        } else if (!connectExistingGoal) {
            "Set up your widget"
        } else {
            "Set up a new goal"
        }
    )

    Text(
        text = "Describe your goal in 1–2 words:",
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
    )

    GoalTitleTextField(
        title = setUpYourWidgetGoal.title,
        onValueChange = {
            setUpYourWidgetGoal.title = it
            setUpYourWidgetGoalChange(setUpYourWidgetGoal)
        },
        modifier = modifier
    )

    Spacer(modifier = Modifier.height(24.dp))
    /* Interval */
    Text(
        text = "How often do you want to reach your goal? Every",
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier,
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        val buttonModifier: Modifier = Modifier
            .padding(end = 12.dp)
            .width(28.dp)
            .height(28.dp)
        val textModifier: Modifier = Modifier
            .padding(end = 12.dp)
        FilledTonalButton(
            onClick = {
                setUpYourWidgetGoal.intervalBlue--
                setUpYourWidgetGoalChange(setUpYourWidgetGoal)
            },
            enabled = setUpYourWidgetGoal.intervalBlue >= 2,
            contentPadding = PaddingValues(all = 0.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = buttonModifier,
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            text = setUpYourWidgetGoal.intervalBlue.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = textModifier
        )
        FilledTonalButton(
            onClick = {
                setUpYourWidgetGoal.intervalBlue++
                setUpYourWidgetGoalChange(setUpYourWidgetGoal)
            },
            contentPadding = PaddingValues(all = 0.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = buttonModifier,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            text = plural(setUpYourWidgetGoal.intervalBlue, "day") + ".",
            style = MaterialTheme.typography.bodyMedium,
            modifier = textModifier,
        )
    }


    /* Date & time */
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "Should the widget show when you reached your goal the last time?",
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier,
    )
    SwitchWithText(
        description = "Show date",
        checked = setUpYourWidgetGoal.showDate,
        onCheckedChange = {
            setUpYourWidgetGoal.showDate = it
            setUpYourWidgetGoalChange(setUpYourWidgetGoal)
        },
    )
    SwitchWithText(
        description = "Show time",
        checked = setUpYourWidgetGoal.showTime,
        onCheckedChange = {
            setUpYourWidgetGoal.showTime = it
            setUpYourWidgetGoalChange(setUpYourWidgetGoal)
        },
    )
}