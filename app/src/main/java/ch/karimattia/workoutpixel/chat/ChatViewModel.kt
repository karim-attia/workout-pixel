package ch.karimattia.workoutpixel.chat

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.karimattia.workoutpixel.screens.onboarding.MessageBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("unused")
private const val TAG: String = "ChatViewModel"

@ExperimentalComposeUiApi
abstract class ChatViewModel : ViewModel() {
	/**
	 * Specifies the first message of the message chain
	 * */
	// abstract val firstMessage: ChatMessage

	/**
	 * The backlog of the message builders to be shown to the user (as soon as the currentStep allows for it)
	 * */
	private val messageBuilderQueue: MutableList<MessageBuilder> = mutableStateListOf()

	/**
	 * The subset of messages in messageQueue that are shown to the user. Determined by currentStep.
	 * */
	val shownMessages: SnapshotStateList<ChatMessage> = mutableStateListOf()

	// TODO: Replace inline
	private fun currentStep() = shownMessages.size

	/**
	 * Check if initially the firstMessage (=latestMessage at this point) has an action and if yes, process it.
	 * */
	fun initialize(firstMessage: MessageBuilder, scope: CoroutineScope) {
		this.scope = scope
		insertMessageBuilderToQueueAtNextPositionAndAdvance (firstMessage)
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
		val lastMessage: ChatMessage = messageBuilderQueue[currentStep()]()
		Log.d(TAG, "increaseCurrentStep: ${lastMessage.text}")
		shownMessages.add(lastMessage)
		scrollDown()

		processLastMessage(lastMessage)
	}

	/**
	 * Check if the latestMessage has an action and if yes, process it. I.e. adding follow-up messages and autoAdvancing.
	 * */
	private fun processLastMessage(lastMessage: ChatMessage) {
		Log.d(TAG, "processLastMessage: ${lastMessage.text}")
		lastMessage.nextMessage?.let { nextMessage -> insertMessageBuilderToQueueAtNextPosition(nextMessage) }

		// MessageAction
		// For every new lastMessage, execute its action once if there is one.
		lastMessage.action?.let { action: () -> Unit -> action() }

		checkAutoAdvance(lastMessage)
	}

	/**
	 * If the latestMessage has an autoAdvance flag, wait and then autoAdvance.
	 * * */
	private fun checkAutoAdvance(lastMessage: ChatMessage) {
		Log.d(TAG, "checkAutoAdvance: ${lastMessage.text}")
		if (lastMessage.autoAdvance && currentStep() < messageBuilderQueue.size) {
			Log.d(TAG, "checkAutoAdvance: true")
			viewModelScope.launch {
				delay(lastMessage.autoAdvanceTime.toLong())
				increaseCurrentStep()
			}
		}
	}

	/**
	 * Insert a new message to the queue, e.g. when a user clicks on a message proposal.
	 * * */
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
	private var scope: CoroutineScope? = null
	val scrollState: ScrollState = ScrollState(0)

	/**
	 * Scroll to the bottom of the chat history.
	 * * */
	private fun scrollDown() {
		scope?.launch {
			scrollState.animateScrollTo(scrollState.maxValue + 5000)
		}
	}

	fun messageProposalOf(
		proposalText: String = "",
		proposalAction: () -> Unit = {},
		insertsMessage: MessageBuilder,
	): MessageProposal = MessageProposal(
		proposalText = proposalText,
		action = {
			proposalAction()
			insertMessageBuilderToQueueAtNextPositionAndAdvance(insertsMessage)
		},
	)

	fun chatInputFieldOf(
		value: LiveData<String>,
		onValueChange: (String) -> Unit,
		insertMessage: MessageBuilder,
		placeholder: String
	): ChatInputField = ChatInputField(
		value = value,
		onValueChange = onValueChange,
		action = { insertMessageBuilderToQueueAtNextPositionAndAdvance(insertMessage) },
		scrollDown = ::scrollDown,
		placeholder = placeholder,
	)
}
