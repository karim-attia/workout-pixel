package ch.karimattia.workoutpixel.onboarding

import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData

data class Message(
	val text: String = "",
	val isMessageByUser: Boolean = false,
	val autoAdvance: Boolean = false,
	// TODO: Set reasonable values
	val autoAdvanceTime: Int = 60,
	val proposals: List<MessageProposal> = emptyList(),
	val chatInputField: ChatInputField? = null,
	val nextMessage: (MessageBuilder)? = null,
	val nextMessages: List<MessageBuilder> = if (nextMessage != null) listOf(nextMessage) else emptyList(),
	val action: (() -> Unit)? = null,
	val messageExtra: @Composable () -> Unit = {},
) {
	fun debugString(): String = "text: $text, autoAdvance: $autoAdvance"
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

enum class BottomArea {
	AutoAdvance, ShowNext, AddWidgetPrompt, TitleInput, IntervalInput, AddWidget, End
}