package ch.karimattia.workoutpixel

import android.util.Log
import ch.karimattia.workoutpixel.core.Goal

class UtilsKotlin {

}

fun goalFromGoalsByUid (goalUid: Int?, goals: List<Goal>) : Goal? {
	return if (goalUid == null || goalUid == -1) {
		Log.i("goalFromGoalsByUid", "null")
		null
	}
	else {
		Log.i("goalFromGoalsByUid", "$goalUid")
		goalFromGoalsByUid(goals = goals, goalUid = goalUid)
	}
}

// TODO: Crashes if no goal found
fun goalFromGoalsByUid (goalUid: Int, goals: List<Goal>) : Goal {
	return goals.first { it.uid == goalUid }
}