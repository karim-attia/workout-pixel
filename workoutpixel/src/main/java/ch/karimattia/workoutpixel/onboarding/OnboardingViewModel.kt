package ch.karimattia.workoutpixel.onboarding

import android.appwidget.AppWidgetManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.*
import ch.karimattia.workoutpixel.composables.GoalPreviewWithBackground
import ch.karimattia.workoutpixel.composables.GoalPreviewsWithBackground
import ch.karimattia.workoutpixel.composables.Lambdas
import ch.karimattia.workoutpixel.core.Status
import ch.karimattia.workoutpixel.data.Goal
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("unused")
private const val TAG: String = "OnboardingViewModel"
typealias MessageBuilder = () -> ChatMessage

class OnboardingViewModel : ChatViewModel() {
	/**
	 * Class with all message templates
	 * */
	private val messageTemplates: MessageTemplates = MessageTemplates()

	/**
	 * Specifies the first message of the message chain
	 * */
	override var firstMessage: ChatMessage = messageTemplates.introMessage()

	/**
	 * Initialize the ChatViewModel: Process the first message.
	 * */
	init {
		initialize()
	}

	private val editableGoal: MutableLiveData<Goal> = MutableLiveData(Goal())
	private var isGoalSaved: Boolean = false
	val savedGoal: MutableLiveData<Goal> = MutableLiveData(Goal())
	val goalTitle: LiveData<String> = editableGoal.map { it.title }

	var widgetPinSuccessful: Boolean = false

	// Reset editableGoal and flags when a new goal is created.
	fun newGoal() {
		editableGoal.value = Goal()
		isGoalSaved = false
		widgetPinSuccessful = false
	}

	/**
	 * As soon as the pin dialog is shown, check whether the pin was successful.
	 * */
	private fun checkIfPinWasSuccessful() {
		viewModelScope.launch {
			savedGoal.asFlow().collectLatest { savedGoal ->
				Log.d(TAG, "checkIfPinWasSuccessful")
				if (
					// Check if goal got saved through addWidgetToHomeScreenFilledIn and thus the uid in editableGoal was set
					isGoalSaved &&
					// Check if the current goal from the main activity has the same uid as editableGoal
					savedGoal.uid == editableGoal.value!!.uid &&
					// Check if it has a valid appWidgetId
					savedGoal.appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID
				) {
					// As soon as the goal has a valid appWidgetId, the pin was successful.
					widgetPinSuccessful = true
					// Also update the appWidgetId in the editableGoal (currently not needed, but maybe I will enable updates from the chat later.
					editableGoal.value = savedGoal
				}
			}
		}
	}

	/**
	 * Needs to be initialized by activity in order that lambdas work.
	 * * */
	private var lambdas: Lambdas = Lambdas()
	fun insertLambdas(lambdas: Lambdas) {
		this.lambdas = lambdas.copy(
			addWidgetToHomeScreenFilledIn = {
				// Save the widget to the DB
				// Get the generated uid in return
				// Open the pin dialog
				val uid = lambdas.addWidgetToHomeScreen(editableGoal.value!!, true)
				Log.d(TAG, "uid: $uid")
				// Save the generated uid to editableGoal
				editableGoal.value = editableGoal.value!!.copy(uid = uid)
				// Flag to checkIfPinWasSuccessful that the goal was saved and the generated uid saved to editableGoal
				isGoalSaved = true
				// Start to listen whether the pin was successful and update widgetPinSuccessful accordingly
				checkIfPinWasSuccessful()
			}
		)
	}

	/**
	 * All message templates.
	 * */
	inner class MessageTemplates {
		private val proposalsNext: List<MessageProposal> = listOf(
			messageProposalOf(
				proposalText = "Next",
				insertMessage = ::proposalNextByUser,
			)
		)

		fun introMessage(): ChatMessage = ChatMessage(
			text = "Hey! Super awesome that you downloaded WorkoutPixel.",
			autoAdvance = true,
			nextMessage = ::basicFeatures
		)

		private fun basicFeatures(): ChatMessage = ChatMessage(
			text = "With WorkoutPixel you can add widgets for your goals to your homescreen. They look like this:",
			nextMessage = ::habits1,
			messageExtra = { GoalPreviewsWithBackground() },
			proposals = proposalsNext
		)

		private fun proposalNextByUser(): ChatMessage = ChatMessage(
			text = "Next",
			isMessageByUser = true,
		)

		private fun habits1(): ChatMessage = ChatMessage(
			text = "You probably look at your homescreen dozens of time per day. The widget will remind you to work on your goal when it’s blue.",
			nextMessage = ::habits2,
			autoAdvance = true,
		)

		private fun habits2(): ChatMessage = ChatMessage(
			text = "But more importantly, you start to notice that your goals are green/done. Usually, we have a negative association with our goals because we always notice them when we are behind them.",
			nextMessage = ::habits3,
			autoAdvance = true,
		)

		private fun habits3(): ChatMessage = ChatMessage(
			text = "Building new habits is hard. Seeing your progress - many times a day - makes it a little easier.",
			nextMessage = ::createGoal,
			proposals = proposalsNext,
		)

		private fun createGoal(): ChatMessage = ChatMessage(
			text = "Let’s create your first goal.",
			nextMessage = ::setTitle,
			autoAdvance = true,
		)

/*		// TODO: Remove
		private fun previewInstructions(): ChatMessage = ChatMessage(
			text = "You can see in the preview below how the widget will look like.",
			nextMessage = ::setTitle,
			autoAdvance = true,
		)*/

		private fun chatInputField(): ChatInputField = chatInputFieldOf(
			value = goalTitle,
			onValueChange = { editableGoal.value = editableGoal.value!!.copy(title = it) },
			insertMessage = ::titleByUser,
		)

		private fun setTitle(): ChatMessage = ChatMessage(
			text = "Please describe your goal in 1-2 words.",
			nextMessage = ::goalPreview,
			chatInputField = chatInputField(),
		)

		private fun goalPreviewWithBackground(): @Composable () -> Unit = {
			GoalPreviewWithBackground(
				goal = editableGoal.value!!.copy(statusOverride = Status.GREEN))
		}

		private fun goalPreview(): ChatMessage = ChatMessage(
			text = "Your goal will look like this:",
			autoAdvance = true,
			nextMessage = ::setInterval,
			messageExtra = goalPreviewWithBackground(),
		)

		private fun proposalEditGoalDescription(): ChatMessage = ChatMessage(
			text = "Edit goal description",
			nextMessage = ::editTitle,
			isMessageByUser = true
		)

		private fun editTitle(): ChatMessage = ChatMessage(
			text = "Enter a new goal description",
			nextMessage = ::editTitleConfirm,
			chatInputField = chatInputField(),
		)

		private fun editTitleConfirm(): ChatMessage = ChatMessage(
			text = "Your goal now looks like this:",
			nextMessage = ::addToHomeScreen,
			autoAdvance = true,
			messageExtra = goalPreviewWithBackground(),
		)

		private fun titleByUser(): ChatMessage = ChatMessage(
			text = editableGoal.value!!.title,
			isMessageByUser = true,
		)

		private fun setInterval(): ChatMessage = ChatMessage(
			text = "How often do you want to reach your goal? Every ... days:",
			nextMessage = ::addToHomeScreen,
			proposals = intervalProposals()
		)

		// Could be val
		private fun intervalProposals(): List<MessageProposal> {
			val proposals: MutableList<MessageProposal> = mutableListOf()
			for (i in 1..31) {
				proposals.add(
					messageProposalOf(
						proposalAction = { editableGoal.value!!.intervalBlue = i },
						proposalText = i.toString(),
						insertMessage = ::intervalByUser,
					)
				)
			}
			return proposals
		}

		private fun intervalByUser(): ChatMessage = ChatMessage(
			text = editableGoal.value!!.intervalBlue.toString(),
			isMessageByUser = true
		)

		private fun addToHomeScreen(): ChatMessage = ChatMessage(
			text = "Let's now add a widget for this goal to your homescreen. After clicking \uD83D\uDC4D, your phone will either automatically add the widget or ask you to place it.",
			proposals = listOf(
				messageProposalOf(
					proposalText = "Edit goal description",
					insertMessage = ::proposalEditGoalDescription,
				),
				messageProposalOf(
					proposalText = "\uD83D\uDC4D",
					insertMessage = ::proposalThumbsUp,
				)
			),
		)

		private fun proposalThumbsUp(): ChatMessage = ChatMessage(
			text = "\uD83D\uDC4D",
			isMessageByUser = true,
			action = {
				viewModelScope.launch {
					lambdas.addWidgetToHomeScreenFilledIn()
				}
				delayAndSendMessage(::success, ::waitingForCallback)()
			}
		)

		private fun waitingForCallback(): ChatMessage = ChatMessage(
			text = "Waiting until the widget gets added...",
			action = delayAndSendMessage(::success, ::retryPrompt)
		)

		private fun delayAndSendMessage2(successMessage: MessageBuilder, noSuccessMessage: MessageBuilder?, totalDelay: Long = 3000): () -> Unit = {
			viewModelScope.launch {
				Log.d(TAG, "delayAndSendMessage2")
				val fail = collectLatestGoalAndInsertMessage(successMessage = successMessage)
				if (fail) noSuccessMessage?.let { insertMessageBuilderToQueueAtNextPositionAndAdvance(it) }
			}
		}

		private suspend fun collectLatestGoalAndInsertMessage(successMessage: MessageBuilder): Boolean {
			var scxx = false
			savedGoal.asFlow().collectLatest {
				if (it.appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
					scxx = true
					insertMessageBuilderToQueueAtNextPositionAndAdvance(successMessage)
				}
			}
			delay(3000)
			return scxx
		}

		private fun delayAndSendMessage(successMessage: MessageBuilder, noSuccessMessage: MessageBuilder?, totalDelay: Long = 3000): () -> Unit = {
			viewModelScope.launch {
				Log.d(TAG, "delayAndSendMessage: widgetPinSuccessful: $widgetPinSuccessful")
				delayUntilWidgetPinSuccessful(totalDelay = totalDelay)
				Log.d(TAG, "delayAndSendMessage: widgetPinSuccessful: $widgetPinSuccessful")
				if (widgetPinSuccessful) insertMessageBuilderToQueueAtNextPositionAndAdvance(successMessage)
				else noSuccessMessage?.let { insertMessageBuilderToQueueAtNextPositionAndAdvance(it) }
			}
		}

		private suspend fun delayUntilWidgetPinSuccessful(
			totalDelay: Long = 3000,
		) {
			var i = 0
			val delaySteps = 12
			while (!widgetPinSuccessful && i < delaySteps) {
				delay(totalDelay / delaySteps)
				i++
			}
		}

		private fun retryPrompt(): ChatMessage = ChatMessage(
			text = "Do you want to retry?",
			proposals = listOf(
				messageProposalOf(
					proposalText = "Edit goal description",
					insertMessage = ::proposalEditGoalDescription,
				),
				messageProposalOf(
					proposalText = "\uD83D\uDC4D",
					insertMessage = ::proposalThumbsUp,
				)
			),
			action = delayAndSendMessage(::success, null)
		)

		private fun success(): ChatMessage = ChatMessage(
			text = "You successfully added your widget.",
			proposals = listOf(
				messageProposalOf(
					proposalText = "Edit",
					insertMessage = ::proposalEdit
				),
				messageProposalOf(
					proposalText = "Close",
					insertMessage = ::proposalClose
				),
				messageProposalOf(
					proposalText = "Add another goal",
					insertMessage = ::proposalAnotherGoal,
				),
			),
		)

		private fun proposalClose(): ChatMessage = ChatMessage(
			text = "Close",
			isMessageByUser = true,
			action = { }
		)

		private fun proposalEdit(): ChatMessage = ChatMessage(
			text = "Edit",
			isMessageByUser = true,
			action = { }
		)

		private fun proposalAnotherGoal(): ChatMessage = ChatMessage(
			text = "Add another goal",
			isMessageByUser = true,
			nextMessage = ::setTitle,
			action = { newGoal() },
		)

	}
}
