package ch.karimattia.workoutpixel.onboarding

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
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
/*
	addNewWidgetToHomeScreen: suspend (Goal) -> Int,
*/
	onboardingViewModel: OnboardingViewModel = viewModel(),
	lambdas: Lambdas,
) {
	val newGoal = onboardingViewModel.goal.observeAsState(initial = Goal())
	val goalDetailViewlambdas = lambdas.copy(
		addWidgetToHomeScreenFilledIn = {
			val uid = lambdas.addWidgetToHomeScreen(newGoal.value, true)
			Log.d(TAG, "uid: $uid")
			onboardingViewModel.updateGoal(newGoal.value.copy(uid = uid))
		}
	)

	val scope: CoroutineScope = rememberCoroutineScope()
	onboardingViewModel.scope = scope

	val shownMessages = onboardingViewModel.shownMessages

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
			goal = newGoal,
			updateGoal = updateGoal,
/*
			addNewWidgetToHomeScreen = addNewWidgetToHomeScreen,
*/
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
	goal: Goal,
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
							goal.intervalBlue = i
							// TODO: Veryfy no copy needed
							updateGoal(goal)
							insertMessageAtNextPosition(messageTemplates.intervalByUser())
						}, text = i.toString())
					}
				}
			}
		}

		// TitleInput
		AnimatedVisibility(visible = lastMessage.bottomArea == BottomArea.TitleInput, enter = EnterTransition.None, exit = ExitTransition.None) {
			TitleTextField(
				value = goal.title,
				onValueChange = { updateGoal(goal.copy(title = it)) },
				action = {
					insertMessageAtNextPosition(messageTemplates.titleByUser())
				},
				scrollDown = scrollDown,
			)
		}

		// AddWidget
		Log.d(TAG, "lastMessage.bottomArea: ${lastMessage.bottomArea}")
		if (lastMessage.bottomArea == BottomArea.AddWidget) {
			Log.d(TAG, "${goal.uid}")
			LaunchedEffect(key1 = true, block = {
				Log.d(TAG, "LaunchedEffect(true) {")
				lambdas.addWidgetToHomeScreenFilledIn()
			})
		}
		LaunchedEffect(key1 = goal.uid, block = {
			Log.d(TAG, "LaunchedEffect(goal) {")
			if (goal.uid != Constants.INVALID_GOAL_UID) insertMessageAtNextPosition(messageTemplates.success())
			// TODO: Observe if goal with this uid has gotten an AppWidgetId
		})
	}
}

@Composable
fun MessageProposal(onClick: () -> Unit, text: String) {
	Button(
		onClick = onClick,
		shape = cardShapeFor(isMine = true),
		colors = ButtonDefaults.buttonColors(backgroundColor = Color.White, contentColor = MaterialTheme.colors.primary),
		border = BorderStroke(width = 1.5.dp, color = MaterialTheme.colors.primary),
		modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
	) {
		Text(text = text)
	}
}


@Composable
fun MessageCard(message: Message) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 8.dp, vertical = 4.dp),
		horizontalAlignment = when { // 2
			message.isMessageByUser -> Alignment.End
			else -> Alignment.Start
		},
	) {
		Card(
			modifier = Modifier.widthIn(max = 340.dp),
			shape = cardShapeFor(message), // 3
			backgroundColor = when {
				message.isMessageByUser -> MaterialTheme.colors.primary
				else -> MaterialTheme.colors.primaryVariant
			},
		) {
			Column {
				Text(
					modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
					text = message.text,
					color = Color.White,
				)
				message.messageExtra()
			}
		}
	}
}

@Composable
fun cardShapeFor(message: Message): Shape = cardShapeFor(message.isMessageByUser)

@Composable
fun cardShapeFor(isMine: Boolean): Shape {
	val roundedCorners = RoundedCornerShape(16.dp)
	return when {
		isMine -> roundedCorners.copy(bottomEnd = CornerSize(0))
		else -> roundedCorners.copy(bottomStart = CornerSize(0))
	}
}

@ExperimentalComposeUiApi
@Composable
fun TitleTextField(
	value: String,
	onValueChange: (String) -> Unit,
	action: (String) -> Unit,
	scrollDown: () -> Unit,
) {

	val keyboardController = LocalSoftwareKeyboardController.current

	TextField(
		value = value,
		onValueChange = onValueChange,
		trailingIcon = {
			Icon(
				imageVector = Icons.Filled.Send,
				contentDescription = null,
				modifier = Modifier.clickable {
					action(value)
				}
			)
		},
		colors = TextFieldDefaults.textFieldColors(
			textColor = MaterialTheme.colors.onBackground,
			disabledTextColor = Color.Transparent,
			backgroundColor = Color.White,
			focusedIndicatorColor = Color.Transparent,
			unfocusedIndicatorColor = Color.Transparent,
			disabledIndicatorColor = Color.Transparent
		),
		placeholder = { Text(text = "E.g. Push ups") },
		shape = CircleShape,
		maxLines = 1,
		keyboardOptions = KeyboardOptions(
			capitalization = KeyboardCapitalization.Sentences,
			imeAction = ImeAction.Next
		),
		keyboardActions = KeyboardActions(onNext = {
			action(value)
			keyboardController?.hide()
		}
		),
		modifier = Modifier
			.fillMaxWidth()
			.background(Color.LightGray)
			.padding(all = 8.dp)
			.onFocusChanged {
				Log.d(TAG, "onFocusChanged")
				scrollDown()
			}
	)
}