package ch.karimattia.workoutpixel.core

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.*
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.background
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.unit.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment.Companion.CenterVertically
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import ch.karimattia.workoutpixel.core.Constants.INVALID_GOAL_UID
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.SettingsData
import javax.inject.Inject

private const val TAG = "GlanceWidget"

class GlanceWidget : GlanceAppWidget() {

    // TODO: Which goal is getting displayed? -> From prefs
    // TODO: Get this goal. -> NO, see below
    // TODO: Draw widget based on prefs: Preferences -> below
    // TODO: Update prefs: Preferences whenever Room is getting updated and call a widget update.


    @Composable
    override fun Content() {
        val prefs: Preferences = currentState<Preferences>()
        val goal: Goal = Goal()
        goal.uid = prefs[intPreferencesKey("uid")] ?: goal.uid
        goal.appWidgetId = prefs[intPreferencesKey("appWidgetId")] ?: goal.appWidgetId
        goal.title = (prefs[stringPreferencesKey("title")] ?: goal.title)
        goal.lastWorkout = prefs[longPreferencesKey("lastWorkout")] ?: goal.lastWorkout
        goal.intervalBlue = prefs[intPreferencesKey("intervalBlue")] ?: goal.intervalBlue
        goal.intervalRed = prefs[intPreferencesKey("intervalRed")] ?: goal.intervalRed
        goal.showDate = (prefs[booleanPreferencesKey("showDate")] ?: goal.showDate)
        goal.showTime = (prefs[booleanPreferencesKey("showTime")] ?: goal.showTime)

        Log.d(TAG, goal.debugString())
        WidgetContent(goal = goal)
    }
}

class GlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = GlanceWidget()
}

@Composable
fun WidgetContent(
    goal: Goal
) {
    Log.d(TAG, "WidgetContent $goal")
    // TODO: Get settingsData
    val backgroundColor = goal.color(SettingsData())
    Log.d(TAG, "backgroundColor $backgroundColor")
    Log.d(TAG, "status ${goal.status()}")

    // TODO: Styling seems unelegant
    Row(
        modifier = GlanceModifier
            //.width(66.dp)
            .height(44.dp)
            .fillMaxWidth()
            .background(backgroundColor, backgroundColor)
            .clickable(onClick = actionRunCallback<ClickAction>())
            .cornerRadius(8.dp),
        verticalAlignment = CenterVertically
    ) {
        Text(
            text = goal.widgetText(SettingsData()),
            // .wrapContentSize(Alignment.Center)
            modifier = GlanceModifier
                .fillMaxWidth(),

            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = ColorProvider(
                    day = Color.White,
                    night = Color.White,
                )
            ),
        )
    }
}

// On widget click
// Same as ACTION_DONE_EXERCISE
//@AndroidEntryPoint
class ClickAction : ActionCallback {

    // TODO: Make goalActions work
    @Inject
    lateinit var widgetActionsFactory: WidgetActions.Factory
    private fun goalActions(goal: Goal): WidgetActions = widgetActionsFactory.create(goal)

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val goal: Goal = Goal()

        Log.d(TAG, "ActionCallback clicked")

        // Update state
        updateAppWidgetState(
            context = context,
            glanceId = glanceId
        ) {
            goal.uid = it[intPreferencesKey("uid")] ?: 0
            // TODO: Update the prefs with click already here?
        }

        // TODO: Not needed
        // Update widget
        GlanceWidget().update(context, glanceId)

        if (goal.uid != INVALID_GOAL_UID) goalActions(goal).updateAfterClick()
    }
}