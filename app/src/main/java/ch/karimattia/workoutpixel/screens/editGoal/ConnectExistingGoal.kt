package ch.karimattia.workoutpixel.screens.editGoal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ch.karimattia.workoutpixel.data.Goal

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

			Text(
				text = "You have defined goals that currently don\'t have a widget. You can connect the widget to one of those goals. This takes over all its data including the history.",
				style = MaterialTheme.typography.bodyMedium,
				modifier = modifier,
			)

			Spacer(modifier = Modifier.height(24.dp))

			// Row {
			var selectedIndex: Int by remember { mutableIntStateOf(0) }
			ConnectDropdown(
				goalsWithoutWidget = goalsWithoutWidget,
				selectedIndex = selectedIndex,
				changeSelectedIndex = { selectedIndex = it },
				modifier = modifier,
			)
			Spacer(modifier = Modifier.height(24.dp))
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
					DropdownMenuItem(
						text = { Text(text = goalWithoutWidget.toString()) },
						onClick = {
							changeSelectedIndex(index)
							expanded = false
						})
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
		Icon(
			imageVector = Icons.Filled.Cable,
			contentDescription = null
		)
		Spacer(modifier = Modifier.width(8.dp))
		Text(text = "Connect widget".uppercase())
	}
}