package ch.karimattia.workoutpixel.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData

data class ChatMessage(
	val text: String = "",
	val isMessageByUser: Boolean = false,
	val autoAdvance: Boolean = isMessageByUser,
	// TODO: Set reasonable values
	val autoAdvanceTime: Int = 100,
	val proposals: List<MessageProposal> = emptyList(),
	val chatInputField: ChatInputField? = null,
	val nextMessage: (MessageBuilder)? = null,
	// One next message should be sufficient - the next one can be put into the next message.
	// val nextMessages: List<MessageBuilder> = if (nextMessage != null) listOf(nextMessage) else emptyList(),
	val action: (() -> Unit)? = null,
	val messageExtra: @Composable () -> Unit = {},
) {
	@ExperimentalComposeUiApi
	@Composable
	fun BottomArea() {
		Column {
			MessageProposals()
			ChatInputField()
		}
	}

	@Composable
	fun MessageProposals() {
		// All messageProposals
		AnimatedVisibility(visible = proposals.isNotEmpty(), enter = fadeIn(), exit = ExitTransition.None) {
			Row(
				horizontalArrangement = Arrangement.End,
				modifier = Modifier
					.fillMaxWidth()
					.horizontalScroll(state = rememberScrollState())
			) {
				for (proposal in proposals) {
					MessageProposal(proposal)
				}
			}
		}
	}

	@ExperimentalComposeUiApi
	@Composable
	fun ChatInputField() {
		// All messageProposals
		// TextInput
		AnimatedVisibility(visible = chatInputField != null, enter = EnterTransition.None, exit = ExitTransition.None) {
			if (chatInputField != null) {
				ChatInputField(
					chatInputField = chatInputField,
				)
			}
		}
	}
}

data class MessageProposal(
	val proposalText: String = "",
	val action: () -> Unit,
)

data class ChatInputField(
	val value: LiveData<String>,
	val onValueChange: (String) -> Unit,
	val action: (String) -> Unit = {},
	val scrollDown: () -> Unit,
)