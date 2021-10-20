package ch.karimattia.workoutpixel.onboarding

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier

@ExperimentalComposeUiApi
@Composable
fun ChatMainScreen(
	chatViewModel: ChatViewModel,
) {
	ChatMainScreen(
		shownMessages = chatViewModel.shownMessages,
		scrollState = chatViewModel.scrollState)
}

@ExperimentalComposeUiApi
@Composable
fun ChatMainScreen(
	shownMessages: List<ChatMessage>,
	scrollState: ScrollState,
) {
	val lastMessage = shownMessages.last()
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
		BottomArea(lastMessage = lastMessage)
/*
		BottomArea(
			lastMessage = lastMessage,
		)*/
	}

/*	// TODO: Move to ChatViewModel
	// MessageAction
	// For every new lastMessage, execute its action once if there is one.
	val lastValueOfLastMessage = remember { mutableStateOf(lastMessage) }
	if (lastValueOfLastMessage.value != lastMessage) {
		lastMessage.action?.let { it() }
		lastValueOfLastMessage.value = lastMessage
	}*/
}

@ExperimentalComposeUiApi
@Composable
fun BottomArea(
	lastMessage: ChatMessage,
) {
	Box(
		contentAlignment = Alignment.CenterEnd,
		modifier = Modifier
			.fillMaxWidth()
	)
	{
		// Message proposals

		// All messageProposals
		AnimatedVisibility(visible = lastMessage.proposals.isNotEmpty(), enter = fadeIn(), exit = ExitTransition.None) {
			Row(
				horizontalArrangement = Arrangement.End,
				modifier = Modifier
					.fillMaxWidth()
					.horizontalScroll(state = rememberScrollState())
			) {
				for (proposal in lastMessage.proposals) {
					MessageProposal(proposal)
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
	}
}
