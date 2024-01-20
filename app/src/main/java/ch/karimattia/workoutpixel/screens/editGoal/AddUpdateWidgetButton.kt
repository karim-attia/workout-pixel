package ch.karimattia.workoutpixel.screens.editGoal

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
		Icon(
			imageVector = Icons.Filled.Save,
			contentDescription = null
		)
		Spacer(modifier = Modifier.width(8.dp))
		Text(
			text = if (isFirstConfigure) {
				"Add widget".uppercase()
			} else {
				"Update goal".uppercase()
			},
		)
	}
}