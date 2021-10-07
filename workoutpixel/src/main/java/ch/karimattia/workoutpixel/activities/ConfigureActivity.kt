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
import androidx.lifecycle.lifecycleScope
import ch.karimattia.workoutpixel.composables.EditGoalView
import ch.karimattia.workoutpixel.core.WidgetActions
import ch.karimattia.workoutpixel.data.*
import ch.karimattia.workoutpixel.ui.theme.WorkoutPixelTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG: String = "ConfigureCompose"

@AndroidEntryPoint
@ExperimentalComposeUiApi
class ConfigureActivity : ComponentActivity() {

	@Inject
	lateinit var widgetActionsFactory: WidgetActions.Factory
	private fun widgetActions(goal: Goal): WidgetActions = widgetActionsFactory.create(goal)

	@Inject
	lateinit var goalRepository: GoalRepository

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val goalViewModel by viewModels<GoalViewModel>()
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
			// collectedGoal can return null despite of what lint says.
			@Suppress("KotlinDeprecation")
			val collectedGoal: Goal = goalRepository.loadGoalByAppWidgetIdFlow(goal).collectAsState(initial = goal).value ?: goal
			val isFirstConfigure = collectedGoal == goal
			Log.d(TAG, "isFirstConfigure: $isFirstConfigure")

			ConfigureActivityCompose(
				initialGoal = if (isFirstConfigure) goal else collectedGoal,
				isFirstConfigure = isFirstConfigure,
				insertGoal = { updatedGoal ->
					lifecycleScope.launch {
						// Insert the goal into the DB and also update the widget.
						updatedGoal.uid = goalViewModel.insertGoal(updatedGoal)
						widgetActions(goal = updatedGoal).runUpdate(true)
						setWidgetAndFinish(goal = updatedGoal, isFirstConfigure = isFirstConfigure)
					}
				},
				updateGoal = {updatedGoal ->
					lifecycleScope.launch {
						// Insert the goal into the DB and also update the widget.
						goalViewModel.updateGoal(updatedGoal)
						widgetActions(goal = updatedGoal).runUpdate(true)
						setWidgetAndFinish(goal = updatedGoal, isFirstConfigure = isFirstConfigure)
					}
				},
				goalsWithoutWidget = goalRepository.loadGoalsWithoutValidAppWidgetId().collectAsState(initial = emptyList()).value,
				settingsData = settingsViewModel.settingsData.observeAsState(initial = SettingsData()).value,
			)
		}
	}

	private fun setWidgetAndFinish(goal: Goal, isFirstConfigure: Boolean, context: Context = this) {
		// Make sure we pass back the original appWidgetId.
		val resultValue = Intent()
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, goal.appWidgetId)
		setResult(RESULT_OK, resultValue)
		if (isFirstConfigure) Toast.makeText(
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
	updateGoal: (Goal) -> Unit,
	insertGoal: (Goal) -> Unit,
	settingsData: SettingsData,
) {
	Log.d(TAG, "initialGoal.toString() $initialGoal")
	Log.d(TAG, "initialGoal isFirstConfigure $isFirstConfigure")
	WorkoutPixelTheme(
		darkTheme = false,
	) {
		Scaffold(
			topBar = { TopAppBar(title = { Text(text = if (isFirstConfigure) "Add a widget for your goal" else initialGoal.title) }) },
		) {
			EditGoalView(
				initialGoal = initialGoal,
				isFirstConfigure = isFirstConfigure,
				goalsWithoutWidget = goalsWithoutWidget,
				updateGoal = updateGoal,
				insertGoal = insertGoal,
				settingsData = settingsData,
				// modifier = Modifier.padding(innerPadding),
			)
		}
	}
}



