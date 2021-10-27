package ch.karimattia.workoutpixel.screens.progress

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.karimattia.workoutpixel.core.goalFromGoalsByUid
import ch.karimattia.workoutpixel.data.Goal
import ch.karimattia.workoutpixel.data.PastClick
import ch.karimattia.workoutpixel.data.PastClickAndGoal
import ch.karimattia.workoutpixel.data.PastClickRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
private const val TAG: String = "ProgressViewModel"

@HiltViewModel
class ProgressViewModel @Inject constructor(
	pastClickRepository: PastClickRepository,
) : ViewModel() {
	private val activePastClicksAndGoalLastWeekFlow: Flow<List<PastClickAndGoal>> = pastClickRepository.activePastClicksAndGoalLastWeek()

/*
	fun updatePastClick(pastClick: PastClick) = viewModelScope.launch {
		pastClickRepository.updatePastClick(pastClick)
	}
*/

	val activePastClicksAndGoalLastWeek: SnapshotStateList<PastClickAndGoal> = mutableStateListOf()

/*
	fun pastClicksAndGoal(goals: List<Goal>): Flow<List<PastClickAndGoal>> = activePastClicksLastWeekFlow.map { activePastClicksLastWeek ->
		activePastClicksLastWeek.map { pastClick ->
			// Null assertion is safe since DB deletes pastClicks when goal is deleted.
			PastClickAndGoal(pastClick, goalFromGoalsByUid(goalUid = pastClick.widgetUid, goals = goals)!!)
		}
	}
*/

	// This is needed so the state of pastClicks and thus the UI updates.
	init {
		viewModelScope.launch {
			activePastClicksAndGoalLastWeekFlow.collectLatest { newPastClickAndGoal ->
				activePastClicksAndGoalLastWeek.clear()
				activePastClicksAndGoalLastWeek.addAll(newPastClickAndGoal)
			}
		}
	}
}