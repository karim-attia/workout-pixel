package ch.karimattia.workoutpixel.onboarding

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
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
	currentGoal: Goal,
	lambdas: Lambdas,
) {
	val scope: CoroutineScope = rememberCoroutineScope()
	onboardingViewModel.scope = scope

	val newGoal = onboardingViewModel.goal.observeAsState(initial = Goal())
	// TODO: Cases where there is a current goal to start with.
	val firstGoalChange = remember { currentGoal.appWidgetId }
	LaunchedEffect(key1 = currentGoal.appWidgetId, block = {
		// The appWidgetId of the current goal changed.
		if (currentGoal.appWidgetId != firstGoalChange || currentGoal.uid != Constants.INVALID_GOAL_UID) onboardingViewModel.updateGoal(currentGoal)
	})
	val shownMessages = onboardingViewModel.shownMessages

	val goalDetailViewlambdas = lambdas.copy(
		addWidgetToHomeScreenFilledIn = {
			val uid = lambdas.addWidgetToHomeScreen(newGoal.value, true)
			Log.d(TAG, "uid: $uid")
			onboardingViewModel.updateGoal(newGoal.value.copy(uid = uid))
		}
	)
	onboardingViewModel.lambdas = goalDetailViewlambdas

	Onboarding(
		shownMessages = shownMessages,
		lastMessage = shownMessages.last(),
		messageTemplates = onboardingViewModel.messageTemplates,
		// Convert message template to message builder.
		insertMessageAtNextPosition = { messageTemplate -> onboardingViewModel.insertMessageBuilderToQueueAtNextPositionAndAdvance(messageBuilder = { messageTemplate }) },
		scrollState = onboardingViewModel.scrollState,
		newGoal = newGoal.value,
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
	insertMessageAtNextPosition: (Message) -> Unit,
	scrollState: ScrollState,
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
			insertMessageAtNextPosition = insertMessageAtNextPosition,
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
	insertMessageAtNextPosition: (Message) -> Unit,
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
			// All messageProposals
			AnimatedVisibility(visible = lastMessage.proposals.isNotEmpty(), enter = fadeIn(), exit = ExitTransition.None) {
				Row {
					for (proposal in lastMessage.proposals) {
						MessageProposal(proposal)
					}
				}
			}
		}

		// TextInput
		AnimatedVisibility(visible = lastMessage.chatInputField != null, enter = EnterTransition.None, exit = ExitTransition.None) {
			if (lastMessage.chatInputField != null) {
				ChatInputField(
					chatInputField = lastMessage.chatInputField,
				)
			}
		}

		// MessageAction
		// For every new lastMessage, execute its action once if there is one.
		val lastValueOfLastMessage = remember { mutableStateOf(lastMessage) }
		if (lastValueOfLastMessage.value != lastMessage) {
			lastMessage.action?.let { it() }
			lastValueOfLastMessage.value = lastMessage
		}

	/*// AddWidget
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
	}*/
/*
		LaunchedEffect(key1 = newGoal.appWidgetId, block = {
			Log.d(TAG, "LaunchedEffect(goal) {")
			if (newGoal.appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) insertMessageAtNextPosition(messageTemplates.success())
		})
*/
}
}
