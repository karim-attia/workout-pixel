package ch.karimattia.workoutpixel.onboarding

import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.karimattia.workoutpixel.composables.GoalPreview
import ch.karimattia.workoutpixel.core.testGoals
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData

val allMessages: List<MessageOld> = listOf(
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
	editGoalDescription(),
	editTitle(),
	editTitleConfirm(),
	editTitleConfirmBasic(),
	titleByUser(),
	setInterval(),
	intervalByUser(),
	addToHomeScreen(),
	thumbsUpProposal(),
	waitingForCallback(),
)

private val _goal: MutableLiveData<Goal> = MutableLiveData(Goal())
val goal: LiveData<Goal> = _goal

//////
fun introMessage(): MessageOld = MessageOld(text = "Hey! Super awesome that you downloaded WorkoutPixel.", nextMessage = basicFeatures())
fun basicFeatures(): MessageOld = MessageOld(text = "With WorkoutPixel you can add widgets for your goals to your homescreen. They look like this:",
	bottomArea = BottomArea.ShowNext, nextMessage = habits1(), messageExtra = { GoalPreview(goal = testGoals[0], settingsData = SettingsData()) })

fun nextByUser(): MessageOld = MessageOld(text = "Next", isMessageByUser = true)
fun habits1(): MessageOld =
	MessageOld(text = "You probably look at your homescreen dozens of time per day. The widget will remind you to work on your goal when it’s blue.",
		nextMessage = habits2())

fun habits2(): MessageOld =
	MessageOld(text = "But more importantly, you start to notice that your goals are green/done. Usually, we have a negative association with our goals because we always notice them when we are behind them.",
		nextMessage = habits3())

fun habits3(): MessageOld = MessageOld(text = "Building new habits is hard. Seeing your progress - many times a day - makes it a little easier.",
	bottomArea = BottomArea.ShowNext, nextMessage = createGoal())

fun createGoal(): MessageOld = MessageOld(text = "Let’s create your first goal.", nextMessage = previewInstructions())
fun previewInstructions(): MessageOld = MessageOld(text = "You can see in the preview below how the widget will look like.", nextMessage = setTitle())

fun setTitle(): MessageOld = MessageOld(text = "Please describe your goal in 1-2 words.", bottomArea = BottomArea.TitleInput, nextMessage = setInterval())
fun editGoalDescription(): MessageOld = MessageOld(text = "Edit goal description", nextMessage = editTitle(), isMessageByUser = true)
fun editTitle(): MessageOld = MessageOld(text = "Enter a new goal description", bottomArea = BottomArea.TitleInput, nextMessage = editTitleConfirmBasic())
fun editTitleConfirm(): MessageOld =
	MessageOld(text = "Alright, updated your goal description to: ${goal.value!!.title}", nextMessage = addToHomeScreen())

fun editTitleConfirmBasic(): MessageOld =
	MessageOld(text = "Alright, updated your goal description.", nextMessage = addToHomeScreen())

fun titleByUser(): MessageOld = MessageOld(text = goal.value!!.title, isMessageByUser = true)
fun setInterval(): MessageOld =
	MessageOld(text = "How often do you want to reach your goal? Every ... days:", bottomArea = BottomArea.IntervalInput, nextMessage = addToHomeScreen())

fun intervalByUser(): MessageOld = MessageOld(text = goal.value!!.intervalBlue.toString(), isMessageByUser = true)
fun addToHomeScreen(): MessageOld =
	MessageOld(text = "Let's now add a widget for this goal to your homescreen. After clicking \uD83D\uDC4D, your phone will either automatically add the widget or ask you to place it.",
		bottomArea = BottomArea.AddWidgetPrompt,
		proposal = "\uD83D\uDC4D")

fun thumbsUpProposal(): MessageOld =
	MessageOld(text = "\uD83D\uDC4D", isMessageByUser = true, bottomArea = BottomArea.AddWidget, autoAdvanceTime = 3000, nextMessage = waitingForCallback())

fun waitingForCallback(): MessageOld = MessageOld(text = "Waiting until the widget gets added...")


data class MessageOld(
	val text: String = "",
	val isMessageByUser: Boolean = false,
	val bottomArea: BottomArea = BottomArea.AutoAdvance,
	val autoAdvance: Boolean = bottomArea == BottomArea.AutoAdvance || bottomArea == BottomArea.AddWidget,
	// TODO: Set reasonable values
	val autoAdvanceTime: Int = 60,
	val showNextProposal: Boolean = bottomArea == BottomArea.ShowNext,
	val proposal: String = if (bottomArea == BottomArea.ShowNext) "Next" else "",
	val nextMessage: MessageOld? = null,
	val nextMessages: List<MessageOld> = if (nextMessage != null) listOf(nextMessage) else emptyList(),
	// TODO: Show below
	val messageExtra: @Composable () -> Unit = {},
) {
	fun debugString(): String = "text: $text, autoAdvance: $autoAdvance"
	// val name: String = object{}.javaClass.enclosingMethod.name
}

class AllMessageBuilders {
	fun builder(message: Message): MessageBuilder = { message }

	fun introMessage(): MessageBuilder =
		builder(Message(text = "Hey! Super awesome that you downloaded WorkoutPixel.", nextMessage = basicFeatures()))

	fun basicFeatures(): MessageBuilder =
		builder(Message(text = "With WorkoutPixel you can add widgets for your goals to your homescreen. They look like this:",
			bottomArea = BottomArea.ShowNext, nextMessage = habits1(), messageExtra = { GoalPreview(goal = testGoals[0], settingsData = SettingsData()) }))

	fun nextByUser(): MessageBuilder = builder(Message(text = "Next", isMessageByUser = true))
	fun habits1(): MessageBuilder =
		builder(Message(text = "You probably look at your homescreen dozens of time per day. The widget will remind you to work on your goal when it’s blue.",
			nextMessage = habits2()))

	fun habits2(): MessageBuilder =
		builder(Message(text = "But more importantly, you start to notice that your goals are green/done. Usually, we have a negative association with our goals because we always notice them when we are behind them.",
			nextMessage = habits3()))

	fun habits3(): MessageBuilder =
		builder(Message(text = "Building new habits is hard. Seeing your progress - many times a day - makes it a little easier.",
			bottomArea = BottomArea.ShowNext, nextMessage = createGoal()))

	fun createGoal(): MessageBuilder = builder(Message(text = "Let’s create your first goal.", nextMessage = previewInstructions()))
	fun previewInstructions(): MessageBuilder =
		builder(Message(text = "You can see in the preview below how the widget will look like.", nextMessage = setTitle()))

	fun setTitle(): MessageBuilder =
		builder(Message(text = "Please describe your goal in 1-2 words.", bottomArea = BottomArea.TitleInput, nextMessage = setInterval()))

	fun editGoalDescription(): MessageBuilder = builder(Message(text = "Edit goal description", nextMessage = editTitle(), isMessageByUser = true))
	fun editTitle(): MessageBuilder =
		builder(Message(text = "Enter a new goal description", bottomArea = BottomArea.TitleInput, nextMessage = editTitleConfirm()))

	fun editTitleConfirm(): MessageBuilder =
		builder(Message(text = "Alright, updated your goal description to: ${goal.value!!.title}", nextMessage = addToHomeScreen()))

	fun editTitleConfirmBasic(): MessageBuilder =
		builder(Message(text = "Alright, updated your goal description.", nextMessage = addToHomeScreen()))

	fun titleByUser(): MessageBuilder = builder(Message(text = goal.value!!.title, isMessageByUser = true))
	fun setInterval(): MessageBuilder =
		builder(Message(text = "How often do you want to reach your goal? Every ... days:",
			bottomArea = BottomArea.IntervalInput,
			nextMessage = addToHomeScreen()))

	fun intervalByUser(): MessageBuilder = builder(Message(text = goal.value!!.intervalBlue.toString(), isMessageByUser = true))
	fun addToHomeScreen(): MessageBuilder =
		builder(Message(text = "Let's now add a widget for this goal to your homescreen. After clicking \uD83D\uDC4D, your phone will either automatically add the widget or ask you to place it.",
			bottomArea = BottomArea.AddWidgetPrompt,
			proposal = "\uD83D\uDC4D"))

	fun thumbsUpProposal(): MessageBuilder =
		builder(Message(text = "\uD83D\uDC4D",
			isMessageByUser = true,
			bottomArea = BottomArea.AddWidget,
			autoAdvanceTime = 3000,
			nextMessage = waitingForCallback()))

	fun waitingForCallback(): MessageBuilder = builder(Message(text = "Waiting until the widget gets added..."))
}