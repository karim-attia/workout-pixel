package ch.karimattia.workoutpixel.screens.allGoals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ch.karimattia.workoutpixel.core.Screens
import ch.karimattia.workoutpixel.core.dateBeautiful
import ch.karimattia.workoutpixel.core.last3Am
import ch.karimattia.workoutpixel.core.plural
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.screens.CardWithTitle
import ch.karimattia.workoutpixel.screens.Lambdas
import java.util.Locale

@Composable
fun GoalCard(
    goal: Goal,
    lambdas: Lambdas,
    withLink: Boolean = true,
) {

    CardWithTitle(
        title = goal.title,
        onClick = { if (withLink) lambdas.navigateTo(Screens.GoalDetailView.name, goal, false) })
    {
        // Preview and rest
        Row(
            verticalAlignment = Alignment.Top,
            // modifier = Modifier.padding(all = 1.dp),
        ) {
/*
			GoalPreview(
				goal = goal,
				onClick = { lambdas.updateAfterClick(goal) },
				settingsData = lambdas.settingsData,
				modifier = Modifier.padding(all = 4.dp)
			)
*/
            Row(
                // horizontalArrangement = Arrangement.SpaceBetween,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
                    .padding(top = 1.dp)
                //.background(MaterialTheme.colorScheme.primary)
            ) {
/*
                Box(modifier = Modifier.weight(0.15f)) {
                    Divider(
                        color = sameHueLowerSaturation(MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(2.dp)
                    )
                }
*/

                Box(modifier = Modifier.weight(1f)) {
                    StatisticsCount(goal = goal)
                }

                /*
                                                Box(modifier = Modifier.weight(0.15f)) {
                                                    Divider(
                                                        color = sameHueLowerSaturation(MaterialTheme.colorScheme.primary),
                                                        modifier = Modifier
                                                            .fillMaxHeight()
                                                            .width(2.dp)
                                                    )
                                                }
                                */


                Box(modifier = Modifier.weight(1f)) {
                    StatisticsLastDone(goal = goal)
                }

                /*
                                                Box(modifier = Modifier.weight(0.15f)) {
                                                    Divider(
                                                        color = sameHueLowerSaturation(MaterialTheme.colorScheme.primary),
                                                        modifier = Modifier
                                                            .fillMaxHeight()
                                                            .width(2.dp)
                                                    )
                                                }
                                */


                Box(modifier = Modifier.weight(1f)) {
                    StatisticsInterval(goal = goal)
                }
            }
        }

    }
}

@Composable
fun StatisticsCount(goal: Goal) {
    Statistics(
        icon = Icons.Default.TrendingUp,
        label = "Done",
        info = goal.count.toString(),
        unit = plural(goal.count, "time")
    )
}

@Composable
fun StatisticsLastDone(goal: Goal) {
    val info = dateBeautiful(
        date = goal.lastWorkout,
        agoWording = false,
        // locale = Locale.getDefault()
    )
    Statistics(
        icon = Icons.Default.Done,
        label = "Last done",
        info = info,
        unit = if (goal.lastWorkout < last3Am() && info != "Never") "ago" else "",
    )
}

@Composable
fun StatisticsInterval(goal: Goal) {
    Statistics(
        icon = Icons.Outlined.DateRange,
        label = "Every",
        info = goal.intervalBlue.toString(),
        unit = if (goal.intervalBlue == 1) "day" else "days"
    )
}

@Composable
fun Statistics(
    icon: ImageVector,
    label: String,
    info: String,
    unit: String,
) {
    val infoAndUnit: AnnotatedString = buildAnnotatedString {
        withStyle(
            MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Medium
            ).toSpanStyle()
        ) { append(info) }
        withStyle(
            MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                // color = Color.Gray
            ).toSpanStyle()
        ) { append(" $unit") }
    }

    Column(
        // space between
        // verticalArrangement = Arrangement.SpaceBetween,
        // horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 0.5.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(4.dp)
            )
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(start = 8.dp)

    )
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(22.dp)
            // .padding(start = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                // tint = sameHueLowerSaturation(MaterialTheme.colorScheme.primary),
                // tint = MaterialTheme.colorScheme.primary,
                tint = Color.Gray,
                modifier = Modifier
                    .size(if (icon == Icons.Outlined.DateRange) 18.dp else 22.dp)
                    .padding(end = 4.dp)
            )

            Text(
                text = label.uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelSmall,
                // color = sameHueLowerSaturation(MaterialTheme.colorScheme.primary),
                color = Color.Gray,

                modifier = Modifier
                //.padding(top = 2.dp, start = 2.dp, end = 2.dp)
            )
        }
        Text(
            text = infoAndUnit,
            fontWeight = FontWeight.Bold,
            // color = MaterialTheme.colorScheme.onPrimary,
            // color = MaterialTheme.colorScheme.primary,
            // color = Color.Gray,
            modifier = Modifier
                .padding(start = 2.dp)
        )

    }

}