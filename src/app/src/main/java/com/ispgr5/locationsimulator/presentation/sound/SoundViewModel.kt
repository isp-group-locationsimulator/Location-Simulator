package com.ispgr5.locationsimulator.presentation.sound

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.ispgr5.locationsimulator.FilePicker
import com.ispgr5.locationsimulator.presentation.run.SoundPlayer

class SoundViewModel(private val filePicker: FilePicker) {
    // The provided state for the View
    private val _state = mutableStateOf(SoundState(filePicker.getSoundFileNames()))
    val state: State<SoundState> = _state

    fun onEvent(event: SoundEvent) {
        when(event) {
            is SoundEvent.RefreshPage -> {
                _state.value = SoundState(filePicker.getSoundFileNames())
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
                filePicker.moveFileToPrivateFolder()
            }
        }
    }
}