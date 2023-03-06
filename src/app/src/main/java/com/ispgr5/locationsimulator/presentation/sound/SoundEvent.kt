package com.ispgr5.locationsimulator.presentation.sound

import androidx.navigation.NavController
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager

/**
 * The UI Events the View can call
 */
sealed class SoundEvent {
    data class RefreshPage(val soundStorageManager: SoundStorageManager) : SoundEvent()
    data class TestPlaySound(val privateDirUri: String, val soundName: String) : SoundEvent()
    data class SelectSound(val soundName: String, val navController: NavController) : SoundEvent()
    data class ImportSound(val soundStorageManager: SoundStorageManager): SoundEvent()
}
