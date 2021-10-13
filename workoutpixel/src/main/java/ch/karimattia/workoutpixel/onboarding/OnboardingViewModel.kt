package ch.karimattia.workoutpixel.onboarding

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.karimattia.workoutpixel.data.Goal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG: String = "OnboardingViewModel"

@HiltViewModel
class OnboardingViewModel @Inject constructor(
	//private val goalRepository: GoalRepository,
) : ViewModel() {
	var currentStep: Int = 0
	private val firstMessage = introMessage()
	var latestMessage: MutableLiveData<Message> = MutableLiveData(firstMessage)

	fun increaseCurrentStep() {
		Log.d(TAG, "increaseCurrentStep currentStep: ${currentStep}")
		currentStep++
		Log.d(TAG, "increaseCurrentStep currentStep: ${currentStep}")
		Log.d(TAG, "increaseCurrentStep: ${latestMessage.value!!.debugString()}")
		latestMessage.value = if (currentStep < messages.size) messages[currentStep] else messages[messages.size - 1]
		Log.d(TAG, "increaseCurrentStep: ${latestMessage.value!!.debugString()}")
		Log.d(TAG, "increaseCurrentStep: messages.size: ${messages.size}")
		latestMessage.value!!.nextMessage?.let { messages.add(element = it) }
		Log.d(TAG, "increaseCurrentStep: messages.size: ${messages.size}")
		checkAutoAdvance()
	}

	private fun checkAutoAdvance() {
		Log.d(TAG, "checkAutoAdvance: ${latestMessage.value!!.debugString()}")
		if (latestMessage.value!!.autoAdvance && currentStep < messages.size - 1) {
			viewModelScope.launch {
				delay(latestMessage.value!!.autoAdvanceTime.toLong())
				increaseCurrentStep()
			}
		}
	}

	val messages: SnapshotStateList<Message> = mutableStateListOf(firstMessage)

	init {
		Log.d(TAG, "init: messages.size: ${messages.size}")
		latestMessage.value!!.nextMessage?.let { messages.add(element = it) }
		Log.d(TAG, "init: messages.size: ${messages.size}")
		checkAutoAdvance()
	}

	fun insertMessageAtNextPosition(message: Message) {
		messages.add(index = currentStep + 1, element = message)
		// messages.add(element = message)
		increaseCurrentStep()
	}

	private val _goal: MutableLiveData<Goal> = MutableLiveData(Goal())
	val goal: LiveData<Goal> = _goal
	fun updateGoal(goal: Goal) {
		_goal.value = goal
	}
}

enum class BottomArea {
	AutoAdvance, ShowNext, AddWidgetPrompt, TitleInput, IntervalInput, AddWidget
}

data class Message(
	val text: String = "",
	val isMessageByUser: Boolean = false,
	val bottomArea: BottomArea = BottomArea.AutoAdvance,
	val autoAdvance: Boolean = bottomArea == BottomArea.AutoAdvance || bottomArea == BottomArea.AddWidget,
	// TODO: Set reasonable values
	val autoAdvanceTime: Int = 60,
	val showNextProposal: Boolean = bottomArea == BottomArea.ShowNext,
	val proposal: String = if (bottomArea == BottomArea.ShowNext) "Next" else "",
	val nextMessage: Message? = null,
	val nextMessages: List<Message> = if (nextMessage != null) listOf(nextMessage) else emptyList(),
	// TODO: Show below
	val messageExtra: () -> Unit = {},
) {
	fun debugString(): String = "text: $text, autoAdvance: $autoAdvance"
}

fun introMessage(): Message = Message(text = "Hey! Super awesome that you downloaded WorkoutPixel.", nextMessage = basicFeatures())
fun basicFeatures(): Message = Message(text = "With WorkoutPixel you can add widgets for your goals to your homescreen. They look like this:",
	bottomArea = BottomArea.ShowNext, nextMessage = habits1())

fun nextByUser(): Message = Message(text = "Next", isMessageByUser = true)
fun habits1(): Message =
	Message(text = "You probably look at your homescreen dozens of time per day. The widget will remind you to work on your goal when it’s blue.",
		nextMessage = habits2())

fun habits2(): Message =
	Message(text = "But more importantly, you start to notice that your goals are green/done. Usually, we have a negative association with our goals because we always notice them when we are behind them.",
		nextMessage = habits3())

fun habits3(): Message = Message(text = "Building new habits is hard. Seeing your progress - many times a day - makes it a little easier.",
	bottomArea = BottomArea.ShowNext, nextMessage = createGoal())

fun createGoal(): Message = Message(text = "Let’s create your first goal.", nextMessage = previewInstructions())
fun previewInstructions(): Message = Message(text = "You can see in the preview below how the widget will look like.", nextMessage = setTitle())

fun setTitle(): Message = Message(text = "Please describe your goal in 1-2 words.", bottomArea = BottomArea.TitleInput, nextMessage = setInterval())
fun editGoalDescription(): Message = Message(text = "Edit goal description", nextMessage = editTitle(), isMessageByUser = true)
fun editTitle(): Message = Message(text = "Enter a new goal description", bottomArea = BottomArea.TitleInput, nextMessage = editTitleConfirmBasic())
fun editTitleConfirm(title: String): Message =
	Message(text = "Alright, updated your goal description to: $title", nextMessage = addToHomeScreen())

fun editTitleConfirmBasic(): Message =
	Message(text = "Alright, updated your goal description.", nextMessage = addToHomeScreen())

fun titleByUser(title: String): Message = Message(text = title, isMessageByUser = true)
fun setInterval(): Message =
	Message(text = "How often do you want to reach your goal? Every ... days:", bottomArea = BottomArea.IntervalInput, nextMessage = addToHomeScreen())

fun intervalByUser(intervalBlue: Int): Message = Message(text = intervalBlue.toString(), isMessageByUser = true)
fun addToHomeScreen(): Message =
	Message(text = "Let's now add a widget for this goal to your homescreen. After clicking \uD83D\uDC4D, your phone will either automatically add the widget or ask you to place it.",
		bottomArea = BottomArea.AddWidgetPrompt,
		proposal = "\uD83D\uDC4D")

fun thumbsUpProposal(): Message =
	Message(text = "\uD83D\uDC4D", isMessageByUser = true, bottomArea = BottomArea.AddWidget, autoAdvanceTime = 3000, nextMessage = waitingForCallback())

fun waitingForCallback(): Message = Message(text = "Waiting until the widget gets added...")

fun test(): Message = Message(text = "Test. ")

val initialMessages: List<Message> = listOf(
	introMessage(),
	basicFeatures(),
	nextByUser(),
	habits1(),
	habits2(),
	habits3(),
	nextByUser(),
	createGoal(),
	previewInstructions(),
	setTitle(),
	setInterval(),
	addToHomeScreen(),
	thumbsUpProposal(),
	waitingForCallback(),
)

