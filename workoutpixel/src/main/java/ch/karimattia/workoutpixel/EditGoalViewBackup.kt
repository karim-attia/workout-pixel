package ch.karimattia.workoutpixel

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.core.CommonFunctions
import ch.karimattia.workoutpixel.core.Goal
import java.util.*

@ExperimentalComposeUiApi
@Composable
fun EditGoalViewBackup(
	goal: Goal,
	setAppBarTitle: (appBarText: String) -> Unit,
	isFirstConfigure: Boolean = true,
	addUpdateWidget: (Goal) -> Unit,
) {
	// For some reason, any change in the goal recomposes this page
	// It even updates the original goal
	// For some weird reason, only the first ever change is reflected in this original goal.
	// This original goal is then also shown if the back button is clicked.
	// This doesn't happen if the reflected data is saved via the update button.

	Log.d("EDIT GOAL VIEW", "original goal: " + goal.intervalBlue.toString())

	// Should the title be updated when the text field is updated? I think no.
	setAppBarTitle(goal.title)
	val (editGoalViewGoal, setValueEditGoalViewGoal) = remember { mutableStateOf(goal) }

	Log.d(
		"EDIT GOAL VIEW",
		"editGoalViewGoal, setValueEditGoalViewGoal: " + editGoalViewGoal.intervalBlue.toString()
	)

	Column(
		modifier = Modifier
			.verticalScroll(rememberScrollState())
			.padding(start = 8.dp, end = 8.dp, bottom = 20.dp)
	) {
		val modifier = Modifier.padding(top = 8.dp)
		Hints(
			modifier = modifier,
		)
		if (isFirstConfigure) {
			ConnectAnExistingGoal(
				modifier = modifier,
			)
		}
		SetUpYourWidgetBackup(
			setUpYourWidgetGoal = editGoalViewGoal,
			goalChange = { goalChangeGoal ->
				// Extract copy to CommonFunctions or Goal
				// https://stackoverflow.com/questions/63956058/jetpack-compose-state-modify-class-property
				val goalLocal: Goal = Goal(
					goalChangeGoal.appWidgetId,
					goalChangeGoal.title,
					goalChangeGoal.lastWorkout,
					goalChangeGoal.intervalBlue,
					goalChangeGoal.intervalRed,
					goalChangeGoal.showDate,
					goalChangeGoal.showTime,
					goalChangeGoal.status,
				)
				goalLocal.setUid(goalChangeGoal.uid)

				setValueEditGoalViewGoal(
					goalLocal
				)
				Log.d(
					"EDIT GOAL VIEW",
					"SetUpYourWidget goalChange" + editGoalViewGoal.intervalBlue.toString()
				)
			},
			modifier = modifier,
		)
		WidgetConfigurationPreview(
			editGoalViewGoal = editGoalViewGoal,
			modifier = modifier,
		)
		AddUpdateWidgetButton(
			addUpdateWidget = {
				Log.d(
					"EDIT GOAL VIEW",
					"AddWidgetButton addUpdateWidget" + editGoalViewGoal.intervalBlue.toString()
				)
				addUpdateWidget(editGoalViewGoal)
			},
			modifier = Modifier
				.padding(top = 24.dp)
				.align(Alignment.End),
		)
	}
}

@Composable
fun HintsBackup(
	modifier: Modifier = Modifier
) {
	FreeStandingTitle(text = "Hints")
	val text: AnnotatedString = buildAnnotatedString {

		withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
			append("Start small. ")
		}
		append("Start doing it every 2-3 days instead of every day. ")
		withStyle(style = ParagraphStyle(lineHeight = 8.sp)) { "\n" }

		withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
			append("Have fun reaching your goal. ")
		}
		append("Feel a sense of accomplishment when clicking the widget.")
		withStyle(style = ParagraphStyle(lineHeight = 8.sp)) { "\n" }

		withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
			append("Build a habit. ")
		}
		append("Remind yourself that you have reached your goals whenever you look at your homescreen.")
	}

	Text(
		text = text,
		fontSize = 14.sp,
		modifier = modifier
	)
}

@ExperimentalComposeUiApi
@Composable
fun ConnectAnExistingGoalBackup(
	modifier: Modifier = Modifier
) {
	// TODO: Expando
	FreeStandingTitle(text = "Connect an existing goal")
	/*FormattedCard(
		paddingOutsideOfCard = PaddingValues(vertical = 8.dp, horizontal = 0.dp)
	) {*/
	Infobox(
		text = stringResource(R.string.connect_widget_instructions),
		modifier = modifier,
	)
	// Row {
	ConnectDropdown(
		goalsWithoutWidget = CommonFunctions.testData(),
		modifier = modifier,
	)
	ConnectButton(
		connectWidget = { /*TODO*/ },
		modifier = modifier,
	)
	// }

}

@ExperimentalComposeUiApi
@Composable
fun ConnectDropdownBackup(
	goalsWithoutWidget: List<Goal>,
	modifier: Modifier = Modifier,
) {
	var expanded: Boolean by remember { mutableStateOf(false) }
	var selectedIndex: Int by remember { mutableStateOf(0) }
	val icon = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown

	Box {
		// https://stackoverflow.com/questions/67902919/jetpack-compose-textfield-clickable-does-not-work
		val (focusRequester) = FocusRequester.createRefs()
		val interactionSource = remember { MutableInteractionSource() }

		OutlinedTextField(
			value = goalsWithoutWidget[selectedIndex].toString(),
			trailingIcon = {
				Icon(
					imageVector = icon,
					"contentDescription",
					modifier = Modifier
					//	.clickable { expanded = !expanded }
				)
			},
			label = { Text("Choose goal") },
			onValueChange = { },
			readOnly = true,
			// enabled = false,
			singleLine = true,
			modifier = modifier
				.fillMaxWidth()
				//.clickable(onClick = { expanded = !expanded })
				.focusRequester(focusRequester)
			// .background(Color.Gray)
		)
		if (!expanded) {
			Box(
				modifier = Modifier
					.padding(horizontal = 8.dp)
					.matchParentSize()
					.clickable(
						onClick = {
							expanded = !expanded
							focusRequester.requestFocus() //to give the focus to the TextField
						},
						interactionSource = interactionSource,
						indication = null //to avoid the ripple on the Box
					)
			)
		}
		if (expanded) {
			DropdownMenu(
				expanded = expanded,
				onDismissRequest = { expanded = false },
				modifier = Modifier
					.padding(horizontal = 8.dp)
					.background(Color.LightGray)
					.fillMaxWidth()
					.padding(horizontal = 0.dp)
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
	}
}

@Composable
fun ConnectButtonBackup(
	connectWidget: () -> Unit,
	modifier: Modifier = Modifier
) {
	Button(
		onClick = { connectWidget() },
		modifier = modifier
	) {
		Text(text = "Connect widget")
		Icon(
			imageVector = Icons.Filled.Cable,
			contentDescription = null
		)
	}
}


@Composable
fun SetUpYourWidgetBackup(
	setUpYourWidgetGoal: Goal,
	goalChange: (Goal) -> Unit,
	modifier: Modifier = Modifier,
) {
	// TODO: Text based on firstConfigure.
	FreeStandingTitle(text = "Set up your widget")

	/*Text(
		text = stringResource(id = R.string.widgetTitle),
		modifier = modifier,
	)*/
	OutlinedTextField(
		value = setUpYourWidgetGoal.title,
		onValueChange = {
			setUpYourWidgetGoal.title = it
			goalChange(setUpYourWidgetGoal)
		},
		label = { Text(stringResource(id = R.string.widgetTitle)) },
		keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
		modifier = modifier
			.fillMaxWidth(),
	)
	Text(
		text = stringResource(id = R.string.interval),
		modifier = modifier,
	)
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier,
	) {
		val buttonModifier: Modifier = Modifier
			.padding(end = 12.dp)
			.width(32.dp)
			.height(32.dp)
		val textModifier: Modifier = Modifier
			.padding(end = 12.dp)
		Button(
			onClick = {
				setUpYourWidgetGoal.intervalBlue--
				Log.d("EDIT GOAL VIEW", "--" + setUpYourWidgetGoal.intervalBlue.toString())
				goalChange(setUpYourWidgetGoal)
			},
			enabled = setUpYourWidgetGoal.intervalBlue >= 2,
			contentPadding = PaddingValues(0.dp),
			modifier = buttonModifier,
		) {
			Text(
				text = "–",
				fontSize = 24.sp,
			)
		}
		Text(
			text = setUpYourWidgetGoal.intervalBlue.toString(),
			modifier = textModifier
		)
		Button(
			onClick = {
				setUpYourWidgetGoal.intervalBlue++
				Log.d("EDIT GOAL VIEW", "++" + setUpYourWidgetGoal.intervalBlue.toString())
				goalChange(setUpYourWidgetGoal)
			},
			contentPadding = PaddingValues(0.dp),
			modifier = buttonModifier,
		) {
			Text(
				text = "+",
				fontSize = 24.sp,
			)
		}
		Text(
			text = CommonFunctions.days(setUpYourWidgetGoal.intervalBlue),
			modifier = textModifier,
		)
	}
	Text(
		text = stringResource(id = R.string.date_time_instructions),
		modifier = modifier,
	)
	CheckboxWithText(
		description = stringResource(id = R.string.show_date_in_the_widget),
		checked = setUpYourWidgetGoal.showDate,
		onCheckedChange = {
			setUpYourWidgetGoal.showDate = it
			goalChange(setUpYourWidgetGoal)
		},
		modifier = modifier,
	)
	CheckboxWithText(
		description = stringResource(id = R.string.show_time_in_the_widget),
		checked = setUpYourWidgetGoal.showTime,
		onCheckedChange = {
			setUpYourWidgetGoal.showTime = it
			goalChange(setUpYourWidgetGoal)
		},
		modifier = Modifier.padding(top = 4.dp),
	)
}

@Composable
fun WidgetConfigurationPreviewBackup(
	goal: Goal,
	modifier: Modifier = Modifier,
) {
	FreeStandingTitle(text = "Preview")
	GoalPreview(
		goal = goal,
		modifier = modifier,
	)
}

@Composable
fun FreeStandingTitleBackup(
	text: String
) {
	Text(
		text = text.uppercase(Locale.getDefault()),
		fontSize = 16.sp,
		fontWeight = FontWeight.Bold,
		modifier = Modifier.padding(top = 12.dp)
	)
}

@Composable
fun AddWidgetButtonBackup(
	addUpdateWidget: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Button(
		onClick = {
			addUpdateWidget()
		},
		modifier = modifier
			.fillMaxWidth(),
	) {
		Text(
			text = "Add widget",
			modifier = Modifier.padding(end = 16.dp)
		)
		Icon(
			imageVector = Icons.Filled.Done,
			contentDescription = null
		)
	}
}

/*@Composable
fun ConfigureScreenContent(
	content: @Composable () -> Unit,
) {
	content()
}*/

@ExperimentalComposeUiApi
@Preview("EditGoalView preview")
@Composable
fun EditGoalViewPreviewBackup() {
	EditGoalView(
		initialGoal = CommonFunctions.testData()[0],
		setAppBarTitle = { },
		addUpdateWidget = { },
	)
}
