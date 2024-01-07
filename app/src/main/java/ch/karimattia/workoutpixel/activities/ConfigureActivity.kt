package ch.karimattia.workoutpixel.activities

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.lifecycleScope
import ch.karimattia.workoutpixel.screens.EditGoalView
import ch.karimattia.workoutpixel.screens.Lambdas
import ch.karimattia.workoutpixel.core.WidgetActions
import ch.karimattia.workoutpixel.data.*
import ch.karimattia.workoutpixel.screens.allGoals.GoalViewModel
import ch.karimattia.workoutpixel.screens.settings.SettingsViewModel
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
			try {
				val glanceAppWidgetManager = GlanceAppWidgetManager(this)
				goal.glanceId = glanceAppWidgetManager.getGlanceIdBy(goal.appWidgetId).toString()
				Log.d(TAG, "goal.glanceId: ${goal.glanceId}")
			} catch (e: IllegalArgumentException) {
				Log.d(TAG, "No GlanceId found for this appWidgetId.")
			}
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
			val collectedGoal: Goal = goalRepository.loadGoalByAppWidgetIdFlow(goal.appWidgetId).collectAsState(initial = goal).value ?: goal
			val isFirstConfigure = collectedGoal == goal
			Log.d(TAG, "isFirstConfigure: $isFirstConfigure")

			val configureActivityLambdas = Lambdas(
				// Case reconfigure or connect existing goal
				updateGoal = { updatedGoal ->
					lifecycleScope.launch {
						// Insert the goal into the DB and also update the widget.
						goalViewModel.updateGoal(updatedGoal)
						widgetActions(goal = updatedGoal).runUpdate()
						if (isFirstConfigure) setWidgetResult(goal = updatedGoal)
						finishAndRemoveTask()
					}
				},
				// Case new goal
				insertGoal = { updatedGoal ->
					lifecycleScope.launch {
						// Insert the goal into the DB and also update the widget.
						updatedGoal.uid = goalViewModel.insertGoal(updatedGoal)
						widgetActions(goal = updatedGoal).runUpdate()
						if (isFirstConfigure) setWidgetResult(goal = updatedGoal)
						finishAndRemoveTask()
					}
				},
				settingsData = settingsViewModel.settingsData.observeAsState(SettingsData()).value,
			)

			ConfigureActivityCompose(
				initialGoal = if (isFirstConfigure) goal else collectedGoal,
				isFirstConfigure = isFirstConfigure,
				goalsWithoutWidget = goalRepository.loadGoalsWithoutValidAppWidgetId().collectAsState(initial = emptyList()).value,
				lambdas = configureActivityLambdas,
			)
		}
	}

	private fun setWidgetResult(goal: Goal, context: Context = this) {
		// Make sure we pass back the original appWidgetId.
			val resultValue = Intent()
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, goal.appWidgetId)
			setResult(RESULT_OK, resultValue)

			Toast
				.makeText(
					context,
					"Widget created. Click on it to register a workout.",
					Toast.LENGTH_LONG
				).show()
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalComposeUiApi
@Composable
fun ConfigureActivityCompose(
	initialGoal: Goal,
	isFirstConfigure: Boolean,
	goalsWithoutWidget: List<Goal>,
	lambdas: Lambdas,
) {
	Log.d(TAG, "initialGoal.toString() $initialGoal")
	Log.d(TAG, "initialGoal isFirstConfigure $isFirstConfigure")
	WorkoutPixelTheme {
		Scaffold(
			topBar = {
				TopAppBar(
					title = { Text(text = if (isFirstConfigure) "Add a widget for your goal" else initialGoal.title) },
					colors = TopAppBarDefaults.topAppBarColors(
						containerColor = MaterialTheme.colorScheme.primary,
						titleContentColor = MaterialTheme.colorScheme.onPrimary,
						navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
						actionIconContentColor = MaterialTheme.colorScheme.onSecondary
					),

					)
					 },
		) {innerPadding ->

		EditGoalView(
				initialGoal = initialGoal,
				isFirstConfigure = isFirstConfigure,
				goalsWithoutWidget = goalsWithoutWidget,
				lambdas = lambdas,
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}



