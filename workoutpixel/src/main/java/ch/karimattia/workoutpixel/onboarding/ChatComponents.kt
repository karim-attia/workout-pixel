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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp

@Suppress("unused")
private const val TAG: String = "ChatComponents"

@ExperimentalComposeUiApi
@Composable
fun MessageCard(message: ChatMessage) {
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
			modifier = Modifier.widthIn(min = 32.dp, max = 340.dp),
			shape = cardShapeFor(message), // 3
			backgroundColor = when {
				message.isMessageByUser -> MaterialTheme.colors.secondary
				else -> MaterialTheme.colors.primary
			},
		) {
			Column(
				modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
			) {
				Text(
					// modifier = Modifier.padding(bottom = 8.dp),
					text = message.text,
					color = when {
						message.isMessageByUser -> MaterialTheme.colors.onSecondary
						else -> MaterialTheme.colors.onPrimary
					},
				)
				message.messageExtra?.invoke()
			}
		}
	}
}

@Composable
fun MessageProposal(messageProposal: MessageProposal) {
	Button(
		onClick = messageProposal.action,
		shape = cardShapeFor(isMine = true),
		colors = ButtonDefaults.buttonColors(backgroundColor = Color.White, contentColor = MaterialTheme.colors.secondary),
		border = BorderStroke(width = 1.5.dp, color = MaterialTheme.colors.secondary),
		modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
	) {
		Text(text = messageProposal.proposalText, fontWeight = FontWeight.SemiBold)
	}
}

@ExperimentalComposeUiApi
@Composable
fun cardShapeFor(message: ChatMessage): Shape = cardShapeFor(message.isMessageByUser)

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
fun ChatInputFieldImpl(
	chatInputField: ChatInputField,
) {
	val value: String = chatInputField.value.observeAsState(initial = "").value
	val onValueChange: (String) -> Unit = chatInputField.onValueChange
	val action: (String) -> Unit = chatInputField.action
	val scrollDown: () -> Unit = chatInputField.scrollDown
	val placeholder: String = chatInputField.placeholder

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
		placeholder = { Text(text = placeholder) },
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

@ExperimentalComposeUiApi
@Composable
fun BottomArea(messageProposals: List<MessageProposal>, chatInputField: ChatInputField?) {
	Column {
		MessageProposals(messageProposals)
		ChatInputField(chatInputField)
	}
}

@Composable
fun MessageProposals(messageProposals: List<MessageProposal>) {
	// All messageProposals
	AnimatedVisibility(visible = messageProposals.isNotEmpty(), enter = fadeIn(), exit = ExitTransition.None) {
		Row(
			horizontalArrangement = Arrangement.End,
			modifier = Modifier
				.fillMaxWidth()
				.horizontalScroll(state = rememberScrollState())
		) {
			for (proposal in messageProposals) {
				MessageProposal(proposal)
			}
		}
	}
}

@ExperimentalComposeUiApi
@Composable
fun ChatInputField(chatInputField: ChatInputField?) {
	// All messageProposals
	// TextInput
	AnimatedVisibility(visible = chatInputField != null, enter = EnterTransition.None, exit = ExitTransition.None) {
		if (chatInputField != null) {
			ChatInputFieldImpl(
				chatInputField = chatInputField,
			)
		}
	}
}
