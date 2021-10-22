package ch.karimattia.workoutpixel.onboarding

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.karimattia.workoutpixel.composables.Lambdas
import ch.karimattia.workoutpixel.data.Goal
import kotlinx.coroutines.CoroutineScope

@Suppress("unused")
private const val TAG: String = "Onboarding"

@ExperimentalComposeUiApi
@Composable
fun Onboarding(
	// First with uid, then with appwidgetid
	currentGoal: Goal,
	lambdas: Lambdas,
	onboardingViewModel: OnboardingViewModel = viewModel(factory = OnboardingViewModelFactory(Chatvariant.Onboarding, rememberCoroutineScope(), lambdas = lambdas)),
	) {

	// If the caller sends a new currentGoal, update the viewModel with it. If it's the same one (e.g. through a recomposition), do not update.
	val lastValueOfCurrentGoal = remember { mutableStateOf(currentGoal) }
	if (lastValueOfCurrentGoal.value != currentGoal) {
		onboardingViewModel.savedGoal.value = currentGoal
		lastValueOfCurrentGoal.value = currentGoal
	}

	ChatMainScreen(
		chatViewModel = onboardingViewModel,
	)
}

