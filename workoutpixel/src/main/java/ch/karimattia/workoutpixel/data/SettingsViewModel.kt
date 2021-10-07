package ch.karimattia.workoutpixel.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val settingsRepository: SettingsRepository,
) : ViewModel() {

	val settingsData: LiveData<SettingsData> = settingsRepository.getSettings.asLiveData()

	suspend fun updateSettings(settingsData: SettingsData) = settingsRepository.updateSettings(settingsData)
}