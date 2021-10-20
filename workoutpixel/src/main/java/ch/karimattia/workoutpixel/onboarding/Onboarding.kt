package ch.karimattia.workoutpixel.onboarding

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.karimattia.workoutpixel.composables.Lambdas
import ch.karimattia.workoutpixel.core.Constants
import ch.karimattia.workoutpixel.data.Goal
import kotlinx.coroutines.CoroutineScope

@Suppress("unused")
private const val TAG: String = "Onboarding"

@ExperimentalComposeUiApi
@Composable
fun Onboarding(
	onboardingViewModel: OnboardingViewModel = viewModel(),
	// First with uid, then with appwidgetid
	currentGoal: Goal,
	lambdas: Lambdas,
) {
	val scope: CoroutineScope = rememberCoroutineScope()
	onboardingViewModel.scope = scope
	onboardingViewModel.insertLambdas(lambdas = lambdas)
	onboardingViewModel.savedGoal.value = currentGoal

/*	// TODO: Cases where there is a current goal to start with.
	val initialGoal = remember { currentGoal }
	LaunchedEffect(key1 = currentGoal.appWidgetId, block = {
		// The appWidgetId of the current goal changed.
		Log.d(TAG, "LaunchedEffect goalSwitch. firstGoalChange: ${initialGoal.appWidgetId}")
		Log.d(TAG, "LaunchedEffect goalSwitch. currentGoal: ${currentGoal.appWidgetId}")
		if (currentGoal.appWidgetId != initialGoal.appWidgetId || currentGoal.uid != Constants.INVALID_GOAL_UID) onboardingViewModel.updateGoal(currentGoal)
	})*/

	ChatMainScreen(
		chatViewModel = onboardingViewModel,
	)
}

