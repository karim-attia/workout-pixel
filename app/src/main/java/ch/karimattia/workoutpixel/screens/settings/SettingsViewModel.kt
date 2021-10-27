package ch.karimattia.workoutpixel.screens.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ch.karimattia.workoutpixel.data.SettingsData
import ch.karimattia.workoutpixel.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
	private val settingsRepository: SettingsRepository,
) : ViewModel() {

	val settingsData: LiveData<SettingsData> = settingsRepository.getSettings.asLiveData()

	suspend fun updateSettings(settingsData: SettingsData) = settingsRepository.updateSettings(settingsData)
}