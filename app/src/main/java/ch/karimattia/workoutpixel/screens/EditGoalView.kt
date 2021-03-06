package ch.karimattia.workoutpixel.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.R
import ch.karimattia.workoutpixel.core.Status
import ch.karimattia.workoutpixel.core.plural
import ch.karimattia.workoutpixel.core.testGoals
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData
import java.util.*

@Suppress("unused")
private const val TAG: String = "EditGoalView"

@ExperimentalComposeUiApi
@Composable
fun EditGoalView(
	initialGoal: Goal,
	isFirstConfigure: Boolean,
	goalsWithoutWidget: List<Goal> = emptyList(),
	lambdas: Lambdas,
) {
	Log.d(TAG, "START")
	Log.d(TAG, "isFirstConfigure: $isFirstConfigure")

	// For some reason, any change in the goal recomposes this page
	// It even updates the initialGoal
	// For some weird reason, only the first ever change is reflected in this original goal. -> Doesn't happen if internal goal is used.
	// This original goal is then also shown if the back button is clicked.
	// This doesn't happen if the reflected data is saved via the update button.
	// Copying the goal values fixes this...
	// TODO: Save all changes directly to the DB to simplify this.

	// https://stackoverflow.com/questions/63956058/jetpack-compose-state-modify-class-property (Last answer for policy)
	val (editGoalViewGoal: Goal, setValueEditGoalViewGoal: (Goal) -> Unit) = remember {
		mutableStateOf(
			value = initialGoal.copy(),
			policy = neverEqualPolicy()
		)
	}
	// If the caller sends a new initialGoal, update editGoalViewGoal with it. If it's the same one (e.g. through a recomposition), keep the values edited by the user.
	val lastValueOfInitialGoal = remember { mutableStateOf(initialGoal) }
	if (lastValueOfInitialGoal.value != initialGoal) {
		setValueEditGoalViewGoal(initialGoal)
		lastValueOfInitialGoal.value = initialGoal
	}

	Column(
		modifier = Modifier
			.fillMaxHeight()
			.verticalScroll(rememberScrollState())
			.padding(start = 8.dp, end = 8.dp, bottom = 12.dp)
	) {
		val modifier = Modifier.padding(top = 8.dp)

		Hints(
			modifier = modifier,
		)
		var connectExistingGoal by remember { mutableStateOf(false) }
		if (isFirstConfigure && goalsWithoutWidget.isNotEmpty()) {
			ConnectExistingGoal(
				goalsWithoutWidget = goalsWithoutWidget,
				connectGoal = { updatedConnectedGoal ->
					updatedConnectedGoal.appWidgetId = initialGoal.appWidgetId
					lambdas.updateGoal(updatedConnectedGoal)
					lambdas.navigateUp(false)
				},
				modifier = modifier,
			)
			connectExistingGoal = true
		} else {
			connectExistingGoal = false
		}
		SetUpYourWidget(
			setUpYourWidgetGoal = editGoalViewGoal,
			setUpYourWidgetGoalChange = { changedGoal ->
				// Copying goal or policy required: https://stackoverflow.com/questions/63956058/jetpack-compose-state-modify-class-property
				// setValueEditGoalViewGoal(changedGoal.copy())

				setValueEditGoalViewGoal(changedGoal)
			},
			isFirstConfigure = isFirstConfigure,
			connectExistingGoal = connectExistingGoal,
			modifier = modifier,
		)
		WidgetConfigurationPreview(
			editGoalViewGoal = editGoalViewGoal,
			isFirstConfigure = isFirstConfigure,
			settingsData = lambdas.settingsData,
			modifier = modifier,
		)
		Spacer(
			modifier = Modifier
				.height(10.dp)
				.weight(weight = 1f, fill = true)
				.background(Color.Blue)
		)
		AddUpdateWidgetButton(
			isFirstConfigure = isFirstConfigure,
			insertUpdateWidget = {
				if (isFirstConfigure) lambdas.insertGoal(editGoalViewGoal)
				else {
					lambdas.updateGoal(editGoalViewGoal)
					lambdas.navigateUp(false)
				}
			},
			modifier = Modifier
				.padding(top = 24.dp)
				.align(Alignment.End),
		)
	}
}

@Composable
fun Hints(
	modifier: Modifier = Modifier,
) {
	FreeStandingTitle(text = "Hints", topPadding = 8.dp)
	val paragraphs = arrayListOf(
		buildAnnotatedString {
			withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Start small. ") }
			append("Start doing it every 2-3 days instead of every day.")
		},
		buildAnnotatedString {
			withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Have fun reaching your goal. ") }
			append("Feel a sense of accomplishment when clicking the widget.")
		},
		buildAnnotatedString {
			withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Build a habit. ") }
			append("Remind yourself that you have reached your goals whenever you look at your homescreen.")
		})

	for (paragraph in paragraphs) {
		Text(
			text = paragraph,
			fontSize = 14.sp,
			modifier = modifier
		)
	}
}

@ExperimentalComposeUiApi
@Composable
fun ConnectExistingGoal(
	goalsWithoutWidget: List<Goal>,
	connectGoal: (Goal) -> Unit,
	modifier: Modifier = Modifier,
) {
	var expando: Boolean by remember { mutableStateOf(false) }
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.fillMaxWidth()
			.clickable { expando = !expando }
	) {
		FreeStandingTitle(text = "Connect an existing goal", Modifier.weight(1f))
		Icon(
			imageVector = if (!expando) Icons.Filled.ExpandMore else Icons.Filled.ExpandLess,
			contentDescription = null,
			modifier = Modifier.padding(top = 16.dp)
		)
	}

	AnimatedVisibility(visible = expando, enter = fadeIn(), exit = shrinkOut()) {
		Column {

			Infobox(
				text = stringResource(R.string.connect_widget_instructions),
				modifier = modifier,
			)
			// Row {
			var selectedIndex: Int by remember { mutableStateOf(0) }
			ConnectDropdown(
				goalsWithoutWidget = goalsWithoutWidget,
				selectedIndex = selectedIndex,
				changeSelectedIndex = { selectedIndex = it },
				modifier = modifier,
			)
			ConnectButton(
				connectWidget = {
					// Need to check if it is null and if it is, make the selection red?
					// connectSpinner.setBackgroundColor(Color.RED)
					connectGoal(goalsWithoutWidget[selectedIndex])
				},
				modifier = modifier,
			)
		}
	}


	// }

}

@Composable
fun ConnectDropdown(
	goalsWithoutWidget: List<Goal>,
	selectedIndex: Int,
	changeSelectedIndex: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {
	var expanded: Boolean by remember { mutableStateOf(false) }
	val icon = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown

	Box {
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
						changeSelectedIndex(index)
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
fun ConnectButton(
	connectWidget: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Button(
		onClick = {
			connectWidget()
		},
		modifier = modifier.fillMaxWidth(),
		) {
		Text(text = "Connect widget".uppercase())
		Icon(
			imageVector = Icons.Filled.Cable,
			contentDescription = null
		)
	}
}


@ExperimentalComposeUiApi
@Composable
fun SetUpYourWidget(
	setUpYourWidgetGoal: Goal,
	setUpYourWidgetGoalChange: (Goal) -> Unit,
	isFirstConfigure: Boolean,
	modifier: Modifier = Modifier,
	connectExistingGoal: Boolean = false,
) {
	FreeStandingTitle(
		text = if (!isFirstConfigure) {
			stringResource(id = R.string.reconfigureWidgetActivityLabel)
		} else if (!connectExistingGoal) {
			stringResource(id = R.string.configuration_title)
		} else {
			stringResource(id = R.string.configuration_widget_setup_title_new_goal)
		}
	)

	Text(
		text = stringResource(id = R.string.widgetTitle),
		fontSize = 14.sp,
		modifier = modifier
	)

	GoalTitleTextField(
		title = setUpYourWidgetGoal.title,
		onValueChange = {
			setUpYourWidgetGoal.title = it
			setUpYourWidgetGoalChange(setUpYourWidgetGoal)
		},
		modifier = modifier
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
			.width(28.dp)
			.height(28.dp)
		val textModifier: Modifier = Modifier
			.padding(end = 12.dp)
		Button(
			onClick = {
				setUpYourWidgetGoal.intervalBlue--
				setUpYourWidgetGoalChange(setUpYourWidgetGoal)
			},
			enabled = setUpYourWidgetGoal.intervalBlue >= 2,
			contentPadding = PaddingValues(all = 0.dp),
			modifier = buttonModifier,
		) {
			Icon(
				imageVector = Icons.Filled.Remove,
				contentDescription = null,
			)
		}
		Text(
			text = setUpYourWidgetGoal.intervalBlue.toString(),
			modifier = textModifier
		)
		Button(
			onClick = {
				setUpYourWidgetGoal.intervalBlue++
				setUpYourWidgetGoalChange(setUpYourWidgetGoal)
			},
			contentPadding = PaddingValues(all = 0.dp),
			modifier = buttonModifier,
		) {
			Icon(
				imageVector = Icons.Filled.Add,
				contentDescription = null,
			)
		}
		Text(
			text = plural(setUpYourWidgetGoal.intervalBlue, "day") + ".",
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
			setUpYourWidgetGoalChange(setUpYourWidgetGoal)
		},
	)
	CheckboxWithText(
		description = stringResource(id = R.string.show_time_in_the_widget),
		checked = setUpYourWidgetGoal.showTime,
		onCheckedChange = {
			setUpYourWidgetGoal.showTime = it
			setUpYourWidgetGoalChange(setUpYourWidgetGoal)
		},
	)
}

@Composable
fun WidgetConfigurationPreview(
	editGoalViewGoal: Goal,
	isFirstConfigure: Boolean,
	settingsData: SettingsData,
	modifier: Modifier = Modifier,
) {
	// 12dp because the checkbox above has 4 dp clickable area and then 16 is too much.
	FreeStandingTitle(text = "Preview", topPadding = 12.dp)
	GoalPreview(
		goal = if (isFirstConfigure) editGoalViewGoal.copy(statusOverride = Status.GREEN) else editGoalViewGoal,
		settingsData = settingsData,
		modifier = modifier,
	)
}

@Composable
fun FreeStandingTitle(
	text: String,
	modifier: Modifier = Modifier,
	topPadding: Dp = 16.dp,
) {
	Text(
		text = text.uppercase(Locale.getDefault()),
		fontSize = 16.sp,
		fontWeight = FontWeight.Bold,
		modifier = modifier.padding(top = topPadding)
	)
}

@Composable
fun AddUpdateWidgetButton(
	isFirstConfigure: Boolean,
	insertUpdateWidget: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Button(
		onClick = {
			insertUpdateWidget()
		},
		modifier = modifier
			.fillMaxWidth(),
	) {
		Text(
			text = if (isFirstConfigure) {
				"Add widget".uppercase()
			} else {
				"Update goal".uppercase()
			},
			modifier = Modifier.padding(end = 16.dp)
		)
		Icon(
			imageVector = Icons.Filled.Save,
			contentDescription = null
		)
	}
}

@ExperimentalComposeUiApi
@Preview(name = "EditGoalView preview", showBackground = true)
@Composable
fun EditGoalViewPreview() {
	EditGoalView(
		initialGoal = testGoals[0],
		isFirstConfigure = false,
		lambdas = Lambdas()
	)
}
