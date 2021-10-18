package ch.karimattia.workoutpixel.onboarding

import android.appwidget.AppWidgetManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import ch.karimattia.workoutpixel.composables.GoalPreviewWithBackground
import ch.karimattia.workoutpixel.composables.GoalPreviewsWithBackground
import ch.karimattia.workoutpixel.composables.Lambdas
import ch.karimattia.workoutpixel.core.Status
import ch.karimattia.workoutpixel.data.Goal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("unused")
private const val TAG: String = "OnboardingViewModel"
typealias MessageBuilder = () -> Message

/*@HiltViewModel
class OnboardingViewModel @Inject constructor(
	private val settingsRepository: SettingsRepository,
) : ChatViewModel() {*/

class OnboardingViewModel : ChatViewModel() {
	/**
	 * Class with all message templates
	 * */
	val messageTemplates: MessageTemplates = MessageTemplates()

	/**
	 * Specifies the first message of the message chain
	 * */
	override var firstMessage: Message = messageTemplates.introMessage()

	init {
		initialize()
	}

	private val _goal: MutableLiveData<Goal> = MutableLiveData(Goal())
	val goal: LiveData<Goal> = _goal
	val goalTitle: LiveData<String> = _goal.map { it.title }
	fun updateGoal(goal: Goal) {
		_goal.value = goal
	}

	/**
	 * Needs to be initialized by activity in order that automatic scrolling works.
	 * * */
	var lambdas: Lambdas = Lambdas()

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

		fun introMessage(): Message = Message(
			text = "Hey! Super awesome that you downloaded WorkoutPixel.",
			nextMessage = ::basicFeatures
		)

		private fun basicFeatures(): Message = Message(
			text = "With WorkoutPixel you can add widgets for your goals to your homescreen. They look like this:",
			autoAdvance = true,
			nextMessage = ::habits1,
			messageExtra = { GoalPreviewsWithBackground() },
			proposals = proposalsNext
		)

		private fun proposalNextByUser(): Message = Message(
			text = "Next",
			isMessageByUser = true
		)

		private fun habits1(): Message = Message(
			text = "You probably look at your homescreen dozens of time per day. The widget will remind you to work on your goal when it’s blue.",
			nextMessage = ::habits2
		)

		private fun habits2(): Message = Message(
			text = "But more importantly, you start to notice that your goals are green/done. Usually, we have a negative association with our goals because we always notice them when we are behind them.",
			nextMessage = ::habits3
		)

		private fun habits3(): Message = Message(
			text = "Building new habits is hard. Seeing your progress - many times a day - makes it a little easier.",
			autoAdvance = true,
			nextMessage = ::createGoal,
			proposals = proposalsNext,
		)

		private fun createGoal(): Message = Message(
			text = "Let’s create your first goal.",
			nextMessage = ::previewInstructions
		)

		private fun previewInstructions(): Message = Message(
			text = "You can see in the preview below how the widget will look like.",
			nextMessage = ::setTitle
		)

		private fun chatInputField(): ChatInputField = chatInputFieldOf(
			value = goalTitle,
			onValueChange = { _goal.value = _goal.value!!.copy(title = it) },
			insertMessage = ::titleByUser,
		)

		private fun setTitle(): Message = Message(
			text = "Please describe your goal in 1-2 words.",
			nextMessage = ::goalPreview,
			chatInputField = chatInputField(),
		)

		private fun goalPreviewWithBackground(): @Composable () -> Unit = {
			GoalPreviewWithBackground(
				goal = goal.value!!.copy(statusOverride = Status.GREEN))
		}

		private fun goalPreview(): Message = Message(
			text = "Your goal will look like this:",
			autoAdvance = true,
			nextMessage = ::setInterval,
			messageExtra = goalPreviewWithBackground(),
		)

		private fun proposalEditGoalDescription(): Message = Message(
			text = "Edit goal description",
			nextMessage = ::editTitle,
			isMessageByUser = true
		)

		private fun editTitle(): Message = Message(
			text = "Enter a new goal description",
			nextMessage = ::editTitleConfirm,
			chatInputField = chatInputField(),
		)

		private fun editTitleConfirm(): Message = Message(
			text = "Your goal now looks like this:",
			nextMessage = ::addToHomeScreen,
			messageExtra = goalPreviewWithBackground(),
		)

		private fun titleByUser(): Message = Message(
			text = goal.value!!.title,
			isMessageByUser = true,
		)

		private fun setInterval(): Message = Message(
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
						proposalAction = { _goal.value!!.intervalBlue = i },
						proposalText = i.toString(),
						insertMessage = ::intervalByUser,
					)
				)
			}
			return proposals
		}

		private fun intervalByUser(): Message = Message(
			text = goal.value!!.intervalBlue.toString(),
			isMessageByUser = true
		)

		private fun addToHomeScreen(): Message = Message(
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

		private fun proposalThumbsUp(): Message = Message(
			text = "\uD83D\uDC4D",
			isMessageByUser = true,
			action = {
				viewModelScope.launch {
					Log.d(TAG, "proposalThumbsUp")
					lambdas.addWidgetToHomeScreenFilledIn()
					delay(3000)
					Log.d(TAG, "firstdelay")
					Log.d(TAG, "newGoal.appWidgetId: ${_goal.value!!.appWidgetId}")
					Log.d(TAG, "newGoal.appWidgetId true: ${_goal.value!!.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID}")
					if (_goal.value!!.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) insertMessageBuilderToQueueAtNextPositionAndAdvance(::waitingForCallback)
				}
			}
		)

		private fun waitingForCallback(): Message = Message(
			text = "Waiting until the widget gets added...",
			action = {
				viewModelScope.launch {

					delay(3000)
					Log.d(TAG, "seconddelay")
					// TODO: check and add messageproposal thumbs up
					if (_goal.value!!.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) insertMessageBuilderToQueueAtNextPositionAndAdvance(::retryPrompt)
					else {
						insertMessageBuilderToQueueAtNextPositionAndAdvance(::success)
					}
					Log.d(TAG, "endblock")
				}
			}
		)

		private fun retryPrompt(): Message = Message(
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
		)

		private fun success(): Message = Message(
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
			),
		)

		private fun proposalClose(): Message = Message(
			text = "Close",
			isMessageByUser = true,
			action = { }
		)

		private fun proposalEdit(): Message = Message(
			text = "Edit",
			isMessageByUser = true,
			action = { }
		)

	}
}
