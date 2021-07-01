package ch.karimattia.workoutpixel

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.CommonFunctions.getColorFromStatus
import ch.karimattia.workoutpixel.core.Goal
import ch.karimattia.workoutpixel.database.GoalViewModel
import ch.karimattia.workoutpixel.database.InteractWithPastWorkout
import ch.karimattia.workoutpixel.ui.theme.WorkoutPixelTheme
import java.util.*

class ComposeActivity : ComponentActivity() {
	private val TAG = "ComposeActivity"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val goalViewModel = ViewModelProvider(this).get(GoalViewModel::class.java)
		setContent {
			MyApp {
				// MyScreenContent(goals)
				val goals: List<Goal> by goalViewModel.allGoals.observeAsState(listOf())
				GoalListWithList(
					goals,
					updateAfterClick = {
						Toast.makeText(this, "It worked :)", Toast.LENGTH_SHORT).show()
						Log.d(TAG, "updateAfterClick")
					},
				)
			}
		}
	}
}

@Composable
fun GoalListWithList(
	goals: List<Goal>,
	updateAfterClick: () -> Unit,
) {
	// We save the scrolling position with this state that can also
	// be used to programmatically scroll the list
	val scrollState = rememberLazyListState()

	// We save the coroutine scope where our animated scroll will be executed
	val coroutineScope = rememberCoroutineScope()

	LazyColumn(
		state = scrollState,
		contentPadding = PaddingValues(top = 6.dp, bottom = 40.dp)
	) {
		items(
			items = goals,
		) { goal ->
			GoalCard(
				goal = goal,
				updateAfterClick = updateAfterClick
			)
		}
	}
}

/*
@Composable
fun GoalListWithViewModel(modifier: Modifier = Modifier, goalViewModel: GoalViewModel) {
	val goals: List<Goal> by goalViewModel.allGoals.observeAsState(listOf())

	// We save the scrolling position with this state that can also
	// be used to programmatically scroll the list
	val scrollState = rememberLazyListState()

	// We save the coroutine scope where our animated scroll will be executed
	val coroutineScope = rememberCoroutineScope()

	LazyColumn(modifier = modifier, state = scrollState) {
		items(items = goals) { goal ->
			GoalCard(goal = goal, 				updateAfterClick = updateAfterClick
			)
		}
	}
}
*/

@Composable
fun GoalCard(
	modifier: Modifier = Modifier,
	goal: Goal,
	updateAfterClick: () -> Unit,
) {
	Card(
		backgroundColor = Color.White,
		elevation = 4.dp,
		modifier = Modifier
			.padding(vertical = 4.dp, horizontal = 8.dp)
			.fillMaxWidth()
	) {
		// Preview and rest
		Row(
			modifier = Modifier.padding(all = 1.dp),
		) {
			GoalPreview(
				goal = goal, updateAfterClick = updateAfterClick
			)
			// Rest
			Column(modifier = Modifier.padding(start = 4.dp))
			{
				GoalTitle(goal)
				Row(modifier = Modifier.padding(top = 1.dp)) {
					IntervalIconAndText(goal)
					LastDoneIconAndText(goal)
				}
			}
		}
	}
}

@Composable
fun GoalPreview(
	goal: Goal, updateAfterClick: () -> Unit,
) {
	Text(
		text = goal.widgetText(),
		textAlign = TextAlign.Center,
		fontSize = 12.sp,
		color = Color.White,
		modifier = Modifier
			.padding(4.dp)
			.width(66.dp)
			.height(44.dp)
			.clip(shape = RoundedCornerShape(4.dp))
			.background(Color(getColorFromStatus(goal.status)))
			.wrapContentSize(Alignment.Center)
			.clickable { updateAfterClick() }
	)
}

@Composable
fun GoalTitle(goal: Goal) {
	Text(
		text = goal.title.uppercase(Locale.getDefault()),
		fontSize = 16.sp,
		fontWeight = FontWeight.Bold,
		modifier = Modifier
			.padding(top = 2.dp, start = 2.dp, end = 2.dp)
	)
}

@Composable
fun IntervalIconAndText(goal: Goal) {
	IconAndText(
		Icons.Filled.DateRange,
		20,
		goal.everyWording()
	)
}

@Composable
fun LastDoneIconAndText(goal: Goal) {
	IconAndText(
		Icons.Filled.Done,
		22,
		CommonFunctions.dateBeautiful(goal.lastWorkout)
	)
}

@Composable
fun IconAndText(icon: ImageVector, size: Int, text: String) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Icon(
			icon, contentDescription = null,
			Modifier
				.height(size.dp)
				.width(size.dp)
		)
		Text(
			text = text,
			fontSize = 14.sp,
			modifier = Modifier
				.defaultMinSize(100.dp)
				.padding(start = 5.dp)
		)
	}
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
	WorkoutPixelTheme {
		Surface(color = Color.White) {
			content()
		}
	}
}

@Composable
fun MyScreenContent(goals: List<Goal>) {
	val counterState = remember { mutableStateOf(0) }

	Column {
		TopAppBar(
			title = {
				Text(text = "Page title", maxLines = 2)
			}
		)

		Column(modifier = Modifier.padding(16.dp)) {

			Image(
				painter = painterResource(R.drawable.instructions_pitch),
				contentDescription = null,
				modifier = Modifier
					.height(180.dp)
					.fillMaxWidth()
					.clip(shape = RoundedCornerShape(4.dp)),
				contentScale = ContentScale.Crop

			)
			Spacer(Modifier.height(16.dp))

			Text(
				text = "A day wandering through the sandhills " +
						"in Shark Fin Cove, and a few of the " +
						"sights I saw",
				style = typography.h6,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis
			)
			Text(
				"Davenport, California",
				style = typography.body2
			)
			Text(
				"December 2018",
				style = typography.body2
			)

			Spacer(Modifier.height(16.dp))
			ItemList(Modifier.weight(1f), goals)
			Counter(
				count = counterState.value,
				updateCount = { newCount ->
					counterState.value = newCount
				}
			)
		}
	}
}

@Composable
fun ItemList(modifier: Modifier = Modifier, goals: List<Goal>) {
	// We save the scrolling position with this state that can also
	// be used to programmatically scroll the list
	val scrollState = rememberLazyListState()

	// We save the coroutine scope where our animated scroll will be executed
	val coroutineScope = rememberCoroutineScope()

	LazyColumn(modifier = modifier, state = scrollState) {
		items(items = goals) { goal ->
			Greeting(name = goal.title)
		}
	}
}

@Composable
fun Greeting(name: String) {
	Text(text = "Hello $name!", modifier = Modifier.padding(12.dp))
}

@Composable
fun Counter(count: Int, updateCount: (Int) -> Unit) {
	Button(
		onClick = { updateCount(count + 1) },
		colors = ButtonDefaults.buttonColors(
			backgroundColor = if (count > 5) Color.Green else Color.White
		)
	) {
		Text("I've been clicked $count times")
	}
}

@Preview("MyScreen preview")
@Composable
fun DefaultPreview() {
	MyApp {
		GoalListWithList(CommonFunctions.testData(), { })
	}
}
