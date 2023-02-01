package com.ispgr5.locationsimulator.presentation.sound

import com.ispgr5.locationsimulator.presentation.MainActivity

/**
 * The UI Events the View can call
 */
sealed class SoundEvent {
    object RefreshPage : SoundEvent()
    data class TestPlaySound(val mainActivity: MainActivity, val soundName: String) : SoundEvent()
    data class SelectSound(val soundName: String) : SoundEvent()
    object ImportSound: SoundEvent()
}
