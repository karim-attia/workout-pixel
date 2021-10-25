package ch.karimattia.workoutpixel.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.core.dateBeautiful
import ch.karimattia.workoutpixel.core.timeBeautiful
import ch.karimattia.workoutpixel.data.PastClick
import java.util.stream.Collectors

@Suppress("unused")
private const val TAG: String = "ReusablesPastClick"

fun lastClickBasedOnActiveClicks(pastClicks: List<PastClick>): Long {
	val activeWorkoutsOrderedByWorkoutTime = pastClicks.stream()
		.filter { clickedClick: PastClick -> clickedClick.isActive }.collect(
			Collectors.toList()
		)

	// If there still is an active past workout, take the latest one to set the last workout time
	return if (activeWorkoutsOrderedByWorkoutTime.isNotEmpty()) activeWorkoutsOrderedByWorkoutTime[0].workoutTime
	// Otherwise, set it to 0.
	else 0L
}