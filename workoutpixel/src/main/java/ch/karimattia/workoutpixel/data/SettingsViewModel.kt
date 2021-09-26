package ch.karimattia.workoutpixel.data

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import ch.karimattia.workoutpixel.SettingsData
import ch.karimattia.workoutpixel.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val settingsRepository: SettingsRepository
) : ViewModel() {

	val settingsData: LiveData<SettingsData> = settingsRepository.getSettings.asLiveData()

	/*
	private val _uiState = MutableStateFlow(value = SettingsData())
	val settingsData: StateFlow<SettingsData> = _uiState

	init {
        viewModelScope.launch {
	        settingsRepository.getSetting(colorDone).observeAs()
                // Update View with the latest favorite news
                // Writes to the value property of MutableStateFlow,
                // adding a new element to the flow and updating all
                // of its collectors
                .collect {
                    _uiState.value.colorDoneInt = it
                }
        }
    }

	fun settingsDataLone() = SettingsData(
		colorDoneInt = if (settingsRepository.getSetting(colorDone).asLiveData().value != null) {settingsRepository.getSetting(colorDone).asLiveData().value!!} else 0,
		colorFirstIntervalInt = 0,
		colorSecondIntervalInt = 0,
		colorInitialInt = 0,
		)
	val settingsDataLiveData = flow<SettingsData> { emit (settingsDataLone())}.asLiveData()
*/


	fun updateColorDone(int: Int) = viewModelScope.launch {
		settingsRepository.updateColorDone(int)
	}


}