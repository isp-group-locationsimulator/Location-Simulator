package com.ispgr5.locationsimulator.presentation.sound

import androidx.navigation.NavController
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.presentation.settings.SettingsState

/**
 * The UI Events the View can call
 */
sealed class SoundEvent {
	data class RefreshPage(val soundStorageManager: SoundStorageManager) : SoundEvent()
	data class TestPlaySound(val soundsDirUri: String, val soundName: String) : SoundEvent()
	object StopPlayback : SoundEvent()
	data class SelectSound(val soundName: String, val navController: NavController, val getDefaultValues : () -> SettingsState) : SoundEvent()
	data class DeleteSound(val soundName: String, val soundStorageManager: SoundStorageManager) :
		SoundEvent()

	data class ImportSound(val soundStorageManager: SoundStorageManager) : SoundEvent()
}
