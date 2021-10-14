package ch.karimattia.workoutpixel.onboarding

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.karimattia.workoutpixel.composables.GoalPreview
import ch.karimattia.workoutpixel.core.testGoals
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG: String = "OnboardingViewModel"
typealias MessageBuilder = () -> Message

class OnboardingViewModel : ChatViewModel() {

	/**
	 * Class with all message templates
	 * */
	val messageTemplates: MessageTemplates = MessageTemplates()

	/**
	 * Specifies the first message of the message chain
	 * */
	override var firstMessage: Message = messageTemplates.introMessage()

	init{
		initialize()
	}

	private val _goal: MutableLiveData<Goal> = MutableLiveData(Goal())
	val goal: LiveData<Goal> = _goal
	fun updateGoal(goal: Goal) {
		_goal.value = goal
	}

	// Could live in Onboarding.kt and also access goal data there.
	inner class MessageTemplates {
		fun introMessage(): Message =
			Message(text = "Hey! Super awesome that you downloaded WorkoutPixel.", nextMessage = ::basicFeatures)

		fun basicFeatures(): Message =
			Message(text = "With WorkoutPixel you can add widgets for your goals to your homescreen. They look like this:",
				bottomArea = BottomArea.ShowNext, nextMessage = ::habits1, messageExtra = { GoalPreview(goal = testGoals[0], settingsData = SettingsData()) })

		fun nextByUser(): Message = Message(text = "Next", isMessageByUser = true)
		fun habits1(): Message =
			Message(text = "You probably look at your homescreen dozens of time per day. The widget will remind you to work on your goal when it’s blue.",
				nextMessage = ::habits2)

		fun habits2(): Message =
			Message(text = "But more importantly, you start to notice that your goals are green/done. Usually, we have a negative association with our goals because we always notice them when we are behind them.",
				nextMessage = ::habits3)

		fun habits3(): Message =
			Message(text = "Building new habits is hard. Seeing your progress - many times a day - makes it a little easier.",
				bottomArea = BottomArea.ShowNext, nextMessage = ::createGoal)

		fun createGoal(): Message = Message(text = "Let’s create your first goal.", nextMessage = ::previewInstructions)
		fun previewInstructions(): Message =
			Message(text = "You can see in the preview below how the widget will look like.", nextMessage = ::setTitle)

		fun setTitle(): Message =
			Message(text = "Please describe your goal in 1-2 words.", bottomArea = BottomArea.TitleInput, nextMessage = ::setInterval)

		fun editGoalDescription(): Message = Message(text = "Edit goal description", nextMessage = ::editTitle, isMessageByUser = true)
		fun editTitle(): Message =
			Message(text = "Enter a new goal description", bottomArea = BottomArea.TitleInput, nextMessage = ::editTitleConfirm)

		fun editTitleConfirm(): Message =
			Message(text = "Alright, updated your goal description to: ${goal.value!!.title}", nextMessage = ::addToHomeScreen)

		fun editTitleConfirmBasic(): Message =
			Message(text = "Alright, updated your goal description.", nextMessage = ::addToHomeScreen)

		fun titleByUser(): Message = Message(text = goal.value!!.title, isMessageByUser = true)
		fun setInterval(): Message =
			Message(text = "How often do you want to reach your goal? Every ... days:",
				bottomArea = BottomArea.IntervalInput,
				nextMessage = ::addToHomeScreen)

		fun intervalByUser(): Message = Message(text = goal.value!!.intervalBlue.toString(), isMessageByUser = true)
		fun addToHomeScreen(): Message =
			Message(text = "Let's now add a widget for this goal to your homescreen. After clicking \uD83D\uDC4D, your phone will either automatically add the widget or ask you to place it.",
				bottomArea = BottomArea.AddWidgetPrompt,
				proposal = "\uD83D\uDC4D")

		fun thumbsUpProposal(): Message =
			Message(text = "\uD83D\uDC4D",
				isMessageByUser = true,
				bottomArea = BottomArea.AddWidget,
				autoAdvanceTime = 3000,
				nextMessage = ::waitingForCallback)

		fun waitingForCallback(): Message = Message(text = "Waiting until the widget gets added...")
	}

}
