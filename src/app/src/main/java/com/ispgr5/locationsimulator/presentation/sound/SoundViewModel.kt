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
	private val soundPlayer = SoundPlayer { _isPlaying.value = "" }

	// Set to the name of the sound that currently plays
	private val _isPlaying = mutableStateOf("")
	val isPlaying: State<String> = _isPlaying

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
				_isPlaying.value = event.soundName
			}
			is SoundEvent.StopPlayback -> {
				stopSound()
			}
			is SoundEvent.SelectSound -> {
				stopSound()
				val defaultValues = event.getDefaultValues()
				savedStateHandle.get<Int>("configurationId")?.let { configurationId ->
					event.navController.navigate(Screen.EditTimelineScreen.createRoute(
						configurationId = configurationId,
						soundNameToAdd = event.soundName,
						minVolume = defaultValues.minVolumeSound,
						maxVolume = defaultValues.maxVolumeSound,
						minPause = defaultValues.minPauseSound,
						maxPause = defaultValues.maxPauseSound
					)){
						popUpTo(Screen.SoundScreen.route){ inclusive = true }
					}
				}
			}
			is SoundEvent.ImportSound -> {
				stopSound()
				event.soundStorageManager.moveFileToSoundsFolder()
			}
			is SoundEvent.DeleteSound -> {
				stopSound()
				event.soundStorageManager.deleteFileFromSoundsDir(event.soundName)
				_state.value = SoundState(event.soundStorageManager.getSoundFileNames())
			}
		}
	}

	private fun stopSound(){
		soundPlayer.stopPlayback()
		_isPlaying.value = ""
	}
}