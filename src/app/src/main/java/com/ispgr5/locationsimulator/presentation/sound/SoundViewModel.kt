package com.ispgr5.locationsimulator.presentation.sound

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.model.Sound
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.presentation.run.SoundPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
                soundPlayer.startSound(event.privateDirUri + "/" + event.soundName, 1f)
            }
            is SoundEvent.StopPlayback -> {
                soundPlayer.stopPlayback()
            }
            is SoundEvent.SelectSound -> {
                savedStateHandle.get<Int>("configurationId")?.let { configurationId ->
                    viewModelScope.launch {
                        configurationUseCases.getConfiguration(configurationId)?.also { configuration ->
                            val componentsCopy = configuration.components.toMutableList()
                            componentsCopy.add(Sound(event.soundName, 1f, 1f, 3, 7, false))
                            configurationUseCases.addConfiguration(
                                Configuration(
                                    id = configurationId,
                                    name = configuration.name,
                                    description = configuration.description,
                                    randomOrderPlayback = configuration.randomOrderPlayback,
                                    components = componentsCopy
                                )
                            )
                        }
                    }
                    event.navController.navigate("editTimeline?configurationId=${configurationId}")
                }
            }
            is SoundEvent.ImportSound -> {
                event.soundStorageManager.moveFileToPrivateFolder()
            }
            is SoundEvent.DeleteSound -> {
                event.soundStorageManager.deleteFileFromPrivateDir(event.soundName)
                _state.value = SoundState(event.soundStorageManager.getSoundFileNames())
            }
        }
    }
}