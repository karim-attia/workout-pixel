package ch.karimattia.workoutpixel.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.karimattia.workoutpixel.data.SettingsData

private const val TAG: String = "Onboarding"

@Composable
fun Onboarding(
	settingsData: SettingsData,
	onboardingViewModel: OnboardingViewModel = viewModel(),
) {
	MessageList(messages)
}

data class Message(
	var isMine: Boolean = false,
	var text: String,
	// var username: String = "Workout Pixel"
)

val messages: List<Message> = listOf(
	Message(
		text = "Hey! Super awesome that you downloaded WorkoutPixel.",
	),
	Message(
		text = "With WorkoutPixel you can add widgets for your goals to your homescreen. They look like this:",
	),
)

@Composable
fun MessageList(messageList: List<Message>) { // 1$
	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		// reverseLayout = true, // 5
	) {
		items(messageList) { message ->
			MessageCard(message) // 6
		}
	}
}

@Composable
fun MessageCard(message: Message) { // 1
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 8.dp, vertical = 4.dp),
		horizontalAlignment = when { // 2
			message.isMine -> Alignment.End
			else -> Alignment.Start
		},
	) {
		Card(
			modifier = Modifier.widthIn(max = 340.dp),
			shape = cardShapeFor(message), // 3
			backgroundColor = when {
				message.isMine -> MaterialTheme.colors.primary
				else -> MaterialTheme.colors.secondary
			},
		) {
			Text(
				modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
				text = message.text,
				color = when {
					message.isMine -> MaterialTheme.colors.onPrimary
					else -> MaterialTheme.colors.onSecondary
				},
			)
		}
/*
    Text( // 4
      text = message.username,
      fontSize = 12.sp,
    )
*/
	}
}

@Composable
fun cardShapeFor(message: Message): Shape {
	val roundedCorners = RoundedCornerShape(16.dp)
	return when {
		message.isMine -> roundedCorners.copy(bottomEnd = CornerSize(0))
		else -> roundedCorners.copy(bottomStart = CornerSize(0))
	}
}


@Preview(name = "Onboarding preview", showBackground = true)
@Composable
fun OnboardingPreview() {
	Onboarding(
		settingsData = SettingsData(),
	)
}