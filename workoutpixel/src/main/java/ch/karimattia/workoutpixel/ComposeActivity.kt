package ch.karimattia.workoutpixel

import android.content.Context
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.Goal
import ch.karimattia.workoutpixel.core.WidgetAlarm
import ch.karimattia.workoutpixel.database.KotlinGoalViewModel
import ch.karimattia.workoutpixel.ui.theme.WorkoutPixelTheme

class ComposeActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val kotlinGoalViewModel by viewModels<KotlinGoalViewModel>()
		var alreadySetUp: Boolean = false

		kotlinGoalViewModel.allGoals.observe(this, { goals ->
			Log.d(
				"ComposeActivity",
				"observe ${goals[10]}"
			)

			setContent {
				WorkoutPixelApp(
					kotlinGoalViewModel = kotlinGoalViewModel,
					goals = goals,
					updateAfterClick = { it.updateAfterClick(this) }, // contains updateGoal
					// TODO: Proper viewModel stuff
					// TODO: runUpdate
					updateGoal = {
						kotlinGoalViewModel.updateGoal(it)
						it.runUpdate(this, false);
								 },
					deleteGoal = { kotlinGoalViewModel.deleteGoal(it) },
				)
			}

			// Run oneTimeSetup once after the goals are loaded.
			if (!alreadySetUp && goals != null) {
				oneTimeSetup(goals, this)
				alreadySetUp = true
			}
		})
	}
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WorkoutPixelApp(
	kotlinGoalViewModel: KotlinGoalViewModel,
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
		val currentScreen = WorkoutPixelScreen.fromRoute(
			backstackEntry.value?.destination?.route
		)
		// TODO: appBarTitle part of viewModel?
		var appBarTitle: String by remember { mutableStateOf("Workout Pixel") }
		val currentGoalUid: Int? by kotlinGoalViewModel.currentGoalUid.observeAsState()

		Log.d("ComposeActivity", "currentGoal: Uid: $currentGoalUid")

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
							IconButton(onClick = { navController.navigate(WorkoutPixelScreen.EditGoalView.name) }) {
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

			WorkoutPixelNavHost(
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
						kotlinGoalViewModel.changeCurrentGoal(null)
						navController.navigateUp()
					}

				},
				currentGoal = goalFromGoalsByUid(goalUid = currentGoalUid, goals = goals),
				modifier = Modifier.padding(innerPadding),
			)
		}
	}
}

@ExperimentalComposeUiApi
@Composable
fun WorkoutPixelNavHost(
	navController: NavHostController,
	goals: List<Goal>,
	kotlinGoalViewModel: KotlinGoalViewModel,
	updateAfterClick: (Goal) -> Unit,
	navigateTo: (destination: String, goal: Goal?) -> Unit,
	setAppBarTitle: (appBarText: String) -> Unit,
	updateGoal: (updatedGoal: Goal, navigateUp: Boolean) -> Unit,
	deleteGoal: (updatedGoal: Goal, navigateUp: Boolean) -> Unit,
	currentGoal: Goal?,
	modifier: Modifier = Modifier,
) {
	// val goals: List<Goal> by goalViewModel.allGoals.observeAsState(initial = listOf())

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
			route = WorkoutPixelScreen.GoalDetailView.name,
		) {
			Log.d("ComposeActivity", "Goal Detail Navigation $currentGoal")

			if (currentGoal != null) {
				GoalDetailView(
					goal = currentGoal,
					updateAfterClick = { updateAfterClick(currentGoal) },
					// TODO
					deleteGoal = { deleteGoal(it, true) },
					setAppBarTitle = setAppBarTitle,
					updateGoal = updateGoal,
				)
			} else (
					Text("currentGoal = null")
					)
		}
		// Edit goal
		composable(
			route = WorkoutPixelScreen.EditGoalView.name,
		) {
			Log.d("ComposeActivity", "Edit goal Navigation $currentGoal")

			if (currentGoal != null) {
				EditGoalView(
					initialGoal = currentGoal,
					setAppBarTitle,
					isFirstConfigure = false,
					addUpdateWidget = { updateGoal(it, true) }
				)
			} else (
					Text("currentGoal = null")
					)
		}
	}
}

fun oneTimeSetup(goals: List<Goal>, context: Context) {
	Log.d("ComposeActivity", "oneTimeSetup")

	// Update all goals
	for (goal in goals) {
		// Sometimes the onClickListener in the widgets stop working. This is a super stupid way to regularly reset the onClickListener when you open the main app.
		if (goal.hasValidAppWidgetId()) {
			goal.updateWidgetBasedOnStatus(context)
		}
	}

	// Remove appWidgetId if it is not valid anymore. Run only once.
	CommonFunctions.cleanGoals(context, goals)

	// Every time the app starts, set the alarm to update everything at 3:00. In case something breaks.
	WidgetAlarm.startAlarm(context)
}