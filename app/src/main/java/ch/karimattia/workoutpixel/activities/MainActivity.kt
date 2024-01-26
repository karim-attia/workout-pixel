package ch.karimattia.workoutpixel.activities

import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ch.karimattia.workoutpixel.core.Constants
import ch.karimattia.workoutpixel.core.OtherActions
import ch.karimattia.workoutpixel.core.Screens
import ch.karimattia.workoutpixel.core.WidgetActions
import ch.karimattia.workoutpixel.core.WidgetAlarm
import ch.karimattia.workoutpixel.core.goalFromGoalsByUid
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData
import ch.karimattia.workoutpixel.screens.Lambdas
import ch.karimattia.workoutpixel.screens.allGoals.GoalList
import ch.karimattia.workoutpixel.screens.allGoals.GoalViewModel
import ch.karimattia.workoutpixel.screens.editGoal.EditGoalView
import ch.karimattia.workoutpixel.screens.goalDetail.GoalDetailView
import ch.karimattia.workoutpixel.screens.goalDetail.PastClickViewModelAssistedFactory
import ch.karimattia.workoutpixel.screens.onboarding.ChatVariant
import ch.karimattia.workoutpixel.screens.onboarding.Instructions
import ch.karimattia.workoutpixel.screens.onboarding.Onboarding
import ch.karimattia.workoutpixel.screens.progress.Progress
import ch.karimattia.workoutpixel.screens.settings.Settings
import ch.karimattia.workoutpixel.screens.settings.SettingsViewModel
import ch.karimattia.workoutpixel.ui.theme.WorkoutPixelTheme
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG: String = "MainActivity"

@ExperimentalAnimationApi
@ExperimentalAnimationGraphicsApi
@ExperimentalComposeUiApi
@AndroidEntryPoint
@ExperimentalCoilApi
@ExperimentalPagerApi
class MainActivity : ComponentActivity() {

	// Create a class that has those two and extend it (also in AppWidgetProvider, ConfigureActivity)?
	@Inject
	lateinit var widgetActionsFactory: WidgetActions.Factory
	private fun widgetActions(goal: Goal): WidgetActions = widgetActionsFactory.create(goal)

	@Inject
	lateinit var otherActions: OtherActions

	// Or inject via constructor? Only for below. Doesn't work for AppWidgetProvider.
	@Inject
	lateinit var widgetAlarm: WidgetAlarm

	@Inject
	lateinit var pastClickViewModelAssistedFactory: PastClickViewModelAssistedFactory


	// private lateinit var firebaseAnalytics: FirebaseAnalytics

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val goalViewModel: GoalViewModel by viewModels()
		val settingsViewModel: SettingsViewModel by viewModels()

		// firebaseAnalytics = FirebaseAnalytics.getInstance(this)
		// https://firebase.google.com/docs/analytics/get-started?platform=android#kotlin+ktx
		// firebaseAnalytics = Firebase.analytics
		// val bundle = Bundle()
		// bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "my_item_id")

		// firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)


		setContent {
			val settingsData = settingsViewModel.settingsData.observeAsState().value
			val currentGoalUid =
				goalViewModel.currentGoalUid.observeAsState(initial = Constants.INVALID_GOAL_UID).value
			val goals = goalViewModel.allGoals
			// Depends on settingsData, but not on currentGoalUid and goals.
			val mainActivityLambdas = Lambdas(
				updateAfterClick = {
					// contains updateGoal
					lifecycleScope.launch(Dispatchers.IO) {
						widgetActions(it).updateAfterClick()
					}
				},
				updateGoal = { goal ->
					lifecycleScope.launch(Dispatchers.IO) {
						// Log.d(TAG, "updateGoal top main")
						goalViewModel.updateGoal(goal)
						widgetActions(goal).runUpdate()
					}
				},
				deleteGoal = {
					lifecycleScope.launch(Dispatchers.IO) {
						goalViewModel.deleteGoal(
							it
						)
					}
				},
				addWidgetToHomeScreen = { goal: Goal, insertNewGoal: Boolean ->
					// Log.d(TAG, "addWidgetToHomeScreen")
					if (insertNewGoal) goal.uid = goalViewModel.insertGoal(goal)
					widgetActions(goal).pinAppWidget()
					return@Lambdas goal.uid
				},
				// Don't use this in highest level. Otherwise colors flicker.
				settingsData = settingsData ?: SettingsData(),
				settingChange = { updatedSettingsData: SettingsData ->
					lifecycleScope.launch(Dispatchers.IO) {
						// Log.d(TAG, "settingChange")
						settingsViewModel.updateSettings(updatedSettingsData)
						// TODO: Could move to ViewModel
						delay(200)
						otherActions.updateAllWidgets()
					}
				},
				widgetPinningPossible = AppWidgetManager.getInstance(this).isRequestPinAppWidgetSupported,
			)


			WorkoutPixelApp(
				goalViewModel = goalViewModel,
				pastClickViewModelAssistedFactory = pastClickViewModelAssistedFactory,
				currentGoalUid = currentGoalUid,
				goals = goals, // emptyList(), to test
				settingsData = settingsData,
				lambdas = mainActivityLambdas,
			)
		}

		// Run oneTimeSetup once after the goals are loaded.
		lifecycleScope.launch(Dispatchers.IO) {
			delay(500)
			otherActions.oneTimeSetup()
			// Every time the app starts, set the alarm to update everything at 3:00. In case something breaks.
			widgetAlarm.startAlarm()
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalCoilApi
@ExperimentalAnimationGraphicsApi
@ExperimentalPagerApi
@Composable
fun WorkoutPixelApp(
	goalViewModel: GoalViewModel,
	pastClickViewModelAssistedFactory: PastClickViewModelAssistedFactory,
	currentGoalUid: Int = goalViewModel.currentGoalUid.observeAsState(initial = Constants.INVALID_GOAL_UID).value,
	settingsData: SettingsData?,
	goals: List<Goal>,
	lambdas: Lambdas,
) {
	WorkoutPixelTheme {
		val allScreens = Screens.entries
		val navController: NavHostController = rememberNavController()
		val backstackEntry: State<NavBackStackEntry?> = navController.currentBackStackEntryAsState()
		val currentScreen: Screens = Screens.fromRoute(backstackEntry.value?.destination?.route)
		val currentGoal: Goal? = goalFromGoalsByUid(goalUid = currentGoalUid, goals = goals)

		Scaffold(
			topBar = {
				// If goals is empty, there is a redirect to the Onboarding screen. Adding && goals.isNotEmpty() removed jank from this switch on first start.
				if (!currentScreen.fullScreen && goals.isNotEmpty()) {
					TopAppBar(
						title = {
							Text(
								text = currentScreen.topAppBarName ?: (currentGoal?.title ?: "")
							)
						},
						navigationIcon = {
							if (currentScreen.showBackNavigation) {

								IconButton(onClick = {
									// TODO: Set current goal to null if back to main screen
									navController.navigateUp()
								}) {
									Icon(Icons.Filled.ArrowBack, "Back")
								}

							}
						},
						actions = {
							if (currentScreen.showEditIcon) {
								IconButton(onClick = { navController.navigate(Screens.EditGoalView.name) }) {
									Icon(Icons.Filled.Edit, contentDescription = "Edit goal")
								}
							}
							if (currentScreen.showSettingsIcon) {
								IconButton(onClick = { navController.navigate(Screens.Settings.name) }) {
									Icon(Icons.Filled.Settings, contentDescription = "Settings")
								}
							}
						},
						colors = TopAppBarDefaults.topAppBarColors(
							containerColor = MaterialTheme.colorScheme.primary,
							titleContentColor = MaterialTheme.colorScheme.onPrimary,
							navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
							actionIconContentColor = MaterialTheme.colorScheme.onSecondary
						),
					)
				}
			},
			bottomBar = {
				if (!currentScreen.fullScreen && goals.isNotEmpty()) {
					NavigationBar {
						// Only show entries that have bottomNavigation == true.
						allScreens.filter { it.bottomNavigation && (it.showWhenPinningPossible || !lambdas.widgetPinningPossible) }
							.forEach { screen ->
								NavigationBarItem(
									icon = {
										screen.icon?.let {
											Icon(
												it,
												contentDescription = null,
												modifier = Modifier.size(24.dp)
											)
										}
									},
									label = { Text(text = screen.displayName ?: "") },
									selected = currentScreen == screen,
									onClick = {
										Log.d(TAG, "onClick $screen")
										navController.navigate(screen.name) {
											if (screen == Screens.GoalsList) {
												// TODO: set current goal to null?
												Log.d(TAG, "popUpTo GoalsList")
												// Pop up to the start destination of the graph to avoid building up a large stack of destinations on the back stack as users select items
												popUpTo(Screens.GoalsList.name) {
													// saveState = true
													inclusive = true
												}
											} else {
												Log.d(TAG, "elseBlock $screen")
												popUpTo(Screens.GoalsList.name)
											}
											// Avoid multiple copies of the same destination when reselecting the same item
											launchSingleTop = true
											// Restore state when reselecting a previously selected item
											// restoreState = true

										}
									})
							}
					}
				}
			}, floatingActionButton = {
				if (lambdas.widgetPinningPossible && currentScreen.showFloatingActionButton) {
					FloatingActionButton(onClick = {
						navController.navigate(Screens.Onboarding.name)
					}) {
						Icon(
							imageVector = Icons.Filled.Add,
							contentDescription = null,
							modifier = Modifier.size(36.dp)
						)
					}
				}
			}

		) { innerPadding ->
			Log.d(TAG, "settingsData $settingsData")
			if (settingsData != null) {
				WorkoutPixelNavHost(
					navController = navController,
					goals = goals,
					currentGoal = currentGoal,
					pastClickViewModelAssistedFactory = pastClickViewModelAssistedFactory,
					lambdas = lambdas.copy(
						navigateTo = { destination: String, goal: Goal?, popBackStack: Boolean ->
							Log.d(TAG, "navigateTo $goal")
							goalViewModel.changeCurrentGoalUid(goal)
							navController.navigate(destination) {
								if (popBackStack) popUpTo(destination) { inclusive = true }
							}
						},
						navigateUp = { setGoalToNull: Boolean ->
							Log.d(TAG, "navigateUp")
							if (setGoalToNull) goalViewModel.changeCurrentGoalUid(Constants.INVALID_GOAL_UID)
							navController.navigateUp()
						},
						// Getting UID to Onboarding through currentGoal through changeCurrentGoalUid(goalUid)
						// With this UID, get the AppWidgetId
						addWidgetToHomeScreen = { goal: Goal, insertNewGoal: Boolean ->
							val goalUid: Int = lambdas.addWidgetToHomeScreen(goal, insertNewGoal)
							goalViewModel.changeCurrentGoalUid(goalUid)
							goalUid
						}), modifier = Modifier.padding(innerPadding)

				)
			}
		}
	}
}

@ExperimentalAnimationApi
@ExperimentalCoilApi
@ExperimentalAnimationGraphicsApi
@ExperimentalPagerApi
@ExperimentalComposeUiApi
@Composable
fun WorkoutPixelNavHost(
	navController: NavHostController,
	goals: List<Goal>,
	currentGoal: Goal?,
	pastClickViewModelAssistedFactory: PastClickViewModelAssistedFactory,
	modifier: Modifier = Modifier,
	lambdas: Lambdas,
) {
	// remember, because otherwise adding the first goal causes a recomposition and exits the Onboarding screen.
	val startDestination = remember {
		when {
			goals.isNotEmpty() -> {
				// .d(TAG, "goals.isNotEmpty()")
				Screens.GoalsList.name
			}

			lambdas.widgetPinningPossible -> {
				Screens.Onboarding.name
			}

			else -> {
				Screens.Instructions.name
			}
		}
	}
	Log.d(TAG, "startDestination $startDestination")

	NavHost(
		navController = navController,
		startDestination = startDestination,
		modifier = modifier,
	) {
		composable(route = Screens.GoalsList.name) {
			Log.d(TAG, "------------GoalsList------------")
			if (goals.isEmpty()) {
				lambdas.navigateTo(Screens.Onboarding.name, null, true)
			}
			GoalList(
				goals = goals,
				lambdas = lambdas,
			)
		}
		composable(route = Screens.Onboarding.name) {
			// Log.d(TAG, "------------Onboarding------------")
			if (lambdas.widgetPinningPossible) {
				Onboarding(
					currentGoal = currentGoal ?: Goal(),
					chatVariant = if (goals.isEmpty()) ChatVariant.Onboarding else ChatVariant.NewGoalOnly,
					lambdas = lambdas,
				)
			} else {
				lambdas.navigateTo(Screens.Instructions.name, null, true)
			}
		}
		composable(route = Screens.Instructions.name) {
			// Log.d(TAG, "------------Instructions------------")
			Instructions()
		}
		// GoalDetailView
		composable(route = Screens.GoalDetailView.name) {
			if (currentGoal != null) {
				GoalDetailView(
					currentGoal = currentGoal,
					pastClickViewModelAssistedFactory = pastClickViewModelAssistedFactory,
					lambdas = lambdas,
				)
			} else (
					lambdas.navigateTo(Screens.GoalsList.name, null, true)
					)
		}
		// Your Progress
		composable(route = Screens.Progress.name) {
			Log.d(TAG, "------------Progress------------")
			Progress(
				goals = goals,
				lambdas = lambdas,
			)
		}
		// Edit goal
		composable(
			route = Screens.EditGoalView.name,
		) {
			// Log.d(TAG, "------------EditGoal $currentGoal------------")
			if (currentGoal != null) {
				EditGoalView(
					initialGoal = currentGoal,
					isFirstConfigure = false,
					lambdas = lambdas,
				)
			} else (
					lambdas.navigateTo(Screens.GoalsList.name, null, true)
					)
		}
		// Settings
		composable(
			route = Screens.Settings.name,
		) {
			// Log.d(TAG, "------------Settings------------")
			Settings(
				lambdas = lambdas,
			)
		}
	}
}

