package ch.karimattia.workoutpixel

import android.util.Log
import ch.karimattia.workoutpixel.core.Goal

fun goalFromGoalsByUid(goalUid: Int?, goals: List<Goal>): Goal? {
	return if (goalUid == null || goalUid == -1) {
		Log.d("goalFromGoalsByUid", "null or -1")
		null
	} else {
		Log.d("goalFromGoalsByUid", "$goalUid")
		goalFromGoalsByUid(goals = goals, goalUid = goalUid)
	}
}

// TODO: Crashes if no goal found
fun goalFromGoalsByUid(goalUid: Int, goals: List<Goal>): Goal {
	return goals.first { it.uid == goalUid }
}

/*
class UtilsKotlin @Inject constructor(var goalSaveActionsFactory: GoalSaveActions.Factory) {
	fun goalSaveActions(goal: Goal): GoalSaveActions {
		return goalSaveActionsFactory.create(goal)
	}
}*/
