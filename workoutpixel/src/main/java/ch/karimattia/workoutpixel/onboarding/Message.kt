package ch.karimattia.workoutpixel.onboarding

import androidx.compose.runtime.Composable

data class Message(
	val text: String = "",
	val isMessageByUser: Boolean = false,
	val bottomArea: BottomArea = BottomArea.AutoAdvance,
	val autoAdvance: Boolean = bottomArea == BottomArea.AutoAdvance || bottomArea == BottomArea.AddWidget,
	// TODO: Set reasonable values
	val autoAdvanceTime: Int = 60,
	val showNextProposal: Boolean = bottomArea == BottomArea.ShowNext,
	val proposal: String = if (bottomArea == BottomArea.ShowNext) "Next" else "",
	val nextMessage: (MessageBuilder)? = null,
	// val nextMessages: List<MessageBuilder> = if (nextMessage != null) listOf(nextMessage) else emptyList(),
	// TODO: Show below
	val messageExtra: @Composable () -> Unit = {},
) {
	fun debugString(): String = "text: $text, autoAdvance: $autoAdvance"
}

enum class BottomArea {
	AutoAdvance, ShowNext, AddWidgetPrompt, TitleInput, IntervalInput, AddWidget
}