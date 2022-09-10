package ch.karimattia.workoutpixel.core

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.*
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
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
import ch.karimattia.workoutpixel.core.Constants.GOAL
import ch.karimattia.workoutpixel.core.Constants.GOAL_UID
import ch.karimattia.workoutpixel.core.Constants.INVALID_GOAL_UID
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.GoalRepository
import ch.karimattia.workoutpixel.data.SettingsData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
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
        val goal = Goal()

        // TODO: Create Goal(prefs: Preferences)
        goal.uid = prefs[intPreferencesKey("uid")] ?: goal.uid
        goal.appWidgetId = prefs[intPreferencesKey("appWidgetId")] ?: goal.appWidgetId
        goal.title = (prefs[stringPreferencesKey("title")] ?: goal.title)
        goal.lastWorkout = prefs[longPreferencesKey("lastWorkout")] ?: goal.lastWorkout
        goal.intervalBlue = prefs[intPreferencesKey("intervalBlue")] ?: goal.intervalBlue
        goal.intervalRed = prefs[intPreferencesKey("intervalRed")] ?: goal.intervalRed
        goal.showDate = (prefs[booleanPreferencesKey("showDate")] ?: goal.showDate)
        goal.showTime = (prefs[booleanPreferencesKey("showTime")] ?: goal.showTime)
        WidgetContent(goal = goal)
    }
}

@Composable
fun WidgetContent(
    goal: Goal
) {
    Log.d(TAG, "WidgetContent $goal")
    // TODO: Get settingsData
    val backgroundColor = goal.color(SettingsData())

    // TODO: Styling seems unelegant
    Row(
        modifier = GlanceModifier
            //.width(66.dp)
            .height(44.dp)
            .fillMaxWidth()
            .clickable(onClick = actionRunCallback<ClickAction>(parameters = actionParametersOf(actionGoal to goal.uid)))
            .background(backgroundColor, backgroundColor)
                // actionParametersOf: https://proandroiddev.com/building-app-widgets-with-glance-8278cb455afa
            .cornerRadius(8.dp),
        verticalAlignment = CenterVertically
    ) {
        Text(
            text = goal.widgetText(SettingsData()),
            // .wrapContentSize(Alignment.Center)
            modifier = GlanceModifier
                .fillMaxWidth()
                // Do I really need this in the row and the text? Seems like it from testing. ð¤·ð
                .clickable(onClick = actionRunCallback<ClickAction>(parameters = actionParametersOf(actionGoal to goal.uid)))
            ,

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

    // TODO: Only logic analog to onClickListener here. Update logic in receiver in override fun onReceive

/*
    // TODO: Make goalActions work
    @Inject
    lateinit var widgetActionsFactory: WidgetActions.Factory
    private fun goalActions(goal: Goal): WidgetActions = widgetActionsFactory.create(goal)
*/

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d(TAG, "ActionCallback clicked")
        val intent = Intent(context, GlanceWidgetReceiver::class.java)
        intent.action = Constants.ACTION_DONE_EXERCISE
        intent.putExtra(GOAL_UID, parameters[actionGoal])
        context.sendBroadcast(intent)
    }
}

@AndroidEntryPoint
class GlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = GlanceWidget()

    @Inject
    lateinit var widgetActionsFactory: WidgetActions.Factory
    private fun goalActions(goal: Goal): WidgetActions = widgetActionsFactory.create(goal)

    @Inject
    lateinit var widgetAlarm: WidgetAlarm

    @Inject
    lateinit var otherActions: OtherActions

    @Inject
    lateinit var repository: GoalRepository

    override fun onReceive(context: Context, intent: Intent) {
        runBlocking {
            super.onReceive(context, intent)
            Log.d(TAG, "ON_RECEIVE ${intent.action}------------------------------------------------------------------------")
            when {
                // Do this if the widget has been clicked
                // TODO: If goal can't be loaded show error instead of crash
                intent.action.equals(Constants.ACTION_DONE_EXERCISE) -> {
                    val uid = intent.getIntExtra(GOAL_UID, 0)
                    val goal: Goal? = repository.loadGoalByUid(uid)
                    if (goal != null) goalActions(goal).updateAfterClick()
                }
                intent.action.equals(Constants.ACTION_SETUP_WIDGET) -> {
                    val uid = intent.getIntExtra(GOAL_UID, 0)
                    val goal: Goal? = repository.loadGoalByUid(uid)
                    if (goal != null) {
                        goal.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
                        goalActions(goal).runUpdate(true)
                        repository.updateGoal(goal)
                    }
                }
                intent.action.equals(Constants.ACTION_ALARM_UPDATE) -> {
                    // Do this when the alarm hits
                    saveTimeWithStringToSharedPreferences(context, "ACTION_ALARM_UPDATE ${dateTimeBeautiful(System.currentTimeMillis())}")
                    otherActions.updateAllWidgets()
                }
            }
        }
    }
}

val actionGoal = ActionParameters.Key<Int>(GOAL_UID)
