package com.ispgr5.locationsimulator.presentation.sound

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.presentation.run.SoundPlayer

/**
 * The ViewModel for the Sound
 */
class SoundViewModel(private val soundStorageManager: SoundStorageManager) {
    // The provided state for the View
    private val _state = mutableStateOf(SoundState(soundStorageManager.getSoundFileNames()))
    val state: State<SoundState> = _state

    /**
     * Handles UI Events
     */
    fun onEvent(event: SoundEvent) {
        when(event) {
            is SoundEvent.RefreshPage -> {
                _state.value = SoundState(soundStorageManager.getSoundFileNames())
            }
            is SoundEvent.TestPlaySound -> {
                val soundPlayer = SoundPlayer()
                soundPlayer.startSound(event.mainActivity.filesDir.toString()
                                            + "/" + event.soundName)
            }
            is SoundEvent.SelectSound -> {
                println(event.soundName)
            }
            is SoundEvent.ImportSound -> {
                soundStorageManager.moveFileToPrivateFolder()
            }
        }
    }
}