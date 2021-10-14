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
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData

@Suppress("unused")
private const val TAG: String = "Onboarding"

@ExperimentalComposeUiApi
@Composable
fun Onboarding(
	addNewWidgetToHomeScreen: (Goal) -> Unit,
	onboardingViewModel: OnboardingViewModel = viewModel(),
) {
	onboardingViewModel.scope = rememberCoroutineScope()

	val shownMessages = onboardingViewModel.shownMessages

	Onboarding(
		shownMessages = shownMessages,
		lastMessage = shownMessages.last(),
		messageTemplates = onboardingViewModel.allMessagesClass,
		// Convert message template to message builder.
		insertMessageAtNextPosition = {messageTemplate -> onboardingViewModel.insertMessageBuilderToQueueAtNextPositionAndAdvance(messageBuilder = { messageTemplate }) },
		scrollState = onboardingViewModel.scrollState,
		scrollDown = { onboardingViewModel.scrollDown() },
		goal = onboardingViewModel.goal.observeAsState(initial = Goal()).value,
		updateGoal = { onboardingViewModel.updateGoal(it) },
		addNewWidgetToHomeScreen = addNewWidgetToHomeScreen,
		onboardingViewModel = onboardingViewModel,

		)
}

@ExperimentalComposeUiApi
@Composable
fun Onboarding(
	shownMessages: List<Message>,
	lastMessage: Message,
	messageTemplates: OnboardingViewModel.MessageTemplates,
	goal: Goal,
	updateGoal: (Goal) -> Unit,
	addNewWidgetToHomeScreen: (Goal) -> Unit,
	insertMessageAtNextPosition: (Message) -> Unit,
	scrollState: ScrollState,
	scrollDown: () -> Unit,
	onboardingViewModel: OnboardingViewModel,
) {
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
/*
			for (i in 0..minOf(currentStep, messageList.size - 1)) {
				Log.d(TAG, "currentStep: $currentStep")
				Log.d(TAG, "i: $i")
				if (messageList[i].messageExtra == null) MessageCard(message = messageList[i])
				else Column(
					horizontalAlignment = when { // 2
						messageList[i].isMine -> Alignment.End
						else -> Alignment.Start
					},
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 8.dp, vertical = 4.dp),
				) {
					messageList[i].messageExtra!!()
				}
			}
*/
		}
		BottomArea(
			lastMessage = lastMessage,
			messageTemplates = messageTemplates,
			goal = goal,
			updateGoal = updateGoal,
			addNewWidgetToHomeScreen = addNewWidgetToHomeScreen,
			insertMessageAtNextPosition = insertMessageAtNextPosition,
			scrollDown = { scrollDown() },
			onboardingViewModel = onboardingViewModel,
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
	addNewWidgetToHomeScreen: (Goal) -> Unit,
	scrollDown: () -> Unit,
	onboardingViewModel: OnboardingViewModel,
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
			Log.d(TAG, "1")
			Log.d(TAG, "${lastMessage.debugString()}")

			// Specific set of different proposals. Here for the add widget prompt.
			AnimatedVisibility(visible = lastMessage.bottomArea == BottomArea.AddWidgetPrompt, enter = fadeIn(), exit = ExitTransition.None) {
				Log.d(TAG, "2")
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
		if (lastMessage.bottomArea == BottomArea.AddWidget) {
			addNewWidgetToHomeScreen(goal)
		}
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