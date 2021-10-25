package ch.karimattia.workoutpixel.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.LiveData

@ExperimentalComposeUiApi
data class ChatMessage (
	// Text in the chat bubble of this message.
	val text: String = "",
	// Whether this message is by the user or by the app. Determines color, whether the message is shown on the left or the right of the screen.
	val isMessageByUser: Boolean = false,
	// This message is added to the messageBuilderQueue in ChatViewModel at the next position.
	// Since it's a MessageBuilder, all functions are only evaluated at the time when it is actually inserted.
	val nextMessage: (MessageBuilder)? = null,
	// One next message should be sufficient - the next one can be put into the next message.
	// val nextMessages: List<MessageBuilder> = if (nextMessage != null) listOf(nextMessage) else emptyList(),
	// This action is executed as soon as the message is shown on the screen.
	val action: (() -> Unit)? = null,
	// Composable shown below the text in the message bubble.
	val messageExtra: (@Composable () -> Unit)? = null,
	// Automatically continue to the next message after showing this message.
	val autoAdvance: Boolean = isMessageByUser,
	val autoAdvanceTime: Int = if (isMessageByUser) 400 else 800,
	// A list of proposals that are shown to the user.
	val messageProposals: List<MessageProposal> = emptyList(),
	// The configuration of the user text input field if there is one.
	val chatInputField: ChatInputField? = null,
	// Show the messageProposals and chatInputField on the bottom of the screens if they are defined.
	// Can be replaced with a custom implementation per message.
	val bottomArea: (@Composable () -> Unit)? = if (messageProposals.isNotEmpty() || chatInputField != null) {{ BottomArea(messageProposals = messageProposals, chatInputField = chatInputField) }} else null,
)

data class MessageProposal(
	val proposalText: String = "",
	val action: () -> Unit,
)

data class ChatInputField(
	val value: LiveData<String>,
	val onValueChange: (String) -> Unit,
	val action: (String) -> Unit = {},
	val scrollDown: () -> Unit,
	val placeholder: String = "",
)