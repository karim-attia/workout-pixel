package ch.karimattia.workoutpixel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.Goal
import java.util.*

@Composable
fun EditGoalView(
	goal: Goal,
	setAppBarTitle: (appBarText: String) -> Unit,
	isFirstConfigure: Boolean = true
) {
	setAppBarTitle(goal.title)
	Column(
		modifier = Modifier
			.padding(horizontal = 8.dp)
			.verticalScroll(rememberScrollState())
	) {
		Hints(
		)
		if (isFirstConfigure) {
			ConnectAnExistingGoal(
			)
		}
		SetUpYourWidget(
			goal = goal,
			onTitleChange = { goal.title = it },
			onIntervalChange = { goal.intervalBlue += it },
			onShowDateChange = { goal.showDate = it },
			onShowTimeChange = { goal.showTime = it },
			)
		WidgetConfigurationPreview(
			goal
		)
	}
}

@Composable
fun Hints(
) {
	FreeStandingTitle(text = "Hints")
	ConfigureScreenContent {
		Text(
			text = stringResource(R.string.instructions_add_widget),
			fontSize = 14.sp
		)
	}
}

@Composable
fun ConnectAnExistingGoal(
) {
	FreeStandingTitle(text = "Connect an existing goal")
	FormattedCard(
		paddingOutsideOfCard = PaddingValues(vertical = 8.dp, horizontal = 0.dp)
	) {
		Infobox(text = stringResource(R.string.connect_widget_instructions))
		Row {
			ConnectDropdown(goalsWithoutWidget = CommonFunctions.testData())
			ConnectButton(connectWidget = { /*TODO*/ })
		}
	}
}

@Composable
fun ConnectDropdown(
	goalsWithoutWidget: List<Goal>
) {
	var expanded: Boolean by remember { mutableStateOf(false) }
	var selectedIndex: Int by remember { mutableStateOf(0) }
	val icon = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown

	OutlinedTextField(
		value = goalsWithoutWidget[selectedIndex].toString(),
		trailingIcon = {
			Icon(
				icon,
				"contentDescription",
				Modifier.clickable { expanded = !expanded })
		},
		label = { Text("Choose goal") },
		onValueChange = { },
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = { expanded = true })
		// .background(Color.Gray)
	)
	DropdownMenu(
		expanded = expanded,
		onDismissRequest = { expanded = false },
		modifier = Modifier
		//	.fillMaxWidth()
		// .background(Color.Red)
	) {
		goalsWithoutWidget.forEachIndexed { index, goalWithoutWidget ->
			DropdownMenuItem(onClick = {
				selectedIndex = index
				expanded = false
			}) {
				Text(text = goalWithoutWidget.toString())
			}
		}
	}
}

@Composable
fun ConnectButton(
	connectWidget: () -> Unit,
) {
	Button(onClick = { connectWidget() }) {
		Text(text = "Connect widget")
	}
}


@Composable
fun SetUpYourWidget(
	goal: Goal,
	onTitleChange: (String) -> Unit,
	onIntervalChange: (Int) -> Unit,
	onShowDateChange: (Boolean) -> Unit,
	onShowTimeChange: (Boolean) -> Unit,
) {
	FreeStandingTitle(text = "Set up your widget")
	Text(text = stringResource(id = R.string.widgetTitle))
	TextField(
		value = goal.title,
		onValueChange = {
			onTitleChange(it)
			goal.title = it
		},
		label = { Text("Label") }
	)
	Text(text = stringResource(id = R.string.interval))
	Row(verticalAlignment = Alignment.CenterVertically) {
		val buttonModifier: Modifier = Modifier
			.width(36.dp)
			.height(40.dp)
		Button(
			onClick = { onIntervalChange(-1) },
			enabled = goal.intervalBlue >= 0,
			modifier = buttonModifier,
		) {
			Text(text = "-")
		}
		Text(text = goal.intervalBlue.toString())
		Button(onClick = { onIntervalChange(1) }, modifier = buttonModifier) {
			Text(text = "+")
		}
		Text(text = CommonFunctions.days(goal.intervalBlue))
	}
	Text(text = stringResource(id = R.string.date_time_instructions))
	Checkbox(
		checked = goal.showDate,
		onCheckedChange = { onShowDateChange(it) },
	)
	Checkbox(
		checked = goal.showTime,
		onCheckedChange = { onShowTimeChange(it) },
	)
}

@Composable
fun WidgetConfigurationPreview(
	goal: Goal
) {
	FreeStandingTitle(text = "Preview")
	GoalPreview(goal = goal, modifier = Modifier.padding(vertical = 4.dp))
}

@Composable
fun FreeStandingTitle(
	text: String
) {
	Text(
		text = text.uppercase(Locale.getDefault()),
		fontSize = 16.sp,
		fontWeight = FontWeight.Bold,
		modifier = Modifier.padding(top = 4.dp)
	)
}

@Composable
fun ConfigureScreenContent(
	content: @Composable () -> Unit,
) {
	content()
}

@Preview("EditGoalView preview")
@Composable
fun EditGoalViewPreview() {
	EditGoalView(
		goal = CommonFunctions.testData()[0],
		setAppBarTitle = {},
	)
}
