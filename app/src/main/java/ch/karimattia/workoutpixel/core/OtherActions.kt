package ch.karimattia.workoutpixel.core

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.GoalRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject

private const val TAG: String = "OtherActions"

class OtherActions @Inject constructor(
    @ApplicationContext val context: Context,
    private val goalRepository: GoalRepository,
) {
    @Inject
    lateinit var widgetActionsFactory: WidgetActions.Factory
    private fun goalActions(goal: Goal): WidgetActions = widgetActionsFactory.create(goal)

    // TODO: Understand
    // TODO: Replaced iteration through appWidgetIds with data from DB. Insert check that this is the same and fix if not. Maybe before it only iterated through some widgets. But I don't think it matters.
    // TODO: Could check with CommonFunctions.widgetsWithValidAppWidgetId whether they are the same and at least log if not.
    suspend fun updateAllWidgets() {
        Log.d(TAG, "updateAllWidgets  --------------------------------------------")
        val goalsWithValidAppWidgetId: List<Goal> = goalRepository.loadGoalsWithValidAppWidgetId()
        for (goal in goalsWithValidAppWidgetId) {
            // Tell the AppWidgetManager to perform an update on the current app widget
            // Instruct the widget manager to update the widget with the latest widget data
            goalActions(goal).runUpdate()
        }
    }

    /**
     * Run maintenance once after every app start.
     */
    suspend fun oneTimeSetup() {
        // Update all goals
        // Sometimes the onClickListener in the widgets stop working. This is a super stupid way to regularly reset the onClickListener when you open the main app.
        updateAllWidgets()
        Log.d(TAG, "goalsWithoutGlanceId ${goalsWithoutGlanceId().toString()}")
        Log.d(TAG, "goalsWithGlanceId ${goalsWithGlanceId().toString()}")

		// Clean goals: sets all appWidgetIds of goals that are not valid to null. Maybe later even reassign some to unassigned widgets.
		for (goal in goalsWithoutGlanceId()) {
			goalRepository.setAppWidgetIdToNullByUid(goal.uid)
		}

    }

    /**
     * AppWidgetId stuff
     */
    val appWidgetIds: IntArray =
        AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context.packageName, GlanceWidget::class.java.name))

    // Does not only catch goals with appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID, but actually checks whether there is a widget.
    private fun goalsWithoutWidget(goals: List<Goal>): List<Goal> {
        return goals.stream().filter { (_, _) ->
            Arrays.stream(appWidgetIds).noneMatch { appWidgetId: Int -> appWidgetId == appWidgetId }
        }
            .collect(Collectors.toList())
    }

    @Suppress("unused")
    private fun goalsWithInvalidOrNullAppWidgetId(goals: List<Goal>): List<Goal> {
        return goals.stream()
            .filter { (_, appWidgetId) ->
                Arrays.stream(appWidgetIds).noneMatch { i: Int -> appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && i == appWidgetId }
            }
            .collect(Collectors.toList())
    }

    /**
     * GlanceId stuff
     */

    // Problem statement: Identify in main app which goals have no widget.
    // Solution: Identify in Room DB which goals have no widget. Get goal uids from all widgets. Goals with uids not in this list have no widget.
    suspend fun glanceIds(): List<GlanceId> = GlanceAppWidgetManager(context).getGlanceIds(GlanceWidget::class.java)
    suspend fun goalsWithGlanceId(): List<Goal?> = glanceIds().map {
        goalFromGoalsByUid(
            goalUid = getAppWidgetState(
                context = context,
                definition = PreferencesGlanceStateDefinition,
                glanceId = it
            )[intPreferencesKey("uid")] ?: 0,
            goals = goalRepository.allGoals()
        )
    }.filterNotNull()

    suspend fun goalsWithoutGlanceId(): List<Goal> {
        val list: MutableList<Goal?> = goalRepository.allGoals() as MutableList<Goal?>
        list.removeAll(goalsWithGlanceId())
        return list as List<Goal>
    }

    // Problem: Some widgets are blank. Reason: They have no uid that can be found in the Room DB.

    // TODO: All prefs with uids.

}