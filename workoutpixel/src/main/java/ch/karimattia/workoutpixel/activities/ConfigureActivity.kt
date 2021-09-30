package ch.karimattia.workoutpixel.activities

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import ch.karimattia.workoutpixel.SettingsData
import ch.karimattia.workoutpixel.composables.EditGoalView
import ch.karimattia.workoutpixel.core.ContextFunctions
import ch.karimattia.workoutpixel.core.GoalWidgetActions
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.GoalRepository
import ch.karimattia.workoutpixel.data.GoalViewModel
import ch.karimattia.workoutpixel.data.SettingsViewModel
import ch.karimattia.workoutpixel.ui.theme.WorkoutPixelTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG: String = "ConfigureCompose"

@AndroidEntryPoint
@ExperimentalComposeUiApi
class ConfigureActivity : ComponentActivity() {

	@Inject
	lateinit var goalWidgetActionsFactory: GoalWidgetActions.Factory
	private fun goalWidgetActions(goal: Goal): GoalWidgetActions = goalWidgetActionsFactory.create(goal)
	@Inject
	lateinit var contextFunctions: ContextFunctions
	@Inject
	lateinit var goalRepository: GoalRepository

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val kotlinGoalViewModel by viewModels<GoalViewModel>()
		val settingsViewModel: SettingsViewModel by viewModels()

		val goal = Goal()

		// Find the widget id and whether it is a reconfigure activity from the intent.
		val extras = intent.extras

		// Get the AppWidgetId from the launcher if there is one provided. Else exit.
		// Set the result to CANCELED. This will cause the widget host to cancel out of the widget placement if the user presses the back button.
		setResult(RESULT_CANCELED)
		if (extras != null) {
			goal.appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
		} else {
			Log.d(TAG, "extras = null")
		}

		// If this activity was started with an intent without an app widget ID, finish with an error.
		if (goal.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			Log.d(TAG, "AppWidgetId is invalid.")
			finishAndRemoveTask()
			return
		}

		setContent {
			ConfigureActivityCompose(
				initialGoal = goal,
				isFirstConfigure = true,
				addUpdateWidget = {
					// Insert the goal into the DB and also update the widget. The update is done there, because the OnClickListener needs the goal uid. This is returned by the DB.
					kotlinGoalViewModel.insertGoal(it)
					// goal has the wrong uid here, but this doesn't matter for setWidgetAndFinish
					setWidgetAndFinish(goal = it)
				},
				connectGoal = {
					kotlinGoalViewModel.updateGoal(it)
					goalWidgetActions(it).runUpdate(true)
					setWidgetAndFinish(goal = it)
				},
				goalsWithoutWidget = goalRepository.loadGoalsWithoutValidAppWidgetId().collectAsState(initial = emptyList()).value,
				settingsData = settingsViewModel.settingsData.observeAsState(initial = SettingsData()).value,
			)
		}
	}

	private fun setWidgetAndFinish(goal: Goal, context: Context = this) {
		// Make sure we pass back the original appWidgetId.
		val resultValue = Intent()
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, goal.appWidgetId)
		setResult(RESULT_OK, resultValue)
		Toast.makeText(
			context,
			"Widget created. Click on it to register a workout.",
			Toast.LENGTH_LONG
		).show()
		finishAndRemoveTask()
	}
}

@ExperimentalComposeUiApi
@Composable
fun ConfigureActivityCompose(
	initialGoal: Goal,
	isFirstConfigure: Boolean,
	goalsWithoutWidget: List<Goal>,
	addUpdateWidget: (Goal) -> Unit,
	connectGoal: (Goal) -> Unit,
	settingsData: SettingsData,
) {
	WorkoutPixelTheme(
		darkTheme = false,
	) {
		Scaffold(
			topBar = { TopAppBar(title = { Text(text = "Add a widget for your goal") }) },
		) {
			EditGoalView(
				initialGoal = initialGoal,
				isFirstConfigure = isFirstConfigure,
				goalsWithoutWidget = goalsWithoutWidget,
				addUpdateWidget = addUpdateWidget,
				connectGoal = connectGoal,
				settingsData = settingsData,
				// modifier = Modifier.padding(innerPadding),
			)
		}
	}
}



