package ch.karimattia.workoutpixel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.Goal
import ch.karimattia.workoutpixel.database.GoalViewModel
import ch.karimattia.workoutpixel.ui.theme.WorkoutPixelTheme

class ComposeActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val goalViewModel by viewModels<GoalViewModel>()
		goalViewModel.allGoals.observe(this, {
			//  goalsRecyclerViewAdapter.setData(testData());
			setContent {
				WorkoutPixelApp(
					goals = it,
					// goalViewModel = goalViewModel,
					updateAfterClick = { it.updateAfterClick(this) },
				)
			}
		})
	}
}

@Composable
fun WorkoutPixelApp(
	goals: List<Goal>,
	// goalViewModel: GoalViewModel,
	updateAfterClick: (Goal) -> Unit,
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
		var appBarTitle: String by remember { mutableStateOf("Workout Pixel") }
		// TODO: Always have current goal available. Set to 0 if screen for all goals?
		// TODO: App bar based on screens instead of content.
		// TODO: Current goal part of viewModel?
		var currentGoal: Goal? by remember { mutableStateOf(CommonFunctions.testData()[0]) }

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
							IconButton(onClick = { navController.navigate(WorkoutPixelScreen.EditGoalView.name + "/" + (currentGoal?.uid)) }) {
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
				// goalViewModel = goalViewModel,
				updateAfterClick = updateAfterClick,
				navigateTo = { destination: String, goal: Goal? ->
					navController.navigate(destination)
					currentGoal = goal
				},
				setAppBarTitle = { appBarText: String ->
					appBarTitle = appBarText
				},
				modifier = Modifier.padding(innerPadding)
			)
		}
	}
}

@Composable
fun WorkoutPixelNavHost(
	navController: NavHostController,
	goals: List<Goal>,
	// goalViewModel: GoalViewModel,
	updateAfterClick: (Goal) -> Unit,
	navigateTo: (destination: String, goal: Goal?) -> Unit,
	setAppBarTitle: (appBarText: String) -> Unit,
	modifier: Modifier = Modifier
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
			route = WorkoutPixelScreen.GoalDetailView.name + "/{uid}",
			arguments = listOf(
				navArgument("uid") {
					// Make argument type safe
					type = NavType.IntType
				}
			)
		) { entry -> // Look up "uid" in NavBackStackEntry's arguments
			val goalUid = entry.arguments?.getInt("uid", 0)
			val goal: Goal = goals.first { it.uid == goalUid }

			GoalDetailView(
				goal = goal,
				updateAfterClick = { updateAfterClick(goal) },
				// TODO
				deleteGoal = {},
				setAppBarTitle = setAppBarTitle,
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
			val goal: Goal = goals.first { it.uid == goalUid }

			EditGoalView(
				goal = goal,
				setAppBarTitle,
			)
		}
	}
}