package ch.karimattia.workoutpixel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.Goal
import java.util.*

@Composable
fun AllGoals(
	goals: List<Goal>,
	updateAfterClick: (Goal) -> Unit,
	navigateTo: (destination:String) -> Unit,
	setAppBarTitle: (appBarText:String) -> Unit,
	) {

	setAppBarTitle("Workout Pixel")
	// TODO: Move to Instructions if no goals

	GoalList(
		goals = goals,
		updateAfterClick = updateAfterClick,
		navigateTo = navigateTo,
	)
}

@Composable
fun GoalList(
	goals: List<Goal>,
	updateAfterClick: (Goal) -> Unit,
	navigateTo: (destination:String) -> Unit,
) {
	// We save the scrolling position with this state that can also
	// be used to programmatically scroll the list
	val scrollState = rememberLazyListState()

/*
	// We save the coroutine scope where our animated scroll will be executed
	val coroutineScope = rememberCoroutineScope()
*/

	LazyColumn(
		state = scrollState,
		contentPadding = PaddingValues(top = 6.dp, bottom = 40.dp)
	) {
		items(
			items = goals,
		) { goal ->
			GoalCard(
				goal = goal,
				updateAfterClick = { updateAfterClick(goal) },
				navigateTo = navigateTo
			)
		}
	}
}

@Composable
fun GoalCard(
	modifier: Modifier = Modifier,
	goal: Goal,
	updateAfterClick: () -> Unit,
	navigateTo: (destination:String) -> Unit,
) {
	Card(
		backgroundColor = Color.White,
		elevation = 4.dp,
		modifier = Modifier
			.padding(vertical = 4.dp, horizontal = 8.dp)
			.fillMaxWidth()
			.clickable {
				navigateTo("${WorkoutPixelScreen.GoalDetailView.name}/${goal.uid}")
			}
	) {
		// Preview and rest
		Row(
			modifier = Modifier.padding(all = 1.dp),
		) {
			GoalPreview(
				goal = goal, updateAfterClick = updateAfterClick, modifier = Modifier.padding(4.dp)
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
	goal: Goal, updateAfterClick: () -> Unit, modifier: Modifier = Modifier
) {
	Text(
		text = goal.widgetText(),
		textAlign = TextAlign.Center,
		fontSize = 12.sp,
		color = Color.White,
		modifier = modifier
			.clickable { updateAfterClick() }
			.width(66.dp)
			.height(44.dp)
			.clip(shape = RoundedCornerShape(4.dp))
			.background(Color(CommonFunctions.getColorFromStatus(goal.status)))
			.wrapContentSize(Alignment.Center)
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
		20, 2,
		goal.everyWording()
	)
}

@Composable
fun LastDoneIconAndText(goal: Goal) {
	IconAndText(
		Icons.Filled.Done,
		22, 0,
		CommonFunctions.dateBeautiful(goal.lastWorkout)
	)
}

@Composable
fun IconAndText(icon: ImageVector, size: Int, iconPaddingLeft: Int, text: String) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Icon(
			icon, contentDescription = null,
			Modifier
				.padding(end = iconPaddingLeft.dp)
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

@Preview(name = "MyScreen preview")
@Composable
fun AllGoalsPreview() {
	AllGoals(
		goals = CommonFunctions.testData(),
		updateAfterClick = {},
		navigateTo = {},
		setAppBarTitle = {}
	)
}
