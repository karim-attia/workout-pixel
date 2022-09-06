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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import ch.karimattia.workoutpixel.core.*
import ch.karimattia.workoutpixel.data.*
import ch.karimattia.workoutpixel.screens.*
import ch.karimattia.workoutpixel.screens.allGoals.GoalList
import ch.karimattia.workoutpixel.screens.allGoals.GoalViewModel
import ch.karimattia.workoutpixel.screens.goalDetail.GoalDetailView
import ch.karimattia.workoutpixel.screens.goalDetail.PastClickViewModelAssistedFactory
import ch.karimattia.workoutpixel.screens.onboarding.Chatvariant
import ch.karimattia.workoutpixel.screens.onboarding.Onboarding
import ch.karimattia.workoutpixel.screens.progress.Progress
import ch.karimattia.workoutpixel.screens.settings.SettingsViewModel
import ch.karimattia.workoutpixel.ui.theme.WorkoutPixelTheme
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
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


	private lateinit var firebaseAnalytics: FirebaseAnalytics

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val goalViewModel: GoalViewModel by viewModels()
		val settingsViewModel: SettingsViewModel by viewModels()

		firebaseAnalytics = FirebaseAnalytics.getInstance(this)
		// https://firebase.google.com/docs/analytics/get-started?platform=android#kotlin+ktx
		// firebaseAnalytics = Firebase.analytics
		val bundle = Bundle()
		bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "my_item_id")

		firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)


		setContent {
			val settingsData = settingsViewModel.settingsData.observeAsState().value
			val appWidgetManager = AppWidgetManager.getInstance(this)
			val mainActivityLambdas = Lambdas(
				updateAfterClick = {
					// contains updateGoal
					lifecycleScope.launch {
						widgetActions(it).updateAfterClick()
					}
				},
				updateGoal = { goal ->
					lifecycleScope.launch {
						Log.d(TAG, "updateGoal top main")
						goalViewModel.updateGoal(goal)
						widgetActions(goal).runUpdate(true)
					}
				},
				deleteGoal = { lifecycleScope.launch { goalViewModel.deleteGoal(it) } },
				addWidgetToHomeScreen = { goal: Goal, insertNewGoal: Boolean ->
					Log.d(TAG, "addWidgetToHomeScreen")
					if (insertNewGoal) goal.uid = goalViewModel.insertGoal(goal)
					widgetActions(goal).pinAppWidget()
					return@Lambdas goal.uid
				},
				// Don't use this in highest level. Otherwise colors flicker.
				settingsData = settingsData ?: SettingsData(),
				settingChange = { updatedSettingsData: SettingsData ->
					lifecycleScope.launch {
						Log.d(TAG, "settingChange")
						settingsViewModel.updateSettings(updatedSettingsData)
						// TODO: Could move to ViewModel
						delay(200)
						otherActions.updateAllWidgets()
					}
				},
				widgetPinningPossible = appWidgetManager.isRequestPinAppWidgetSupported,
			)

			WorkoutPixelApp(
				goalViewModel = goalViewModel,
				pastClickViewModelAssistedFactory = pastClickViewModelAssistedFactory,
				goals = goalViewModel.allGoals, // emptyList(), to test
				settingsData = settingsViewModel.settingsData.observeAsState().value,
				lambdas = mainActivityLambdas,
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
	WorkoutPixelTheme(
		darkTheme = false,
	) {
		val allScreens = Screens.values().toList()
		val navController: NavHostController = rememberNavController()
		val backstackEntry: State<NavBackStackEntry?> = navController.currentBackStackEntryAsState()
		val currentScreen: Screens = Screens.fromRoute(backstackEntry.value?.destination?.route)
		val currentGoal: Goal? = goalFromGoalsByUid(goalUid = currentGoalUid, goals = goals)

		Scaffold(
			topBar = {
				// If goals is empty, there is a redirect to the Onboarding screen. Adding && goals.isNotEmpty() removed jank from this switch on first start.
				if (!currentScreen.fullScreen && goals.isNotEmpty()) {
					TopAppBar(
						title = { Text(text = currentScreen.topAppBarName ?: (currentGoal?.title ?: "")) },
						navigationIcon =
						if (currentScreen.showBackNavigation) {
							{
								IconButton(onClick = {
									// TODO: Set current goal to null if back to main screen
									navController.navigateUp()
								}) {
									Icon(Icons.Filled.ArrowBack, "Back")
								}
							}
						} else null,
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
						}
					)
				}
			},
			bottomBar = {
				if (!currentScreen.fullScreen && goals.isNotEmpty()) {
					BottomNavigation {
						// Only show entries that have bottomNavigation == true.
						allScreens.filter { it.bottomNavigation && (it.showWhenPinningPossible || !lambdas.widgetPinningPossible) }.forEach { screen ->
							BottomNavigationItem(
								icon = { screen.icon?.let { Icon(it, contentDescription = null, modifier = Modifier.size(24.dp)) } },
								label = { Text(text = screen.displayName ?: "") },
								selected = currentScreen == screen,
								onClick = {
									navController.navigate(screen.name) {
										if (screen == Screens.GoalsList) {
											// TODO: set current goal to null?
											popUpTo(Screens.GoalsList.name) { inclusive = true }
										} else {
											popUpTo(Screens.GoalsList.name)
										}
									}
								}
							)
						}
					}
				}
			},
			floatingActionButton = {
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
							if (setGoalToNull) goalViewModel.changeCurrentGoalUid(Constants.INVALID_GOAL_UID)
							navController.navigateUp()
						},
						// Getting UID to Onboarding through currentGoal through changeCurrentGoalUid(goalUid)
						// With this UID, get the AppWidgetId
						addWidgetToHomeScreen = { goal: Goal, insertNewGoal: Boolean ->
							val goalUid: Int = lambdas.addWidgetToHomeScreen(goal, insertNewGoal)
							goalViewModel.changeCurrentGoalUid(goalUid)
							goalUid
						}
					),
					modifier = Modifier.padding(innerPadding),
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
	NavHost(
		navController = navController,
		// remember, because otherwise adding the first goal causes a recomposition and exits the Onboarding screen.
		startDestination = remember {
			when {
				goals.isNotEmpty() -> {
					Log.d(TAG, "goals.isNotEmpty() -> {n")
					Screens.GoalsList.name
				}
				lambdas.widgetPinningPossible -> {
					Screens.Onboarding.name
				}
				else -> {
					Screens.Instructions.name
				}
			}
		},
		modifier = modifier,
	) {
		composable(route = Screens.GoalsList.name) {
			Log.d(TAG, "------------GoalsList------------")
			GoalList(
				goals = goals,
				lambdas = lambdas,
			)
		}
		composable(route = Screens.Onboarding.name) {
			Log.d(TAG, "------------Onboarding------------")
			if (lambdas.widgetPinningPossible) {
				Onboarding(
					currentGoal = currentGoal ?: Goal(),
					chatvariant = if (goals.isEmpty()) Chatvariant.Onboarding else Chatvariant.NewGoalOnly,
					lambdas = lambdas,
				)
			} else {
				lambdas.navigateTo(Screens.Instructions.name, null, true)
			}
		}
		composable(route = Screens.Instructions.name) {
			Log.d(TAG, "------------Instructions------------")
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
			} else (Text("currentGoal = null"))
		}
		// Your Progress
		composable(route = Screens.Progress.name) {
			Progress(
				goals = goals,
				lambdas = lambdas,
			)
		}
		// Edit goal
		composable(
			route = Screens.EditGoalView.name,
		) {
			Log.d(TAG, "------------EditGoal $currentGoal------------")
			if (currentGoal != null) {
				EditGoalView(
					initialGoal = currentGoal,
					isFirstConfigure = false,
					lambdas = lambdas,
				)
			} else (
					Text("currentGoal = null")
					)
		}
		// Settings
		composable(
			route = Screens.Settings.name,
		) {
			Log.d(TAG, "------------Settings------------")
			Settings(
				lambdas = lambdas,
			)
		}
	}
}

