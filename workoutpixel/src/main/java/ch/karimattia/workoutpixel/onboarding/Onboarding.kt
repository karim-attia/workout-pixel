package ch.karimattia.workoutpixel.onboarding

import android.appwidget.AppWidgetManager
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.karimattia.workoutpixel.composables.Lambdas
import ch.karimattia.workoutpixel.core.Constants
import ch.karimattia.workoutpixel.data.Goal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

@Suppress("unused")
private const val TAG: String = "Onboarding"

@ExperimentalComposeUiApi
@Composable
fun Onboarding(
	onboardingViewModel: OnboardingViewModel = viewModel(),
	currentGoal: Goal,
	lambdas: Lambdas,
) {
	val scope: CoroutineScope = rememberCoroutineScope()
	onboardingViewModel.scope = scope

	val newGoal = onboardingViewModel.goal.observeAsState(initial = Goal())
	LaunchedEffect(key1 = currentGoal, block = {
		if (currentGoal.uid != Constants.INVALID_GOAL_UID) onboardingViewModel.updateGoal(currentGoal)
	})
	val shownMessages = onboardingViewModel.shownMessages

	val goalDetailViewlambdas = lambdas.copy(
		addWidgetToHomeScreenFilledIn = {
			val uid = lambdas.addWidgetToHomeScreen(newGoal.value, true)
			Log.d(TAG, "uid: $uid")
			onboardingViewModel.updateGoal(newGoal.value.copy(uid = uid))
		}
	)

	Onboarding(
		shownMessages = shownMessages,
		lastMessage = shownMessages.last(),
		messageTemplates = onboardingViewModel.messageTemplates,
		// Convert message template to message builder.
		insertMessageAtNextPosition = { messageTemplate -> onboardingViewModel.insertMessageBuilderToQueueAtNextPositionAndAdvance(messageBuilder = { messageTemplate }) },
		scrollState = onboardingViewModel.scrollState,
		scrollDown = { onboardingViewModel.scrollDown() },
		newGoal = newGoal.value,
		updateGoal = { goal -> onboardingViewModel.updateGoal(goal) },
		// currentGoal = currentGoal,
		lambdas = goalDetailViewlambdas,
	)
}

@ExperimentalComposeUiApi
@Composable
fun Onboarding(
	shownMessages: List<Message>,
	lastMessage: Message,
	messageTemplates: OnboardingViewModel.MessageTemplates,
	newGoal: Goal,
	// currentGoal: Goal,
	updateGoal: (Goal) -> Unit,
	insertMessageAtNextPosition: (Message) -> Unit,
	scrollState: ScrollState,
	scrollDown: () -> Unit,
	lambdas: Lambdas,
) {
	Log.d(TAG, "goaluid: ${newGoal.uid}")

	Column {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.verticalScroll(scrollState)
				.weight(1f)
		) {
			for (message in shownMessages) {
				MessageCard(message = message)
			}
		}
		BottomArea(
			lastMessage = lastMessage,
			messageTemplates = messageTemplates,
			newGoal = newGoal,
			// currentGoal = currentGoal,
			updateGoal = updateGoal,
			insertMessageAtNextPosition = insertMessageAtNextPosition,
			scrollDown = { scrollDown() },
			lambdas = lambdas,

			)
	}
}

@ExperimentalComposeUiApi
@Composable
fun BottomArea(
	lastMessage: Message,
	messageTemplates: OnboardingViewModel.MessageTemplates,
	newGoal: Goal,
	// currentGoal: Goal,
	updateGoal: (Goal) -> Unit,
	insertMessageAtNextPosition: (Message) -> Unit,
	scrollDown: () -> Unit,
	lambdas: Lambdas,
) {
	Box(
		contentAlignment = Alignment.CenterEnd,
		modifier = Modifier
			.fillMaxWidth()
	)
	{
		// Message proposals
		Row(
			horizontalArrangement = Arrangement.End,
			modifier = Modifier
				.fillMaxWidth()
				.horizontalScroll(state = rememberScrollState())
		)
		{
			// Specific set of different proposals. Here for the add widget prompt.
			AnimatedVisibility(visible = lastMessage.bottomArea == BottomArea.AddWidgetPrompt, enter = fadeIn(), exit = ExitTransition.None) {
				Row {
					MessageProposal(onClick = {
						insertMessageAtNextPosition(messageTemplates.editGoalDescription())
					}, text = "Edit goal description")
					MessageProposal(onClick = { insertMessageAtNextPosition(messageTemplates.thumbsUpProposal()) }, text = lastMessage.proposal)
				}
			}

			// Only proposal next available.
			AnimatedVisibility(visible = lastMessage.showNextProposal,
				enter = fadeIn(),
				exit = ExitTransition.None) {
				MessageProposal(onClick = { insertMessageAtNextPosition(messageTemplates.nextByUser()) }, text = lastMessage.proposal)
			}

			// IntervalInput
			AnimatedVisibility(visible = lastMessage.bottomArea == BottomArea.IntervalInput, enter = fadeIn(), exit = ExitTransition.None) {
				Row {
					for (i in 1..31) {
						MessageProposal(onClick = {
							newGoal.intervalBlue = i
							// TODO: Veryfy no copy needed
							updateGoal(newGoal)
							insertMessageAtNextPosition(messageTemplates.intervalByUser())
						}, text = i.toString())
					}
				}
			}
		}

		// TitleInput
		AnimatedVisibility(visible = lastMessage.bottomArea == BottomArea.TitleInput, enter = EnterTransition.None, exit = ExitTransition.None) {
			TitleTextField(
				value = newGoal.title,
				onValueChange = { updateGoal(newGoal.copy(title = it)) },
				action = {
					insertMessageAtNextPosition(messageTemplates.titleByUser())
				},
				scrollDown = scrollDown,
			)
		}

		// AddWidget
		Log.d(TAG, "lastMessage.bottomArea: ${lastMessage.bottomArea}")
		if (lastMessage.bottomArea == BottomArea.AddWidget) {
			Log.d(TAG, "${newGoal.uid}")
			// TODO: This block is not in anymore after inserted message because it's not bottom area
			LaunchedEffect(key1 = true, block = {
				Log.d(TAG, "LaunchedEffect(true) {")
				lambdas.addWidgetToHomeScreenFilledIn()
				delay(3000)
				Log.d(TAG, "firstdelay")
				Log.d(TAG, "newGoal.appWidgetId: ${newGoal.appWidgetId}")
				Log.d(TAG, "newGoal.appWidgetId true: ${newGoal.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID}")
				if (newGoal.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) insertMessageAtNextPosition(messageTemplates.waitingForCallback())
				delay(3000)
				Log.d(TAG, "seconddelay")
				// TODO: check and add messageproposal thumbs up
				if (newGoal.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) insertMessageAtNextPosition(messageTemplates.retryPrompt())
				Log.d(TAG, "endblock")

			})
		}
		LaunchedEffect(key1 = newGoal.appWidgetId, block = {
			Log.d(TAG, "LaunchedEffect(goal) {")
			if (newGoal.appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) insertMessageAtNextPosition(messageTemplates.success())
		})
	}
}
