package ch.karimattia.workoutpixel.activities

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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ch.karimattia.workoutpixel.composables.*
import ch.karimattia.workoutpixel.core.*
import ch.karimattia.workoutpixel.data.*
import ch.karimattia.workoutpixel.ui.theme.WorkoutPixelTheme
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG: String = "MainActivity"

@AndroidEntryPoint
@ExperimentalComposeUiApi
@ExperimentalCoilApi
@ExperimentalAnimationGraphicsApi
@ExperimentalPagerApi
class MainActivity : ComponentActivity() {

	// Create a class that has those two and extend it (also in AppWidgetProvider, ConfigureActivity)?
	@Inject
	lateinit var widgetActionsFactory: WidgetActions.Factory
	private fun goalActions(goal: Goal): WidgetActions = widgetActionsFactory.create(goal)

	// Or inject via constructor? Only for below. Doesn't work for AppWidgetProvider.
	@Inject
	lateinit var widgetAlarm: WidgetAlarm

	@Inject
	lateinit var pastClickViewModelAssistedFactory: PastClickViewModelAssistedFactory

	@Inject
	lateinit var otherActions: OtherActions

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val goalViewModel: GoalViewModel by viewModels()
		val settingsViewModel: SettingsViewModel by viewModels()

		setContent {
			WorkoutPixelApp(
				goalViewModel = goalViewModel,
				pastClickViewModelAssistedFactory = pastClickViewModelAssistedFactory,
				goals = goalViewModel.allGoals,
				updateAfterClick = {
					// contains updateGoal
					lifecycleScope.launch {
						goalActions(it).updateAfterClick()
					}
				},
				updateGoal = {
					lifecycleScope.launch {
						goalViewModel.updateGoal(it)
						goalActions(it).runUpdate(true)
					}
				},
				deleteGoal = { lifecycleScope.launch { goalViewModel.deleteGoal(it) } },
				addWidgetToHomeScreen = { lifecycleScope.launch { goalActions(it).pinAppWidget() } },
				settingsData = settingsViewModel.settingsData.observeAsState().value,
				settingChange = {
					lifecycleScope.launch {
						Log.d(TAG, "settingChange")
						settingsViewModel.updateSettings(it)
						// TODO: Could move to ViewModel
						delay(200)
						otherActions.updateAllWidgets()
					}
				}
			)
		}

		// Run oneTimeSetup once after the goals are loaded.
		lifecycleScope.launch {
			delay(500)
			otherActions.oneTimeSetup()
			// Every time the app starts, set the alarm to update everything at 3:00. In case something breaks.
			widgetAlarm.startAlarm()
		}
	}
}

@ExperimentalCoilApi
@ExperimentalAnimationGraphicsApi
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@Composable
fun WorkoutPixelApp(
	goalViewModel: GoalViewModel,
	pastClickViewModelAssistedFactory: PastClickViewModelAssistedFactory,
	currentGoalUid: Int = goalViewModel.currentGoalUid.observeAsState(initial = Constants.INVALID_GOAL_UID).value,
	settingsData: SettingsData?,
	settingChange: (SettingsData) -> Unit,
	goals: List<Goal>,
	updateAfterClick: (Goal) -> Unit,
	updateGoal: (Goal) -> Unit,
	deleteGoal: (Goal) -> Unit,
	addWidgetToHomeScreen: (Goal) -> Unit,
) {
	WorkoutPixelTheme(
		darkTheme = false,
	) {
		val allScreens = WorkoutPixelScreen.values().toList()
		val navController = rememberNavController()
		val backstackEntry = navController.currentBackStackEntryAsState()
		val currentScreen = WorkoutPixelScreen.fromRoute(backstackEntry.value?.destination?.route)
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

			if (settingsData != null) {
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
							navController.navigateUp()
						}

					},
					deleteGoal = { deletedGoal: Goal, navigateUp: Boolean ->
						deleteGoal(deletedGoal)
						if (navigateUp) {
							// The goal uid of the deleted goal doesn't exist anymore. Removing it from the current goal removes sources of errors.
							goalViewModel.changeCurrentGoalUid(null)
							navController.navigateUp()
						}
					},
					addWidgetToHomeScreen = addWidgetToHomeScreen,
					currentGoal = currentGoal,
					pastClickViewModelAssistedFactory = pastClickViewModelAssistedFactory,
					settingsData = settingsData,
					settingChange = settingChange,
					modifier = Modifier.padding(innerPadding),
				)
			}
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
	addWidgetToHomeScreen: (Goal) -> Unit,
	currentGoal: Goal?,
	pastClickViewModelAssistedFactory: PastClickViewModelAssistedFactory,
	modifier: Modifier = Modifier,
	settingsData: SettingsData,
	settingChange: (SettingsData) -> Unit,
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
				settingsData = settingsData,
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
					addWidgetToHomeScreen = addWidgetToHomeScreen,
					settingsData = settingsData,
					pastClickViewModelAssistedFactory = pastClickViewModelAssistedFactory,
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
					updateGoal = { updateGoal(it, true) },
					settingsData = settingsData,
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
			Settings(
				settingsData = settingsData,
				settingChange = settingChange,
			)
		}

	}
}

