package ch.karimattia.workoutpixel.data

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

	fun updateSettings(settingsData: SettingsData) = viewModelScope.launch {
		settingsRepository.updateSettings(settingsData)
	}
}