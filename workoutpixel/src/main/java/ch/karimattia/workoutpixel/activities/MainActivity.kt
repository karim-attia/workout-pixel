package ch.karimattia.workoutpixel.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ch.karimattia.workoutpixel.AllGoals
import ch.karimattia.workoutpixel.EditGoalView
import ch.karimattia.workoutpixel.composables.GoalDetailView
import ch.karimattia.workoutpixel.composables.Instructions
import ch.karimattia.workoutpixel.composables.Settings
import ch.karimattia.workoutpixel.core.*
import ch.karimattia.workoutpixel.data.GoalViewModel
import ch.karimattia.workoutpixel.data.PastClickViewModel
import ch.karimattia.workoutpixel.goalFromGoalsByUid
import ch.karimattia.workoutpixel.ui.theme.WorkoutPixelTheme
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG: String = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

	// Create a class that has those two and extend it (also in AppWidgetProvider, ConfigureActivity)?
	// Or inject via constructor? At least the second one.
	@Inject
	lateinit var goalSaveActionsFactory: GoalSaveActions.Factory
	private fun goalSaveActions(goal: Goal): GoalSaveActions {
		return goalSaveActionsFactory.create(goal)
	}
	@Inject	lateinit var widgetAlarm: WidgetAlarm

	@ExperimentalComposeUiApi
	@ExperimentalCoilApi
	@ExperimentalAnimationGraphicsApi
	@ExperimentalPagerApi
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val goalViewModel: GoalViewModel by viewModels()
		val pastClickViewModel: PastClickViewModel by viewModels()
		var alreadySetUp = false
		val context: Context = this

		goalViewModel.allGoals.observe(this, { goals ->
			Log.d(TAG, "___________observe___________")

			setContent {
				WorkoutPixelApp(
					goalViewModel = goalViewModel,
					pastClickViewModel = pastClickViewModel,
					goals = goals,
					updateAfterClick = {
						// contains updateGoal
						goalSaveActions(it).updateAfterClick()
					},
					updateGoal = {
						goalViewModel.updateGoal(it)
						goalSaveActions(it).updateWidgetBasedOnStatus()
					},
					deleteGoal = { goalViewModel.deleteGoal(it) },
				)
			}

			// Run oneTimeSetup once after the goals are loaded.
			if (!alreadySetUp && goals != null) {
				lifecycleScope.launch {
					oneTimeSetup(goals, context)
				}
				alreadySetUp = true
			}
		})
	}

	private fun oneTimeSetup(goals: List<Goal>, context: Context) {
		Log.d(TAG, "oneTimeSetup")

		// Update all goals
		for (goal in goals) {
			// Sometimes the onClickListener in the widgets stop working. This is a super stupid way to regularly reset the onClickListener when you open the main app.
			if (goal.hasValidAppWidgetId()) {
				goalSaveActions(goal).updateWidgetBasedOnStatus()
			}
		}

		// Remove appWidgetId if it is not valid anymore. Run only once.
		CommonFunctions.cleanGoals(context, goals)

		// Every time the app starts, set the alarm to update everything at 3:00. In case something breaks.
		widgetAlarm.startAlarm()
	}
}

@ExperimentalCoilApi
@ExperimentalAnimationGraphicsApi
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@Composable
fun WorkoutPixelApp(
	goalViewModel: GoalViewModel,
	pastClickViewModel: PastClickViewModel,
	goals: List<Goal>,
	updateAfterClick: (Goal) -> Unit,
	updateGoal: (Goal) -> Unit,
	deleteGoal: (Goal) -> Unit,
) {
	WorkoutPixelTheme(
		darkTheme = false,
	) {
		val allScreens = WorkoutPixelScreen.values().toList()
		val navController = rememberNavController()
		val backstackEntry = navController.currentBackStackEntryAsState()
		val currentScreen = WorkoutPixelScreen.fromRoute(backstackEntry.value?.destination?.route)
		//val appBarTitle: String? by kotlinGoalViewModel.appBarTitle.observeAsState()
		val currentGoalUid: Int? by goalViewModel.currentGoalUid.observeAsState()
		//val currentGoal: Goal? by kotlinGoalViewModel.currentGoal.observeAsState()
		val currentGoal: Goal? = goalFromGoalsByUid(goalUid = currentGoalUid, goals = goals)

		Scaffold(
			topBar = {
				TopAppBar(
					title = {
						Text(
							text =
							currentScreen.displayName ?: (currentGoal?.title ?: "")
						)
					},
					navigationIcon =
					if (currentScreen.showBackNavigation) {
						{
							IconButton(onClick = { navController.navigateUp() }) {
								Icon(Icons.Filled.ArrowBack, "Back")
							}
						}
					} else null,
					actions = {
						if (currentScreen.showEditIcon) {
							IconButton(onClick = { navController.navigate(WorkoutPixelScreen.EditGoalView.name) }) {
								Icon(Icons.Filled.Edit, contentDescription = "Edit goal")
							}
						}
						if (currentScreen.showSettingsIcon) {
							IconButton(onClick = { navController.navigate(WorkoutPixelScreen.Settings.name) }) {
								Icon(Icons.Filled.Settings, contentDescription = "Settings")
							}
						}
					}
				)
			},
			bottomBar = {
				BottomNavigation {
					// Only show entries that have bottomNavigation == true.
					allScreens.filter { it.bottomNavigation }.forEach { screen ->
						BottomNavigationItem(
							icon = { screen.icon?.let { Icon(it, contentDescription = null) } },
							label = { Text(text = screen.displayName ?: "") },
							selected = currentScreen == screen,
							onClick = {
								navController.navigate(screen.name) {
									if (screen == WorkoutPixelScreen.GoalsList) {
										popUpTo(WorkoutPixelScreen.GoalsList.name) {
											inclusive = true
										}
									} else {
										popUpTo(WorkoutPixelScreen.GoalsList.name)
									}
								}
							}
						)
					}
				}
			}
		) { innerPadding ->

			WorkoutPixelNavHost(
				navController = navController,
				goals = goals,
				updateAfterClick = updateAfterClick,
				navigateTo = { destination: String, goal: Goal? ->
					Log.d(TAG, "navigateTo $goal")
					goalViewModel.changeCurrentGoalUid(goal)
					navController.navigate(destination)
				},
				updateGoal = { updatedGoal: Goal, navigateUp: Boolean ->
					updateGoal(updatedGoal)
					if (navigateUp) {
						// Why did I have this?
						// kotlinGoalViewModel.changeCurrentGoal(updatedGoal)
						navController.navigateUp()
					}

				},
				deleteGoal = { deletedGoal: Goal, navigateUp: Boolean ->
					deleteGoal(deletedGoal)
					if (navigateUp) {
						// The goal uid of the deleted goal doesn't exist anymore. Removing it from the current goal removes sources of errors.
						goalViewModel.changeCurrentGoalUid(null)
						//kotlinGoalViewModel.changeCurrentGoal(null, goals)
						navController.navigateUp()
					}

				},
				currentGoal = currentGoal,
				//currentGoal = goalFromGoalsByUid(goalUid = currentGoalUid, goals = goals),
				pastClickViewModel = pastClickViewModel,
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}

@ExperimentalCoilApi
@ExperimentalAnimationGraphicsApi
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@Composable
fun WorkoutPixelNavHost(
	navController: NavHostController,
	goals: List<Goal>,
	updateAfterClick: (Goal) -> Unit,
	navigateTo: (destination: String, goal: Goal?) -> Unit,
	updateGoal: (updatedGoal: Goal, navigateUp: Boolean) -> Unit,
	deleteGoal: (updatedGoal: Goal, navigateUp: Boolean) -> Unit,
	currentGoal: Goal?,
	pastClickViewModel: PastClickViewModel,
	modifier: Modifier = Modifier,
) {
	NavHost(
		navController = navController,
		startDestination = if (goals.isNotEmpty()) {
			WorkoutPixelScreen.GoalsList.name
		} else {
			WorkoutPixelScreen.Instructions.name
		},
		modifier = modifier
	) {
		composable(route = WorkoutPixelScreen.GoalsList.name) {
			Log.d(TAG, "------------GoalsList------------")
			AllGoals(
				goals = goals,
				updateAfterClick = updateAfterClick,
				navigateTo = navigateTo,
			)
		}
		composable(route = WorkoutPixelScreen.Instructions.name) {
			Log.d(TAG, "------------Instructions------------")
			Instructions()
		}
		// GoalDetailView
		composable(route = WorkoutPixelScreen.GoalDetailView.name) {
			Log.d(TAG, "------------GoalDetail $currentGoal------------")
			if (currentGoal != null) {
				GoalDetailView(
					goal = currentGoal,
					updateAfterClick = { updateAfterClick(currentGoal) },
					deleteGoal = { deleteGoal(it, true) },
					updateGoal = updateGoal,
					pastClickViewModel = remember { pastClickViewModel },
				)
			} else (Text("currentGoal = null"))
		}
		// Edit goal
		composable(
			route = WorkoutPixelScreen.EditGoalView.name,
		) {
			Log.d(TAG, "------------EditGoal $currentGoal------------")
			if (currentGoal != null) {
				EditGoalView(
					initialGoal = currentGoal,
					isFirstConfigure = false,
					addUpdateWidget = { updateGoal(it, true) }
				)
			} else (
					Text("currentGoal = null")
					)
		}
		// Settings
		composable(
			route = WorkoutPixelScreen.Settings.name,
		) {
			Log.d(TAG, "------------Settings------------")
			// TODO: Get from datamodel
			Settings() //settingsData = SettingsData(Green = Color(Green), Blue = Color(Blue), Red = Color(Red), Purple = Color(Purple), ))
		}

	}
}
