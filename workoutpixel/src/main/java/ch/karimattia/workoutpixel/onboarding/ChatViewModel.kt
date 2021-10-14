package ch.karimattia.workoutpixel.onboarding

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG: String = "ChatViewModel"

abstract class ChatViewModel : ViewModel() {
	/**
	 * Specifies the first message of the message chain
	 * */
	abstract val firstMessage: Message

	/**
	 * The backlog of the message builders to be shown to the user (as soon as the currentStep allows for it)
	 * */
	private val messageBuilderQueue: MutableList<MessageBuilder> by lazy { mutableStateListOf({ firstMessage }) }

	/**
	 * The subset of messages in messageQueue that are shown to the user. Determined by currentStep.
	 * */
	val shownMessages: SnapshotStateList<Message> by lazy { mutableStateListOf(firstMessage) }

	private fun currentStep() = shownMessages.size

	/**
	 * Check if initially the firstMessage (=latestMessage at this point) has an action and if yes, process it.
	 * */
	fun initialize() {
/*
		Log.d(TAG, "initialize")
		Log.d(TAG, firstMessage.debugString())
		Log.d(TAG, "shownMessages: ${shownMessages.size}")
		Log.d(TAG, "messageBuilderQueue: ${messageBuilderQueue.size}")
*/
		processLatestMessage(firstMessage)
	}

	/**
	 * Show one more message and update the shownMessages and latestMessage accordingly.
	 *
	 * Check if the latestMessage has an action and if yes, process it.
	 * */
	private fun increaseCurrentStep() {
		/**
		 * The last message of the shown messages.
		 * Used to triage what should be done next, e.g. autoAdvance, show message proposals or input fields to the user.
		 * */
		val latestMessage: Message = messageBuilderQueue[currentStep()]()
		shownMessages.add(latestMessage)
		scrollDown()

		processLatestMessage(latestMessage)
	}

	/**
	 * Check if the latestMessage has an action and if yes, process it. I.e. adding follow-up messages and autoAdvancing.
	 * */
	private fun processLatestMessage(lastMessage: Message) {
		Log.d(TAG, "processLatestMessage")
		lastMessage.nextMessage?.let { nextMessage -> insertMessageBuilderToQueueAtNextPosition(nextMessage) }
		checkAutoAdvance(lastMessage)
	}

	/**
	 * If the latestMessage has an autoAdvance flag, wait and then autoAdvance.
	 * * */
	private fun checkAutoAdvance(latestMessage: Message) {
		if (latestMessage.autoAdvance && currentStep() < messageBuilderQueue.size) {
			viewModelScope.launch {
				delay(latestMessage.autoAdvanceTime.toLong())
				increaseCurrentStep()
			}
		}
	}

	fun insertMessageBuilderToQueueAtNextPositionAndAdvance(messageBuilder: MessageBuilder) {
		insertMessageBuilderToQueueAtNextPosition(messageBuilder = messageBuilder)
		increaseCurrentStep()
	}

	private fun insertMessageBuilderToQueueAtNextPosition(messageBuilder: MessageBuilder) {
		messageBuilderQueue.add(index = currentStep(), element = messageBuilder)
	}

	/**
	 * Needs to be initialized by activity in order that automatic scrolling works.
	 * * */
	var scope: CoroutineScope? = null
	val scrollState: ScrollState = ScrollState(0)

	/**
	 * Scroll to the bottom of the chat history.
	 * * */
	fun scrollDown() {
		scope?.launch {
			scrollState.animateScrollTo(scrollState.value + 10000)
		}
	}
}
