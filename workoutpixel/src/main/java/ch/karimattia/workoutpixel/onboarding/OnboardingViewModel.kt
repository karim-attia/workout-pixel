package ch.karimattia.workoutpixel.onboarding

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ch.karimattia.workoutpixel.core.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
	//private val goalRepository: GoalRepository,
) : ViewModel() {

	private val _step = MutableLiveData(Constants.INVALID_GOAL_UID)
	val step: LiveData<Int> = _step
	fun changeCurrentStep(step: Int) {
		_step.value = step
	}
}
