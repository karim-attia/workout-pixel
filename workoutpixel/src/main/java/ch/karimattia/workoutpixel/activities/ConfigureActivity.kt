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
import androidx.compose.ui.ExperimentalComposeUiApi
import ch.karimattia.workoutpixel.EditGoalView
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.Goal
import ch.karimattia.workoutpixel.core.GoalSaveActions
import ch.karimattia.workoutpixel.core.GoalWidgetActions
import ch.karimattia.workoutpixel.data.KotlinGoalViewModel
import ch.karimattia.workoutpixel.ui.theme.WorkoutPixelTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG: String = "ConfigureCompose"

@AndroidEntryPoint
class ConfigureActivity : ComponentActivity() {

	@Inject
	lateinit var goalSaveActionsFactory: GoalSaveActions.Factory
	private fun goalSaveActions(goal: Goal): GoalSaveActions {
		return goalSaveActionsFactory.create(goal)
	}

	@ExperimentalComposeUiApi
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val kotlinGoalViewModel by viewModels<KotlinGoalViewModel>()
		val context: Context = this

		val goal = Goal(
			AppWidgetManager.INVALID_APPWIDGET_ID,
			"",
			0,
			2,
			2,
			false,
			false,
			CommonFunctions.STATUS_NONE
		)

		// Find the widget id  and whether it is a reconfigure activity from the intent.
		val intent = intent
		val extras = intent.extras

		// Get the AppWidgetId from the launcher if there is one provided. Else exit.
		// Set the result to CANCELED. This will cause the widget host to cancel out of the widget placement if the user presses the back button.
		setResult(RESULT_CANCELED)
		if (extras != null) {
			goal.appWidgetId =
				extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID
				)
		} else {
			Log.d(TAG, "extras = null")
		}

		// If this activity was started with an intent without an app widget ID, finish with an error.
		if (goal.appWidgetId == null || goal.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			Log.d(TAG, "AppWidgetId is invalid.")
			finishAndRemoveTask()
			return
		}

		kotlinGoalViewModel.allGoals.observe(this, { goals ->
			Log.d(TAG, "___________observe___________")

			val goalsWithoutWidget =
				CommonFunctions.goalsWithInvalidOrNullAppWidgetId(context, goals)

			setContent {
				ConfigureActivityCompose(
					initialGoal = goal,
					isFirstConfigure = true,
					goalsWithoutWidget = goalsWithoutWidget,
					addUpdateWidget = {
						// Insert the goal into the DB and also update the widget. The update is done there, because the OnClickListener needs the goal uid. This is returned by the DB.
						kotlinGoalViewModel.insertGoal(it)
						// goal has the wrong uid here, but this doesn't matter for setWidgetAndFinish
						setWidgetAndFinish(goal = it, context = this)
					},
					connectGoal = {
						// Not necessarely needed since we always update all goals regardless of whether they are connected. But it doesn't hurt to set the status here.
						goal.setNewStatus()
						it.setNewStatus()
						kotlinGoalViewModel.updateGoal(it)
						GoalWidgetActions(context, it).runUpdate(true)
						setWidgetAndFinish(goal = it, context = this)
					},
				)
			}
		})
	}

	private fun setWidgetAndFinish(goal: Goal, context: Context) {
		// Make sure we pass back the original appWidgetId.
		// Reconfiguration does not need this.
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
				// modifier = Modifier.padding(innerPadding),
			)
		}
	}
}



