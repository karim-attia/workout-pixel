package ch.karimattia.workoutpixel.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import ch.karimattia.workoutpixel.core.colorToInt
import ch.karimattia.workoutpixel.core.testGoals
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData
import ch.karimattia.workoutpixel.ui.theme.GrayBackgroundColor

@Suppress("unused")
private const val TAG: String = "Reusables"
typealias GoalFunction = (Goal) -> Unit

@Composable
fun GoalPreview(
	goal: Goal,
	modifier: Modifier = Modifier,
	settingsData: SettingsData = SettingsData(),
	backgroundColor: Color = goal.color(settingsData = settingsData),
	onClick: () -> Unit = {},
) {
	Column(

		modifier = modifier
			.clickable { onClick() }
			.width(66.dp)
			.height(44.dp)
			.clip(shape = RoundedCornerShape(8.dp))
			.background(backgroundColor)
			.wrapContentSize(Alignment.Center),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,

		) {

		Text(
			text = goal.title,
			textAlign = TextAlign.Center,
			fontSize = 12.sp,
			fontWeight = FontWeight(500),
			color = Color.White,
			// Very hacky!! Should set a body style somewhere.
			style = LocalTextStyle.current.copy(lineHeight = 14.sp)

		)
		if (goal.showDate || goal.showTime) {
			Text(
				text = goal.widgetTextDateAndTime(),
				textAlign = TextAlign.Center,
				fontSize = 10.sp,
				fontWeight = FontWeight(500),
				color = settingsData.colorLighter(goal.status()),
				// Very hacky!! Should set a supporting style somewhere.
				// style = LocalTextStyle.current.copy(lineHeight = 12.sp)
				style = MaterialTheme.typography.labelSmall.copy(lineHeight = 12.sp)
			)
		}
	}
}

@Composable
fun CardWithTitle(
	title: String,
	isClickable: Boolean = false,
	onClick: () -> Unit = {},
	content: @Composable () -> Unit,
) {
	FormattedCard(
		// paddingOutsideOfCard = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
		isClickable = isClickable,
		onClick = onClick,
	) {
		CardTitle(
			text = title
		)
		content()
	}
}

@Composable
fun FormattedCard(
	modifier: Modifier = Modifier,
	paddingBetweenCardAndContent: PaddingValues = PaddingValues(all = 12.dp),
	paddingOutsideOfCard: PaddingValues = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
	isClickable: Boolean = false,
	onClick: () -> Unit = {},
	content: @Composable () -> Unit,
) {
	ElevatedCard(
		// backgroundColor = Color.White,
		// elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
		modifier = modifier
			.fillMaxWidth()
			.padding(paddingOutsideOfCard)
			.then(
				if (isClickable) Modifier.clickable(onClick = onClick) else Modifier
			)
	) {
		Column(
			modifier = Modifier.padding(paddingBetweenCardAndContent)
		) {
			content()
		}
	}
}

@Composable
fun CardTitle(
	text: String,
) {
	Text(
		text = text,
		// fontSize = 20.sp,
		fontWeight = FontWeight.Bold,
		style = MaterialTheme.typography.titleMedium,
		modifier = Modifier.padding(bottom = 12.dp)
	)
	HorizontalDivider(
		modifier = Modifier
			.padding(bottom = 24.dp)
			.width(44.dp),
		thickness = 3.dp,
		color = MaterialTheme.colorScheme.primary
	)
}

// Colors: https://developer.android.com/jetpack/compose/components/switch
@Composable
fun SwitchWithText(
	description: String,
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(verticalAlignment = Alignment.CenterVertically,
		// space between
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier = modifier
			.fillMaxWidth()
			.clickable { onCheckedChange(!checked) }
			.padding(vertical = 4.dp)) {
		Text(
			text = description,
			style = MaterialTheme.typography.bodyMedium,
			modifier = Modifier.padding(start = 6.dp),
		)
		Switch(checked = checked, onCheckedChange = null, // {onCheckedChange(it) }
			thumbContent = if (checked) {
				{
					Icon(
						imageVector = Icons.Filled.Check,
						contentDescription = null,
						tint = MaterialTheme.colorScheme.onPrimary,
						modifier = Modifier.size(SwitchDefaults.IconSize),
					)
				}
			} else {
				null
			}, colors = SwitchDefaults.colors(
				checkedThumbColor = MaterialTheme.colorScheme.primary,
				checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
				//uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
				//uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
			)


		)

	}
}

@Composable
fun GoalTitleTextField(
	title: String,
	onValueChange: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	val keyboardController = LocalSoftwareKeyboardController.current
	val interactionSource = remember { MutableInteractionSource() }
	OutlinedTextField(
		value = title,
		onValueChange = { onValueChange(it) },
		textStyle = LocalTextStyle.current.copy(
			// color = MaterialTheme.colors.onBackground,
			fontSize = 20.sp
		),
		interactionSource = interactionSource,
		label = { Text(text = "Goal title") },
		// Text looks weird
		// placeholder = { Text(text = "Goal title") },
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done
		),
		keyboardActions = KeyboardActions(onDone = {
			keyboardController?.hide()
		}),
		modifier = modifier.fillMaxWidth()
	)
}

@Composable
fun GoalPreviewsWithBackground() {
	Row(
		modifier = Modifier
			.padding(start = 0.dp, top = 8.dp, bottom = 0.dp)
			.background(GrayBackgroundColor)
			.padding(all = 6.dp)
	) {
		GoalPreview(goal = testGoals[0])
		Spacer(modifier = Modifier.width(8.dp))
		GoalPreview(goal = testGoals[1])
	}
}

@Composable
fun GoalPreviewWithBackground(goal: Goal) {
	Row(
		modifier = Modifier
			.padding(start = 0.dp, top = 8.dp, bottom = 0.dp)
			.background(GrayBackgroundColor)
			.padding(all = 6.dp)
	) {
		GoalPreview(goal = goal)
	}
}

data class Lambdas(
	val updateAfterClick: GoalFunction = {},
	val updateGoal: GoalFunction = {},
	val deleteGoal: GoalFunction = {},
	val insertGoal: GoalFunction = {},
	val widgetPinningPossible: Boolean = true,
	val addWidgetToHomeScreen: suspend (goal: Goal, insertNewGoal: Boolean) -> Int = { _, _ -> 0 },
	val settingsData: SettingsData = SettingsData(),
	val settingChange: (SettingsData) -> Unit = {},
	val navigateTo: (destination: String, goal: Goal?, popBackStack: Boolean) -> Unit = { _, _, _ -> },
	val navigateUp: (setGoalToNull: Boolean) -> Unit = { },
)

fun sameHueLowerSaturationInt(originalColor: Int): Int {
	val hsl = FloatArray(3)
	ColorUtils.colorToHSL(originalColor, hsl)
	// hue
	// hsl[0] *= 1f
	// saturation
	hsl[1] = 0.8f
	// hsl[1] *= 0.7f
	// lightness
	hsl[2] = 0.92f
	// hsl[2] *= 0.84f
	return ColorUtils.HSLToColor(hsl)
}

@Suppress("unused")
fun sameHueLowerSaturation(originalColor: Color): Color {
	val colorInt = colorToInt(originalColor)
	val hsl = FloatArray(3)
	ColorUtils.colorToHSL(colorInt, hsl)
	// hue
	// hsl[0] *= 1f
	// saturation
	hsl[1] = 0.8f
	// hsl[1] *= 0.7f
	// lightness
	hsl[2] = 0.92f
	// hsl[2] *= 0.84f
	return Color(ColorUtils.HSLToColor(hsl))
}
