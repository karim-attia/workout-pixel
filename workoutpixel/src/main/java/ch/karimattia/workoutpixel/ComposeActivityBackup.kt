package ch.karimattia.workoutpixel

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import ch.karimattia.workoutpixel.core.Goal
import ch.karimattia.workoutpixel.database.KotlinGoalViewModel
import ch.karimattia.workoutpixel.ui.theme.WorkoutPixelTheme

class ComposeActivityBackup : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val kotlinGoalViewModel by viewModels<KotlinGoalViewModel>()


		kotlinGoalViewModel.allGoals.observe(this, { goals ->
			Log.d(
				"ComposeActivity",
				"observation ${goals[10]}"
			)

			setContent {
				WorkoutPixelAppBackup(
					kotlinGoalViewModel = kotlinGoalViewModel,
					goals = goals,
					updateAfterClick = { it.updateAfterClick(this) },
					// TODO: Proper viewModel stuff
					goalChange = { goal ->
						Log.d(
							"ComposeActivity",
							"setContent goalChange" + goal.intervalBlue.toString()
						)
						kotlinGoalViewModel.updateGoal(goal)
					}
				)
			}
		})
	}

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WorkoutPixelAppBackup(
	kotlinGoalViewModel: KotlinGoalViewModel,
	goals: List<Goal>,
	updateAfterClick: (Goal) -> Unit,
	goalChange: (Goal) -> Unit,
) {
	WorkoutPixelTheme(
		darkTheme = false,
	) {
		val allScreens = WorkoutPixelScreen.values().toList()
		val navController = rememberNavController()
		val backstackEntry = navController.currentBackStackEntryAsState()
		// var currentScreen by rememberSaveable { mutableStateOf(WorkoutPixelScreen.GoalsList) }
		val currentScreen = WorkoutPixelScreen.fromRoute(
			backstackEntry.value?.destination?.route
		)
		// TODO: Current goal part of viewModel?

		var appBarTitle: String by remember { mutableStateOf("Workout Pixel") }
		// TODO: Always have current goal available. Set to 0 if screen for all goals?
		val currentGoalUid:Int? by kotlinGoalViewModel.currentGoalUid.observeAsState()
 
		Log.d(
			"ComposeActivity",
			"currentGoal: Uid: $currentGoalUid"
		)

		Log.d(
			"ComposeActivity",
			"currentGoal: Interval: ${goalFromGoalsByUid(goals = goals, goalUid = currentGoalUid)}"
		)

		Log.d(
			"ComposeActivity",
			"WorkoutPixelTheme ${goals[10]}"
		)

		Scaffold(
			topBar = {
				TopAppBar(
					title = { Text(text = appBarTitle) },
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
							IconButton(onClick = { navController.navigate(WorkoutPixelScreen.EditGoalView.name + "/" + currentGoalUid) }) {
								Icon(Icons.Filled.Edit, contentDescription = "Edit goal")
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
							icon = { Icon(screen.icon, contentDescription = null) },
							label = { Text(screen.name) },
							selected = currentScreen == screen,
							onClick = {
								navController.navigate(screen.name)
							}
						)
					}
				}
			}
		) { innerPadding ->
			Log.d(
				"ComposeActivity",
				"innerPadding ${goals[10]}"
			)

			WorkoutPixelNavHostBackup(
				navController = navController,
				goals = goals,
				kotlinGoalViewModel = kotlinGoalViewModel,
				updateAfterClick = updateAfterClick,
				navigateTo = { destination: String, goal: Goal? ->
					Log.d(
						"ComposeActivity",
						"navigateTo $goal"
					)
					kotlinGoalViewModel.changeCurrentGoal(goal)
					navController.navigate(destination)
				},
				setAppBarTitle = { appBarText: String ->
					appBarTitle = appBarText
				},
				goalChange = { goal: Goal, navigateUp: Boolean ->
					goalChange(goal)
					Log.d(
						"ComposeActivity",
						"WorkoutPixelNavHost goalChange" + goal.intervalBlue.toString()
					)
/*
					if (navigateUp) {
						currentGoalUid = goal.uid
						navController.navigateUp()
					}
*/
				},
				currentGoal = goalFromGoalsByUid(goalUid = currentGoalUid, goals = goals),
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}

@ExperimentalComposeUiApi
@Composable
fun WorkoutPixelNavHostBackup(
	navController: NavHostController,
	goals: List<Goal>,
	kotlinGoalViewModel: KotlinGoalViewModel,
	updateAfterClick: (Goal) -> Unit,
	navigateTo: (destination: String, goal: Goal?) -> Unit,
	setAppBarTitle: (appBarText: String) -> Unit,
	goalChange: (goal: Goal, navigateUp: Boolean) -> Unit,
	currentGoal: Goal?,
	modifier: Modifier = Modifier,
) {
	// val goals: List<Goal> by goalViewModel.allGoals.observeAsState(initial = listOf())
	Log.d(
		"ComposeActivity",
		"WorkoutPixelNavHost ${goals[10]}"
	)

	NavHost(
		navController = navController,
		startDestination = WorkoutPixelScreen.GoalsList.name,
		modifier = modifier
	) {
		composable(route = WorkoutPixelScreen.GoalsList.name) {
			AllGoals(
				goals = goals,
				updateAfterClick = updateAfterClick,
				navigateTo = navigateTo,
				setAppBarTitle = setAppBarTitle,
			)
		}
		composable(route = WorkoutPixelScreen.Instructions.name) {
			Text("Instructions TODO")
			setAppBarTitle("Instructions")
		}
		// Goal Detail
		composable(
			route = WorkoutPixelScreen.GoalDetailView.name + "/{uid}",
			arguments = listOf(
				navArgument("uid") {
					// Make argument type safe
					type = NavType.IntType
				}
			)
		) { entry -> // Look up "uid" in NavBackStackEntry's arguments
			val goalUid: Int? = entry.arguments?.getInt("uid", 0)
			// TODO: Use Utils
			// val goal: Goal = goalFromGoalsByUid(goals = goals, goalUid = goalUid)
			val goal: Goal = goals.first { it.uid == goalUid }

			Log.d(
				"ComposeActivity",
				"Goal Detail Navigation $currentGoal"
			)

			Log.d(
				"ComposeActivity",
				"Goal Detail Navigation ${goals[10]}"
			)

			if (currentGoal != null) {
				GoalDetailView(
					goal = currentGoal,
					updateAfterClick = { updateAfterClick(currentGoal) },
					// TODO
					deleteGoal = { goalToBeDeleted ->

						navController.navigateUp()
					},
					setAppBarTitle = setAppBarTitle,
					updateGoal = {_, _ -> }
				)
			}
			else (
					Text("currentGoal = null")
					)
		}
		// Edit goal
		composable(
			route = WorkoutPixelScreen.EditGoalView.name + "/{uid}",
			arguments = listOf(
				navArgument("uid") {
					// Make argument type safe
					type = NavType.IntType
				}
			)
		) { entry -> // Look up "uid" in NavBackStackEntry's arguments
			val goalUid = entry.arguments?.getInt("uid", 0)
			// TODO: Use Utils
			val goal: Goal = goals.first { it.uid == goalUid }

			Log.d(
				"ComposeActivity",
				"Edit goal Navigation $currentGoal"
			)

			if (currentGoal != null) {
				EditGoalView(
					initialGoal = currentGoal,
					setAppBarTitle,
					addUpdateWidget = {goalChange(it, true)}
				)
			}
			else (
					Text("currentGoal = null")
			)
		}
	}
}