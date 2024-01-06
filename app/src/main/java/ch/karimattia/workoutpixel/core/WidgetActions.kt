package ch.karimattia.workoutpixel.core

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import ch.karimattia.workoutpixel.core.Constants.colorDoneInt
import ch.karimattia.workoutpixel.core.Constants.colorFirstIntervalInt
import ch.karimattia.workoutpixel.core.Constants.colorInitialInt
import ch.karimattia.workoutpixel.core.Constants.colorSecondIntervalInt
import ch.karimattia.workoutpixel.core.Constants.dateCountry
import ch.karimattia.workoutpixel.core.Constants.dateLanguage
import ch.karimattia.workoutpixel.core.Constants.timeCountry
import ch.karimattia.workoutpixel.core.Constants.timeLanguage
import ch.karimattia.workoutpixel.data.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*

private const val TAG: String = "WidgetActions"

/**
 * Class that manages everything related to the widget on the homescreen.
 */
class WidgetActions @AssistedInject constructor(
    @ApplicationContext val context: Context,
    private val goalRepository: GoalRepository,
    private val pastClickRepository: PastClickRepository,
    private val settingsRepository: SettingsRepository,
    @Assisted var goal: Goal,
) {

    @AssistedFactory
    interface Factory {
        fun create(goal: Goal): WidgetActions
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun updateAfterClick() {
        Log.d(TAG, "ACTION_DONE_EXERCISE " + goal.debugString() + "start")
        val numberOfPastWorkouts = pastClickRepository.getCountOfActivePastWorkouts(goal.uid) + 1
        Toast.makeText(
            context,
            "Oh yeah! Already done this $numberOfPastWorkouts ${
                plural(
                    numberOfPastWorkouts,
                    "time"
                )
            } :)",
            Toast.LENGTH_SHORT
        ).show()

        // Update the widget data with the latest click
        goal.lastWorkout = System.currentTimeMillis()

        // Instruct the widget manager to update the widget with the latest widget data
        runUpdate(smiley = true)

        GlobalScope.launch(Dispatchers.Default) {
            delay(1000)
            runUpdate(smiley = false)
        }


        // TODO: Handle as transaction: 3. here: https://medium.com/androiddevelopers/7-pro-tips-for-room-fbadea4bfbd1
        // Add the workout to the database. Technicalities are taken care of in PastWorkoutsViewModel.
        pastClickRepository.insertPastClick(
            PastClick(
                widgetUid = goal.uid,
                workoutTime = goal.lastWorkout
            )
        )

        // Update the widget data in the db
        goalRepository.updateGoal(goal)

        Log.d(
            TAG, "ACTION_DONE_EXERCISE ${goal.debugString()}complete    --------------------------------------------"
        )
    }

    // Can also be called on widget with invalid AppWidgetId
    suspend fun runUpdate(smiley: Boolean = false) {
        if (goal.appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            // Try block because getGlanceIdBy throws IllegalArgumentException if no GlanceId is found for this appWidgetId.
            try {
                val glanceAppWidgetManager = GlanceAppWidgetManager(context)
                val glanceId: GlanceId = glanceAppWidgetManager.getGlanceIdBy(goal.appWidgetId)

                // TODO: Create updatePrefs(goal: Goal)
                updateAppWidgetState(context = context, glanceId = glanceId) { prefs ->
                    prefs[intPreferencesKey("uid")] = goal.uid
                    prefs[intPreferencesKey("appWidgetId")] = goal.appWidgetId
                    prefs[stringPreferencesKey("title")] = goal.title
                    prefs[longPreferencesKey("lastWorkout")] = goal.lastWorkout
                    prefs[intPreferencesKey("intervalBlue")] = goal.intervalBlue
                    prefs[intPreferencesKey("intervalRed")] = goal.intervalRed
                    prefs[booleanPreferencesKey("showDate")] = goal.showDate
                    prefs[booleanPreferencesKey("showTime")] = goal.showTime

                    prefs[booleanPreferencesKey("smiley")] = smiley

                    val settingsData: SettingsData = settingsRepository.getSettingsOnce()
                    prefs[intPreferencesKey(colorDoneInt)] = settingsData.colorDoneInt
                    prefs[intPreferencesKey(colorFirstIntervalInt)] = settingsData.colorFirstIntervalInt
                    prefs[intPreferencesKey(colorSecondIntervalInt)] = settingsData.colorSecondIntervalInt
                    prefs[intPreferencesKey(colorInitialInt)] = settingsData.colorInitialInt

                    prefs[stringPreferencesKey(dateLanguage)] = settingsData.dateLanguage as String
                    prefs[stringPreferencesKey(dateCountry)] = settingsData.dateCountry as String
                    prefs[stringPreferencesKey(timeLanguage)] = settingsData.timeLanguage as String
                    prefs[stringPreferencesKey(timeCountry)] = settingsData.timeCountry as String
                }

                val glanceAppWidget: GlanceAppWidget = GlanceWidget()
                glanceAppWidget.update(context, glanceId)

            } catch (e: IllegalArgumentException) {
                Log.d(TAG, "No GlanceId found for this appWidgetId.")
            }

        } else Log.d(TAG, "runUpdate: appWidgetId = null " + goal.debugString())
    }

    /* Is this all for the pinning preview? The pinning preview doesn't even work... */
/*    @SuppressLint("ResourceType")
    private suspend fun widgetView(
        appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
    ): RemoteViews {
        val widgetView = RemoteViews(context.packageName, R.layout.widget_layout)
        val settingsData: SettingsData = settingsRepository.getSettingsOnce()
        // Set an onClickListener for every widget: https://stackoverflow.com/questions/30174386/multiple-instances-of-widget-with-separated-working-clickable-imageview
        widgetView.setOnClickPendingIntent(
            R.id.appwidget_text,
            widgetPendingIntent()
        )

        // Before updating a widget, the text and background of the view need to be set. Otherwise, the existing not updated properties of the widgetView will be passed.
        widgetView.setTextViewText(
            R.id.appwidget_text,
            goal.widgetText(settingsData = settingsData)
        )

        // widgetView.setInt(R.id.appwidget_text, "setBackgroundResource", R.drawable.rounded_corner_green)
        widgetView.setInt(
            R.id.appwidget_text,
            "setBackgroundColor",
            goal.colorInt(settingsData = settingsData)
        )

        // Set size if available
        // https://stackoverflow.com/questions/25153604/get-the-size-of-my-homescreen-widget
        val heightInDp: Int = appWidgetManager.getAppWidgetOptions(goal.appWidgetId)
            .getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 44)
        val paddingInDp: Int = max(heightInDp - 50, 0)
        val density: Float = context.resources.displayMetrics.density
        val paddingInPx: Int = (paddingInDp * density).toInt()
        val paddingTopInPx: Int = ceil(paddingInPx / 4.0).toInt()
        val paddingBottomInPx: Int = floor(paddingInPx / 4.0 * 3.0).toInt()
        widgetView.setViewPadding(R.id.widget_container, 0, paddingTopInPx, 0, paddingBottomInPx)

        return widgetView
    }*/

    // Create an Intent to set the action DONE_EXERCISE. This will be received in onReceive.
    private fun widgetPendingIntent(): PendingIntent? {
        val intent = Intent(context, GlanceWidgetReceiver::class.java)
        intent.action = Constants.ACTION_DONE_EXERCISE
        // put the appWidgetId as an extra to the update intent
        if (goal.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.d(TAG, "widgetPendingIntent: appWidgetId is null where it shouldn't be.")
            return null
        }
        intent.putExtra(Constants.GOAL_UID, goal.uid)
        return PendingIntent.getBroadcast(
            context,
            goal.appWidgetId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // https://developer.android.com/guide/topics/appwidgets/configuration#pin
    // https://developer.android.com/reference/android/appwidget/AppWidgetManager#requestPinAppWidget(android.content.ComponentName,%20android.os.Bundle,%20android.app.PendingIntent)
    suspend fun pinAppWidget() {
        // TODO with https://medium.com/@avengers14.blogger/requestappwidget-jetpack-glance-2e21f7db1adf
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val myProvider = ComponentName(context, GlanceWidgetReceiver::class.java)
        Log.d(TAG, "pinAppWidget ${goal.debugString()}")
        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            // Create the PendingIntent object only if your app needs to be notified that the user allowed the widget to be pinned. Note that, if the pinning operation fails, your app isn't notified. This callback receives the ID of the newly-pinned widget (EXTRA_APPWIDGET_ID).
            val pinIntent = Intent(context, GlanceWidgetReceiver::class.java)
            pinIntent.action = Constants.ACTION_SETUP_WIDGET
            pinIntent.putExtra(Constants.GOAL_UID, goal.uid)

            val successCallback: PendingIntent = PendingIntent.getBroadcast(
                /* context = */ context,
                /* requestCode = */ 0,
                /* intent = */ pinIntent,
                /* flags = */ PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Looks weird.
            // val bundle = Bundle()
            // bundle.putParcelable(AppWidgetManager.EXTRA_APPWIDGET_PREVIEW, widgetView())

            appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
        }
    }
}

