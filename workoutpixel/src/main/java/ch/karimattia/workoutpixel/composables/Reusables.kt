package ch.karimattia.workoutpixel.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.core.getColorFromStatusColor
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData
import ch.karimattia.workoutpixel.ui.theme.InfoColor

@Suppress("unused")
private const val TAG: String = "Reusables"

@Composable
fun GoalPreview(
	goal: Goal,
	settingsData: SettingsData,
	modifier: Modifier = Modifier,
	backgroundColor: Color = getColorFromStatusColor(goal.status(), settingsData = settingsData),
	onClick: () -> Unit = {},
) {
	Text(
		text = goal.widgetText(settingsData),
		textAlign = TextAlign.Center,
		fontSize = 12.sp,
		color = Color.White,
		modifier = modifier
			.clickable { onClick() }
			.width(66.dp)
			.height(44.dp)
			.clip(shape = RoundedCornerShape(4.dp))
			.background(backgroundColor)
			.wrapContentSize(Alignment.Center)
	)
}

@Composable
fun CardWithTitle(
	title: String,
	onClick: () -> Unit = {},
	content: @Composable () -> Unit,
) {
	FormattedCard(
		paddingOutsideOfCard = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
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
	paddingBetweenCardAndContent: PaddingValues = PaddingValues(all = 8.dp),
	paddingOutsideOfCard: PaddingValues = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
	onClick: () -> Unit = {},
	content: @Composable () -> Unit,
) {
	Card(
		backgroundColor = Color.White,
		elevation = 4.dp,
		modifier = Modifier
			.fillMaxWidth()
			.padding(paddingOutsideOfCard)
			.clickable { onClick() }
	) {
		Column(
			modifier = Modifier
				.padding(paddingBetweenCardAndContent)
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
		fontSize = 20.sp,
		fontWeight = FontWeight.Bold,
		modifier = Modifier.padding(bottom = 8.dp)
	)

}

@Composable
fun Infobox(
	text: String,
	modifier: Modifier = Modifier,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.clip(shape = RoundedCornerShape(4.dp))
			.background(Color(InfoColor))
			.padding(4.dp)
	) {
		Icon(
			imageVector = Icons.Filled.Info,
			contentDescription = "Info icon",
			modifier = Modifier
				.size(32.dp)
		)
		Text(
			text = text,
			fontSize = 14.sp,
			modifier = Modifier.padding(start = 4.dp)
		)
	}
}


@Composable
fun CheckboxWithText(
	description: String,
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.fillMaxWidth()
			.clickable { onCheckedChange(!checked) }
			.padding(vertical = 4.dp)
	) {
		Checkbox(
			checked = checked,
			onCheckedChange = null // {onCheckedChange(it) }
		)
		Text(
			text = description,
			modifier = Modifier.padding(start = 6.dp),
		)
	}
}

@ExperimentalComposeUiApi
@Composable
fun GoalTitleTextField(
	title: String,
	onValueChange: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	val keyboardController = LocalSoftwareKeyboardController.current
	val interactionSource = remember { MutableInteractionSource() }
	BasicTextField(
		value = title,
		onValueChange = {			onValueChange(it)		},
		textStyle = LocalTextStyle.current.copy(
			color = MaterialTheme.colors.onBackground,
			fontSize = 20.sp
		),
		interactionSource = interactionSource,
		decorationBox = { innerTextField ->
			Column(
				modifier = Modifier
					.fillMaxWidth()
			) {
				val isFocused = interactionSource.collectIsFocusedAsState().value
				Box(modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp)) { innerTextField() }
				Divider(thickness = 1.5.dp, color = if (isFocused) MaterialTheme.colors.primary else Color.Gray)
			}
		},
		cursorBrush = SolidColor(MaterialTheme.colors.primary),
		singleLine = true,
		keyboardOptions = KeyboardOptions(
			capitalization = KeyboardCapitalization.Sentences,
			imeAction = ImeAction.Done
		),
		keyboardActions = KeyboardActions(onDone = {
			keyboardController?.hide()
		}),
		modifier = modifier
			.fillMaxWidth()
			.padding(bottom = 10.dp)
	)
}