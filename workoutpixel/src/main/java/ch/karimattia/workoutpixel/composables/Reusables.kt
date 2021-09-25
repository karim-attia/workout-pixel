package ch.karimattia.workoutpixel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.core.getColorFromStatus
import ch.karimattia.workoutpixel.ui.theme.InfoColor

@Composable
fun GoalPreview(
	goal: Goal,
	modifier: Modifier = Modifier,
	onClick: () -> Unit = {},
) {
	GoalPreviewWithColor(
		goal = goal,
		color = Color(getColorFromStatus(goal.status)),
		modifier = modifier,
		onClick = onClick,
	)
}

@Composable
fun GoalPreviewWithColor(
	goal: Goal,
	color: Color,
	modifier: Modifier = Modifier,
	onClick: () -> Unit = {},
) {
	Text(
		text = goal.widgetText(),
		textAlign = TextAlign.Center,
		fontSize = 12.sp,
		color = Color.White,
		modifier = modifier
			.clickable { onClick() }
			.width(66.dp)
			.height(44.dp)
			.clip(shape = RoundedCornerShape(4.dp))
			.background(color)
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
	text: String
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
	modifier: Modifier = Modifier
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
	modifier: Modifier = Modifier
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			//.padding(top = 4.dp)
			.fillMaxWidth()
			.clickable { onCheckedChange(!checked) }
	) {
		Checkbox(
			checked = checked,
			onCheckedChange = { onCheckedChange(it) }
		)
		Text(
			text = description,
			modifier = Modifier.padding(start = 6.dp),
		)
	}
}
