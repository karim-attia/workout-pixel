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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@ExperimentalComposeUiApi
@Composable
fun ChatMainScreen(
	chatViewModel: ChatViewModel,
	spacerTop: Dp = 8.dp,
) {
	ChatMainScreen(
		shownMessages = chatViewModel.shownMessages,
		scrollState = chatViewModel.scrollState,
		spacerTop = spacerTop,
	)
}

@ExperimentalComposeUiApi
@Composable
fun ChatMainScreen(
	shownMessages: List<ChatMessage>,
	scrollState: ScrollState,
	spacerTop: Dp,
	) {
	val lastMessage = shownMessages.lastOrNull()
	Column {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.verticalScroll(scrollState)
				.weight(1f)
		) {
			Spacer(Modifier.height(spacerTop))
			for (message in shownMessages) {
				MessageCard(message = message)
			}
		}
		lastMessage?.let { BottomArea(lastMessage = lastMessage) }
/*
		lastMessage.BottomArea()
		*/
	}
}

@ExperimentalComposeUiApi
@Composable
fun BottomArea(
	lastMessage: ChatMessage,
) {
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
