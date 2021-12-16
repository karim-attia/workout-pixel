package ch.karimattia.workoutpixel.screens.onboarding

import android.appwidget.AppWidgetManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.*
import ch.karimattia.workoutpixel.chat.ChatInputField
import ch.karimattia.workoutpixel.chat.ChatMessage
import ch.karimattia.workoutpixel.chat.ChatViewModel
import ch.karimattia.workoutpixel.chat.MessageProposal
import ch.karimattia.workoutpixel.screens.GoalPreviewWithBackground
import ch.karimattia.workoutpixel.screens.GoalPreviewsWithBackground
import ch.karimattia.workoutpixel.screens.Lambdas
import ch.karimattia.workoutpixel.core.Status
import ch.karimattia.workoutpixel.core.Screens
import ch.karimattia.workoutpixel.data.Goal
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

@Suppress("unused")
private const val TAG: String = "OnboardingViewModel"
@OptIn(ExperimentalComposeUiApi::class)
typealias MessageBuilder = () -> ChatMessage

enum class Chatvariant {
	Onboarding, NewGoalOnly
}

@ExperimentalComposeUiApi
class OnboardingViewModelFactory(private val chatvariant: Chatvariant, private val scope: CoroutineScope, private var lambdas: Lambdas) :
	ViewModelProvider.NewInstanceFactory() {
	override fun <T : ViewModel> create(modelClass: Class<T>): T =
		OnboardingViewModel(chatvariant = chatvariant, scope = scope, receivedLambdas = lambdas) as T
}

@ExperimentalComposeUiApi
class OnboardingViewModel(chatvariant: Chatvariant, scope: CoroutineScope, private var receivedLambdas: Lambdas) : ChatViewModel() {

	private val editableGoal: MutableLiveData<Goal> = MutableLiveData(Goal())
	private var isGoalSaved: Boolean = false
	val savedGoal: MutableLiveData<Goal> = MutableLiveData(Goal())
	private val goalTitle: LiveData<String> = editableGoal.map { it.title }
	private var lambdas: Lambdas

	init {
		/**
		 * Initialize the ChatViewModel: Process the first message.
		 * */
		initialize(
			firstMessage = when (chatvariant) {
				Chatvariant.Onboarding -> ::introMessage
				Chatvariant.NewGoalOnly -> ::setTitle
			},
			scope = scope
		)

		lambdas = receivedLambdas.copy(
			addWidgetToHomeScreen = { goal: Goal, insertNewGoal: Boolean ->
				// Save the widget to the DB
				// Get the generated uid in return
				// Open the pin dialog
				val uid = receivedLambdas.addWidgetToHomeScreen(goal, insertNewGoal)
				// Save the generated uid to editableGoal
				editableGoal.value = editableGoal.value!!.copy(uid = uid)
				// Flag to checkIfPinWasSuccessful that the goal was saved and the generated uid saved to editableGoal
				isGoalSaved = true
				return@copy 0
			}
		)
	}

	/**
	 * Reset editableGoal and flags when a new goal is created.
	 * * */
	private fun newGoal() {
		editableGoal.value = Goal()
		isGoalSaved = false
	}


	/**
	 * All message templates.
	 * */
	private fun introMessage(): ChatMessage = ChatMessage(
		text = "Hey! Super awesome that you downloaded WorkoutPixel.",
		autoAdvance = true,
		nextMessage = ::basicFeatures
	)

	private fun basicFeatures(): ChatMessage = ChatMessage(
		text = "With WorkoutPixel you can add widgets for your goals to your homescreen. They look like this:",
		nextMessage = ::habits1,
		messageExtra = { GoalPreviewsWithBackground() },
		messageProposals = proposalsNext
	)

	private fun proposalNextByUser(): ChatMessage = ChatMessage(
		text = "Next",
		isMessageByUser = true,
	)

	private fun habits1(): ChatMessage = ChatMessage(
		text = "You probably look at your homescreen dozens of times per day. The widget will remind you to work on your goal when it’s blue.",
		nextMessage = ::habits2,
		autoAdvance = true,
	)

	private fun habits2(): ChatMessage = ChatMessage(
		text = "But more importantly, you will start to notice that your goals are green/done. Usually, we have a negative association with our goals because we always notice them when we are falling behind.",
		nextMessage = ::habits3,
		autoAdvance = true,
	)

	private fun habits3(): ChatMessage = ChatMessage(
		text = "Building new habits is hard. Seeing your progress - many times a day - makes it a little easier.",
		nextMessage = ::createGoal,
		messageProposals = proposalsNext,
	)

	private fun createGoal(): ChatMessage = ChatMessage(
		text = "Let’s create your first goal.",
		nextMessage = ::setTitle,
		autoAdvance = true,
	)

	private fun setTitle(): ChatMessage = ChatMessage(
		text = "Please describe your goal in 1-2 words.",
		nextMessage = ::goalPreview,
		chatInputField = chatInputField(),
	)

	private fun titleByUser(): ChatMessage = ChatMessage(
		text = editableGoal.value!!.title,
		isMessageByUser = true,
	)

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

	private fun setInterval(): ChatMessage = ChatMessage(
		text = "How often do you want to reach your goal? Every ... days:",
		nextMessage = ::addToHomeScreen,
		messageProposals = intervalProposals()
	)


	private fun intervalByUser(): ChatMessage = ChatMessage(
		text = editableGoal.value!!.intervalBlue.toString(),
		isMessageByUser = true
	)

	private fun addToHomeScreen(): ChatMessage = ChatMessage(
		text = "Let's now add the widget for this goal to your homescreen. After clicking \uD83D\uDC4D, your phone will either automatically add the widget or ask you to place it.",
		messageProposals = listOf(
			messageProposalOf(
				proposalText = "Edit goal description",
				insertsMessage = ::proposalEditGoalDescription,
			),
			messageProposalOf(
				proposalText = "\uD83D\uDC4D",
				insertsMessage = ::proposalThumbsUp,
			)
		),
	)

	private fun proposalThumbsUp(): ChatMessage = ChatMessage(
		text = "\uD83D\uDC4D",
		isMessageByUser = true,
		action = checkIfPinWasSuccessfulAndSendAccordingMessage(showPinDialog = true, successMessage = ::success, noSuccessMessage = ::waitingForCallback)
	)

	private fun waitingForCallback(): ChatMessage = ChatMessage(
		text = "Waiting until the widget gets added...",
		action = checkIfPinWasSuccessfulAndSendAccordingMessage(successMessage = ::success, noSuccessMessage = ::retryPrompt)
	)

	private fun retryPrompt(): ChatMessage = ChatMessage(
		text = "Do you want to retry?",
		messageProposals = listOf(
			messageProposalOf(
				proposalText = "Edit goal description",
				insertsMessage = ::proposalEditGoalDescription,
			),
			messageProposalOf(
				proposalText = "\uD83D\uDC4D",
				insertsMessage = ::proposalThumbsUp,
			)
		),
		action = checkIfPinWasSuccessfulAndSendAccordingMessage(successMessage = ::success, noSuccessMessage = null, timeout = null)
	)

	private fun success(): ChatMessage = ChatMessage(
		text = "You successfully added your widget.",
		messageProposals = listOf(
/*
				messageProposalOf(
					proposalText = "Edit",
					insertsMessage = ::proposalEdit
				),
*/
			messageProposalOf(
				proposalText = "Close chat",
				insertsMessage = ::proposalClose
			),
			messageProposalOf(
				proposalText = "Add another goal",
				insertsMessage = ::proposalAnotherGoal,
			),
		),
	)

	private fun proposalClose(): ChatMessage = ChatMessage(
		text = "Close chat",
		isMessageByUser = true,
		action = { lambdas.navigateTo(Screens.GoalsList.name, null, true) }
	)

/*
	private fun proposalEdit(): ChatMessage = ChatMessage(
		text = "Edit",
		isMessageByUser = true,
		action = { }
	)
*/

	private fun proposalAnotherGoal(): ChatMessage = ChatMessage(
		text = "Add another goal",
		isMessageByUser = true,
		nextMessage = ::setTitle,
		action = { newGoal() },
	)

	/**
	 * Message template helpers.
	 * */
	private fun goalPreviewWithBackground(): @Composable () -> Unit = {
		GoalPreviewWithBackground(
			goal = editableGoal.value!!.copy(statusOverride = Status.GREEN))
	}

	private val proposalsNext: List<MessageProposal> = listOf(
		messageProposalOf(
			proposalText = "Next",
			insertsMessage = ::proposalNextByUser,
		)
	)

	private fun chatInputField(): ChatInputField = chatInputFieldOf(
		value = goalTitle,
		onValueChange = { editableGoal.value = editableGoal.value!!.copy(title = it) },
		insertMessage = ::titleByUser,
		placeholder = "E.g. Push ups",
	)

	// Could be val
	private fun intervalProposals(): List<MessageProposal> {
		val proposals: MutableList<MessageProposal> = mutableListOf()
		for (i in 1..31) {
			proposals.add(
				messageProposalOf(
					proposalAction = { editableGoal.value!!.intervalBlue = i },
					proposalText = i.toString(),
					insertsMessage = ::intervalByUser,
				)
			)
		}
		return proposals
	}

	private val checkIfPinWasSuccessfulJobs = mutableListOf<Job>()
	private fun checkIfPinWasSuccessfulAndSendAccordingMessage(
		showPinDialog: Boolean = false,
		successMessage: MessageBuilder,
		noSuccessMessage: MessageBuilder?,
		timeout: Long? = 3000,
	): () -> Unit = {
		viewModelScope.launch {
			if (showPinDialog) lambdas.addWidgetToHomeScreen(editableGoal.value!!, true)
			var success = false
			// Cancel job if retry is clicked.
			for (job in checkIfPinWasSuccessfulJobs) job.cancel()

			val job: Job = launch {
				savedGoal.asFlow().collectLatest { savedGoal ->
					if (
					// Check if goal got saved through addWidgetToHomeScreenFilledIn and thus the uid in editableGoal was set
						isGoalSaved &&
						// Check if the current goal from the main activity has the same uid as editableGoal
						savedGoal.uid == editableGoal.value!!.uid &&
						// Check if it has a valid appWidgetId
						savedGoal.appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID
					) {
						success = true
						insertMessageBuilderToQueueAtNextPositionAndAdvance(successMessage)
						editableGoal.value = savedGoal
						// Observing the savedGoal is no longer needed.
						this.cancel()
					}
				}
			}
			// After a certain amount of time without pinning success, cancel the job and move to the next message.
			// There should not be a timeout on the retry screen - if the widget is added 10min later, the success message should still be shown.
			if (timeout != null) {
				delay(timeout)
				if (!success) noSuccessMessage?.let { insertMessageBuilderToQueueAtNextPositionAndAdvance(it) }
				// The next message will observe the savedGoal.
				job.cancel()
			} else {
				// Add still running job to array of still running jobs, so they can get cancelled before the next job starts.
				checkIfPinWasSuccessfulJobs.add(job)
			}
		}
	}
}
