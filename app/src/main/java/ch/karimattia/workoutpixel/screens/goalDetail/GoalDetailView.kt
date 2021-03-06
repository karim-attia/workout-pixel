package ch.karimattia.workoutpixel.screens.goalDetail

import android.appwidget.AppWidgetManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.karimattia.workoutpixel.R
import ch.karimattia.workoutpixel.core.dateBeautiful
import ch.karimattia.workoutpixel.core.timeBeautiful
import ch.karimattia.workoutpixel.data.*
import ch.karimattia.workoutpixel.screens.*
import ch.karimattia.workoutpixel.screens.allGoals.IntervalIconAndText
import ch.karimattia.workoutpixel.screens.allGoals.LastDoneIconAndText
import ch.karimattia.workoutpixel.ui.theme.GreenTest
import ch.karimattia.workoutpixel.ui.theme.TextBlack
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch

@Suppress("unused")
private const val TAG: String = "GoalDetailView"

@Composable
fun GoalDetailView(
	currentGoal: Goal,
	// Move into lambdas?
	pastClickViewModelAssistedFactory: PastClickViewModelAssistedFactory,
	goalDetailViewModel: GoalDetailViewModel = viewModel(factory = provideFactory(pastClickViewModelAssistedFactory, currentGoal.uid)),
	pastClicks: List<PastClick> = goalDetailViewModel.pastClicks,
	lambdas: Lambdas,
) {
	GoalDetailView(
		currentGoal = currentGoal,
		updatePastClick = { updatedPastClick -> goalDetailViewModel.updatePastClick(updatedPastClick) },
		pastClicks = pastClicks,
		lambdas = lambdas,
	)
}

@Composable
fun GoalDetailView(
	currentGoal: Goal,
	updatePastClick: (PastClick) -> Unit,
	pastClicks: List<PastClick>,
	lambdas: Lambdas,
) {
	Column(
		modifier = Modifier
			//.padding(PaddingValues(top = 6.dp, bottom = 40.dp))
			// TODO
			.verticalScroll(rememberScrollState())
	) {
		Spacer(modifier = Modifier.height(6.dp))
		GoalOverview(
			currentGoal = currentGoal,
			lambdas = lambdas,
		)
		AnimatedVisibility(!currentGoal.hasValidAppWidgetId()) {
			GoalDetailNoWidgetCard(
				currentGoal = currentGoal, lambdas = lambdas,/*addWidgetToHomeScreen = addWidgetToHomeScreen*/
			)
		}

		PastClicks(
			currentGoal = currentGoal,
			updatePastClick = updatePastClick,
			pastClicks = pastClicks,
			lambdas = lambdas,
		)

		Spacer(modifier = Modifier.height(40.dp))
	}
}

@Composable
fun GoalOverview(
	currentGoal: Goal,
	lambdas: Lambdas,
) {
	CardWithTitle(
		title = "Goal overview",
	)
	{
		// Preview and rest
		Row(verticalAlignment = Alignment.CenterVertically) {
			GoalPreview(
				goal = currentGoal, settingsData = lambdas.settingsData, onClick = { lambdas.updateAfterClick(currentGoal) }
			)
			// Rest
			Column(
				modifier = Modifier.padding(start = 6.dp, top = 3.dp)
			)
			{
				IntervalIconAndText(goal = currentGoal)
				LastDoneIconAndText(goal = currentGoal, settingsData = lambdas.settingsData)
			}
		}
	}
}

@Composable
fun GoalDetailNoWidgetCard(
	currentGoal: Goal,
	lambdas: Lambdas,
) {
	CardWithTitle(
		title = "No widget for this goal"
	) {
		val dialogState = rememberMaterialDialogState()
		Infobox(text = "There is no widget for this goal on your homescreen. Add a new widget and connect it to this goal to keep the data.")
		Button(
			onClick = { dialogState.show() },
			modifier = Modifier
				.padding(top = 8.dp)
				.fillMaxWidth(),
			colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red, contentColor = Color.White)
		) {
			Icon(imageVector = Icons.Filled.DeleteForever, contentDescription = null)
			Spacer(modifier = Modifier.padding(end = 8.dp))
			Text(text = "Delete goal".uppercase(), modifier = Modifier.padding(end = 16.dp))
		}
		MaterialDialog(dialogState = dialogState, buttons = {
			positiveButton(text = "Confirm") {
				lambdas.deleteGoal(currentGoal)
				lambdas.navigateUp(true)
			}
			negativeButton(text = "Cancel")
		}) {
			title(text = "Do you really want to delete this goal?")
			message(text = "This will irreversibly remove the goal including all its data like past clicks.")
		}

		val appWidgetManager = AppWidgetManager.getInstance(LocalContext.current)
		val scope = rememberCoroutineScope()

		if (appWidgetManager.isRequestPinAppWidgetSupported) {
			Button(
				onClick = { scope.launch { lambdas.addWidgetToHomeScreen(currentGoal, false) } },
				modifier = Modifier
					.padding(top = 4.dp)
					.fillMaxWidth(),
				colors = ButtonDefaults.buttonColors(backgroundColor = GreenTest, contentColor = Color.White)
			) {
				Icon(imageVector = Icons.Filled.Widgets, contentDescription = null)
				Spacer(modifier = Modifier.padding(end = 8.dp))
				Text(text = "Add to homescreen".uppercase(), modifier = Modifier.padding(end = 16.dp))
			}
		}
	}
}

@Composable
fun PastClicks(
	currentGoal: Goal,
	pastClicks: List<PastClick>,
	updatePastClick: (PastClick) -> Unit,
	lambdas: Lambdas,
) {
	CardWithTitle(
		title = "Past clicks",
	) {
		PastClickList(
			currentGoal = currentGoal,
			updatePastClick = updatePastClick,
			pastClicks = pastClicks,
			lambdas = lambdas,
		)
	}
}

@Composable
fun PastClickList(
	currentGoal: Goal,
	pastClicks: List<PastClick>,
	updatePastClick: (PastClick) -> Unit,
	lambdas: Lambdas,
) {
	val numberOfPastClicks = pastClicks.size
	if (numberOfPastClicks > 0) {
		Column {
			PastClickEntry(date = "Date", time = "Time", bold = true)
			Divider(color = Color(TextBlack), thickness = 1.dp)

			// TODO: Limit number of numberOfPastClicks, declare it. Or implement some paging.
			// https://developer.android.com/topic/libraries/architecture/paging.html
			for (pastClick in pastClicks) {
				key(pastClick.uid) {
					PastClickEntry(
						pastClick = pastClick,
						togglePastClick = {
							val updatedPastClick = pastClick.copy(isActive = !pastClick.isActive)
							updatePastClick(updatedPastClick)
							// Copying to prevent changes on screen that are not driven by the viewmodel.
							// This is all super explicit and unelegant, but there were some bugs here in the past.
							val updatedPastClicks = pastClicks.toMutableList()
							updatedPastClicks[updatedPastClicks.indexOfFirst { click -> click.uid == updatedPastClick.uid }] = updatedPastClick
							// updatedPastClicks.replaceAll{click -> if (click.uid == updatedPastClick.uid) updatedPastClick else click }
							// If this change causes a new last workout time, do all the necessary updates.
							// TODO: Move setNewLastWorkout to GoalSaveActions and directly save the updated goal there?
							// TODO: lastWorkout <-> lastClick
							// TODO: Also have goalRepository in this viewModel?
							val lastWorkout = lastClickBasedOnActiveClicks(updatedPastClicks)
							if (currentGoal.lastWorkout != lastWorkout) {
								lambdas.updateGoal(currentGoal.copy(lastWorkout = lastWorkout))
							}
						},
						lambdas = lambdas,
					)
					Divider(color = Color(TextBlack), thickness = 0.5.dp)
				}
			}
		}
	}
	if (numberOfPastClicks == 0) {
		Text(
			text = stringResource(R.string.you_have_never_completed_this_goal_as_soon_as_you_click_on_the_widget_the_click_will_show_up_here),
			fontSize = 14.sp
		)
	}
	Spacer(modifier = Modifier.padding(end = 6.dp))
}


@Composable
fun PastClickEntry(
	pastClick: PastClick,
	togglePastClick: () -> Unit,
	lambdas: Lambdas,
) {
	PastClickEntry(
		date = dateBeautiful(pastClick.workoutTime, lambdas.settingsData.dateLocale()),
		time = timeBeautiful(pastClick.workoutTime, lambdas.settingsData.timeLocale()),
		icon = if (pastClick.isActive) {
			Icons.Filled.Delete
		} else {
			Icons.Filled.Undo
		},
		active = pastClick.isActive,
		togglePastClick = togglePastClick,
	)
}

@Composable
fun PastClickEntry(
	date: String,
	time: String,
	icon: ImageVector? = null,
	bold: Boolean = false,
	active: Boolean = true,
	togglePastClick: () -> Unit = { },
) {
	Row {
		val fontWeight = if (bold) {
			FontWeight.Bold
		} else {
			null
		}
		val textDecoration = if (!active) {
			TextDecoration.LineThrough
		} else {
			null
		}
		Text(
			text = date,
			fontSize = 14.sp,
			fontWeight = fontWeight,
			style = TextStyle(textDecoration = textDecoration),
			modifier = Modifier
				.width(90.dp)
				.padding(start = 4.dp, end = 12.dp)
				.align(Alignment.CenterVertically)
		)
		Text(
			text = time,
			fontSize = 14.sp,
			fontWeight = fontWeight,
			style = TextStyle(textDecoration = textDecoration),
			modifier = Modifier
				.width(90.dp)
				.padding(end = 12.dp)
				.align(Alignment.CenterVertically)
		)
		Spacer(modifier = Modifier.weight(1f))
		if (icon != null) {
			Icon(
				imageVector = icon,
				contentDescription = null,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.align(alignment = Alignment.CenterVertically)
					.clickable { togglePastClick() }
			)
		}
	}
}