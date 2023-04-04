package com.ispgr5.locationsimulator.presentation.sound


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.presentation.run.SoundPlayer
import com.ispgr5.locationsimulator.presentation.util.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * The ViewModel for the Sound
 */
@HiltViewModel
class SoundViewModel @Inject constructor(
	private val configurationUseCases: ConfigurationUseCases,
	//saveStateHandle is required to get the navigation Arguments like configurationId
	private val savedStateHandle: SavedStateHandle
) : ViewModel() {
	// The provided state for the View
	private val _state = mutableStateOf(SoundState())
	val state: State<SoundState> = _state
	private val soundPlayer = SoundPlayer()

	/**
	 * Handles UI Events
	 */
	fun onEvent(event: SoundEvent) {
		when (event) {
			is SoundEvent.RefreshPage -> {
				_state.value = SoundState(event.soundStorageManager.getSoundFileNames())
			}
			is SoundEvent.TestPlaySound -> {
				soundPlayer.startSound(event.soundsDirUri + event.soundName, 1f)
			}
			is SoundEvent.StopPlayback -> {
				soundPlayer.stopPlayback()
			}
			is SoundEvent.SelectSound -> {
				val defaultValues = event.getDefaultValues()
				savedStateHandle.get<Int>("configurationId")?.let { configurationId ->
					event.navController.navigate(Screen.EditTimelineScreen.createRoute(
						configurationId = configurationId,
						soundNameToAdd = event.soundName,
						minVolume = defaultValues.minVolumeSound,
						maxVolume = defaultValues.maxVolumeSound,
						minPause = defaultValues.minPauseSound,
						maxPause = defaultValues.maxPauseSound
					))
				}
			}
			is SoundEvent.ImportSound -> {
				event.soundStorageManager.moveFileToSoundsFolder()
			}
			is SoundEvent.DeleteSound -> {
				event.soundStorageManager.deleteFileFromSoundsDir(event.soundName)
				_state.value = SoundState(event.soundStorageManager.getSoundFileNames())
			}
		}
	}
}