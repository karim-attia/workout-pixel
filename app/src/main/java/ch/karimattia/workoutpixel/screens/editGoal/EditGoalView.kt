package ch.karimattia.workoutpixel.screens.editGoal

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.karimattia.workoutpixel.core.testGoals
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.screens.Lambdas

@Suppress("unused")
private const val TAG: String = "EditGoalView"

@Composable
fun EditGoalView(
	initialGoal: Goal,
	isFirstConfigure: Boolean,
	lambdas: Lambdas,
	modifier: Modifier = Modifier,
	goalsWithoutWidget: List<Goal> = emptyList(),
) {
	Log.d(TAG, "START")
	Log.d(TAG, "isFirstConfigure: $isFirstConfigure")

	// For some reason, any change in the goal recomposes this page
	// It even updates the initialGoal
	// For some weird reason, only the first ever change is reflected in this original goal. -> Doesn't happen if internal goal is used.
	// This original goal is then also shown if the back button is clicked.
	// This doesn't happen if the reflected data is saved via the update button.
	// Copying the goal values fixes this...
	// TODO: Save all changes directly to the DB to simplify this.

	// https://stackoverflow.com/questions/63956058/jetpack-compose-state-modify-class-property (Last answer for policy)
	val (editGoalViewGoal: Goal, setValueEditGoalViewGoal: (Goal) -> Unit) = remember {
		mutableStateOf(
			value = initialGoal.copy(),
			policy = neverEqualPolicy()
		)
	}
	// If the caller sends a new initialGoal, update editGoalViewGoal with it. If it's the same one (e.g. through a recomposition), keep the values edited by the user.
	val lastValueOfInitialGoal = remember { mutableStateOf(initialGoal) }
	if (lastValueOfInitialGoal.value != initialGoal) {
		setValueEditGoalViewGoal(initialGoal)
		lastValueOfInitialGoal.value = initialGoal
	}

	Column(
		modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(start = 8.dp, end = 8.dp, bottom = 12.dp)
	) {
		val modifierWithPadding = Modifier.padding(top = 8.dp)

		Hints(
			modifier = modifierWithPadding,
		)
		var connectExistingGoal by remember { mutableStateOf(false) }
		if (isFirstConfigure && goalsWithoutWidget.isNotEmpty()) {
			ConnectExistingGoal(
				goalsWithoutWidget = goalsWithoutWidget,
				connectGoal = { updatedConnectedGoal ->
					updatedConnectedGoal.appWidgetId = initialGoal.appWidgetId
					lambdas.updateGoal(updatedConnectedGoal)
					lambdas.navigateUp(false)
				},
				modifier = modifierWithPadding,
			)
			connectExistingGoal = true
		} else {
			connectExistingGoal = false
		}
		SetUpYourWidget(
			setUpYourWidgetGoal = editGoalViewGoal,
			setUpYourWidgetGoalChange = { changedGoal ->
				// Copying goal or policy required: https://stackoverflow.com/questions/63956058/jetpack-compose-state-modify-class-property
				// setValueEditGoalViewGoal(changedGoal.copy())

				setValueEditGoalViewGoal(changedGoal)
			},
			isFirstConfigure = isFirstConfigure,
			connectExistingGoal = connectExistingGoal,
			modifier = modifierWithPadding,
		)
		WidgetConfigurationPreview(
			editGoalViewGoal = editGoalViewGoal,
			isFirstConfigure = isFirstConfigure,
			settingsData = lambdas.settingsData,
			modifier = modifierWithPadding,
		)
		Spacer(
			modifier = Modifier
                .height(10.dp)
                .weight(weight = 1f, fill = true)
                .background(Color.Blue)
		)
		AddUpdateWidgetButton(
			isFirstConfigure = isFirstConfigure,
			insertUpdateWidget = {
				if (isFirstConfigure) lambdas.insertGoal(editGoalViewGoal)
				else {
					lambdas.updateGoal(editGoalViewGoal)
					lambdas.navigateUp(false)
				}
			},
			modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.End),
		)
	}
}

@Composable
fun FreeStandingTitle(
	text: String,
	modifier: Modifier = Modifier,
	topPadding: Dp = 32.dp,
	firstTitle: Boolean = false,
) {
	Text(
		text = text,
		// fontSize = 16.sp,

		fontWeight = FontWeight.Bold,
		style = MaterialTheme.typography.titleLarge,
		modifier = modifier.padding(
			top = if (!firstTitle) topPadding else 12.dp,
			bottom = 8.dp
		)
	)
}

@Preview(name = "EditGoalView preview", showBackground = true)
@Composable
fun EditGoalViewPreview() {
	EditGoalView(
		initialGoal = testGoals[0],
		isFirstConfigure = false,
		lambdas = Lambdas()
	)
}
