package ch.karimattia.workoutpixel.core

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment.Companion.CenterVertically
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import ch.karimattia.workoutpixel.core.Constants.GOAL_UID
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.GoalRepository
import ch.karimattia.workoutpixel.data.SettingsData
import ch.karimattia.workoutpixel.data.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val TAG = "GlanceWidget"

class GlanceWidget : GlanceAppWidget() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        provideContent {

            val prefs: Preferences = currentState<Preferences>()
            val settingsData = SettingsData()
            settingsData.colorDoneInt =
                prefs[intPreferencesKey("colorDoneInt")] ?: settingsData.colorDoneInt
            settingsData.colorFirstIntervalInt = prefs[intPreferencesKey("colorFirstIntervalInt")]
                ?: settingsData.colorFirstIntervalInt
            settingsData.colorSecondIntervalInt = prefs[intPreferencesKey("colorSecondIntervalInt")]
                ?: settingsData.colorSecondIntervalInt
            settingsData.colorInitialInt =
                prefs[intPreferencesKey("colorInitialInt")] ?: settingsData.colorInitialInt
            settingsData.dateLanguage =
                prefs[stringPreferencesKey("dateLanguage")] ?: settingsData.dateLanguage
            settingsData.dateCountry =
                prefs[stringPreferencesKey("dateCountry")] ?: settingsData.dateCountry
            settingsData.timeLanguage =
                prefs[stringPreferencesKey("timeLanguage")] ?: settingsData.timeLanguage
            settingsData.timeCountry =
                prefs[stringPreferencesKey("timeCountry")] ?: settingsData.timeCountry

            // TODO: Create Goal(prefs: Preferences)
            val goal = Goal()
            goal.uid = prefs[intPreferencesKey("uid")] ?: goal.uid
            goal.appWidgetId = prefs[intPreferencesKey("appWidgetId")] ?: goal.appWidgetId
            goal.title = prefs[stringPreferencesKey("title")] ?: goal.title
            goal.lastWorkout = prefs[longPreferencesKey("lastWorkout")] ?: goal.lastWorkout
            goal.intervalBlue = prefs[intPreferencesKey("intervalBlue")] ?: goal.intervalBlue
            goal.intervalRed = prefs[intPreferencesKey("intervalRed")] ?: goal.intervalRed
            goal.showDate = prefs[booleanPreferencesKey("showDate")] ?: goal.showDate
            goal.showTime = prefs[booleanPreferencesKey("showTime")] ?: goal.showTime

            val smiley = prefs[booleanPreferencesKey("smiley")] ?: false
            WidgetContent(goal = goal, settingsData = settingsData, smiley = smiley)
        }
    }
}


@Composable
fun WidgetContent(
    goal: Goal,
    smiley: Boolean,
    settingsData: SettingsData
) {
    Log.d(TAG, "WidgetContent $goal")
    // TODO: Get settingsData
    val backgroundColor = goal.color(settingsData)

    // TODO: Styling seems inelegant
    Column(
        modifier = GlanceModifier
            //.width(66.dp)
            .height(44.dp)
            .fillMaxWidth()
            .clickable(
                onClick = actionRunCallback<ClickAction>(
                    parameters = actionParametersOf(
                        actionGoal to goal.uid
                    )
                )
            )
            .background(backgroundColor, backgroundColor)
        // actionParametersOf: https://proandroiddev.com/building-app-widgets-with-glance-8278cb455afa
        //.cornerRadius(8.dp)
        ,
        verticalAlignment = CenterVertically
    ) {
        Text(
            text = if (!smiley) goal.title else ":)",
            // .wrapContentSize(Alignment.Center)

            modifier = GlanceModifier
                .fillMaxWidth()
                // Do I really need this in the row and the text? Seems like it from testing... Or not anymore?
/*
                .clickable(
                    onClick = actionRunCallback<ClickAction>(
                        parameters = actionParametersOf(
                            actionGoal to goal.uid
                        )
                    )
                ),
*/,

            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = if (!smiley) 12.sp else 16.sp,
                textAlign = TextAlign.Center,
                color = ColorProvider(Color.White)
            ),
        )
        if (!smiley && (goal.showDate || goal.showTime)) {
            Text(
                text = goal.widgetTextDateAndTime(settingsData),
                // .wrapContentSize(Alignment.Center)
                modifier = GlanceModifier
                    .fillMaxWidth()
                    // Do I really need this in the row and the text? Seems like it from testing... Or not anymore?
/*
                    .clickable(
                        onClick = actionRunCallback<ClickAction>(
                            parameters = actionParametersOf(
                                actionGoal to goal.uid
                            )
                        )
                    ),
*/,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    // color = ColorProvider(Color.White),
                    color = ColorProvider(settingsData.colorLighter(goal.status()))
                ),
            )
        }

    }
}

// On widget click
// Same as ACTION_DONE_EXERCISE
class ClickAction : ActionCallback {

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
            Log.d(
                TAG,
                "[Glance] ON_RECEIVE ${intent.action}------------------------------------------------------------------------"
            )
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
                        goal.appWidgetId = intent.getIntExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID
                        )
                        goalActions(goal).runUpdate()
                        repository.updateGoal(goal)
                    }
                }

                intent.action.equals(Constants.ACTION_ALARM_UPDATE) -> {
                    // Do this when the alarm hits
                    saveTimeWithStringToSharedPreferences(
                        context,
                        "[Glance] ACTION_ALARM_UPDATE ${dateTimeBeautiful(System.currentTimeMillis())}"
                    )
                    otherActions.updateAllWidgets()
                }
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        runBlocking {
            Log.d(
                TAG,
                "ON_UPDATE\n------------------------------------------------------------------------"
            )
            // Start alarm
            widgetAlarm.startAlarm()
            otherActions.updateAllWidgets()
            saveTimeWithStringToSharedPreferences(
                context,
                "[Glance] onUpdate ${dateTimeBeautiful(System.currentTimeMillis())}"
            )
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        runBlocking {
            // When the user deletes the widget, delete the preference associated with it.
            Log.d(TAG, "ON_DELETED")

            // TODO: This is also called if the configuration is aborted. Then, this database call is useless.
            for (appWidgetId in appWidgetIds) repository.setAppWidgetIdToNullByAppwidgetId(
                appWidgetId
            )

            saveTimeWithStringToSharedPreferences(
                context,
                "onDeleted ${dateTimeBeautiful(System.currentTimeMillis())}"
            )
        }
    }

    override fun onEnabled(context: Context) {
        runBlocking {

            // Enter relevant functionality for when the first widget is created
            Log.d(TAG, "ON_ENABLED")
            super.onEnabled(context)

            otherActions.updateAllWidgets()

            // Start alarm
            Log.v(TAG, "START_ALARM")
            widgetAlarm.startAlarm()
            Log.v(TAG, "ALARM_STARTED")
            saveTimeWithStringToSharedPreferences(
                context,
                "onEnabled ${dateTimeBeautiful(System.currentTimeMillis())}"
            )
        }
    }

    override fun onDisabled(context: Context) {
        runBlocking {
            // Enter relevant functionality for when the last widget is disabled
            // Stop alarm only if all widgets have been disabled
            Log.d(TAG, "ON_DISABLED")
            if (otherActions.appWidgetIds.isEmpty()) {
                // stop alarm
                Log.v(TAG, "STOP_ALARM")
                widgetAlarm.stopAlarm()
                Log.v(TAG, "STOPPED_ALARM")
            }
            saveTimeWithStringToSharedPreferences(
                context,
                "onDisabled ${dateTimeBeautiful(System.currentTimeMillis())}"
            )
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        runBlocking {
            val goal: Goal? = repository.loadGoalByAppWidgetId(appWidgetId = appWidgetId)
            // There may be no goal with this appWidgetId, e.g. if the app data was deleted.
            if (goal != null) goalActions(goal).runUpdate()
            saveTimeWithStringToSharedPreferences(
                context,
                "onAppWidgetOptionsChanged ${dateTimeBeautiful(System.currentTimeMillis())}"
            )
        }
    }
}

val actionGoal = ActionParameters.Key<Int>(GOAL_UID)
