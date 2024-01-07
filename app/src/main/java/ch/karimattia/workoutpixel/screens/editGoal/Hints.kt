package ch.karimattia.workoutpixel.screens.editGoal

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Hints(
    modifier: Modifier = Modifier,
) {
    FreeStandingTitle(text = "Hints", firstTitle = true)

    Row(
        modifier = modifier
            .fillMaxWidth()
            //.padding(top = 8.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        // Box(modifier = Modifier.weight(1f)) {
        HintCard(
            title = "Start small",
            text = "Start doing it every 2-3 days instead of every day."
        )
        // }
        // Box(modifier = Modifier.weight(1f)) {
        HintCard(
            title = "Have fun with it",
            text = "Feel a sense of accomplishment when reaching your goal."
        )
        // }
        // Box(modifier = Modifier.weight(1f)) {
        HintCard(
            title = "Build a habit",
            text = "Feel good when seeing green goals on your homescreen."
        )
        // }
    }
}

@Composable
fun HintCard(
    title: String,
    text: String,
) {
    ElevatedCard(
        modifier = Modifier
            .padding(end = 8.dp)
            //.height(150.dp)
            .width(160.dp)
        // .fillMaxWidth()

    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}