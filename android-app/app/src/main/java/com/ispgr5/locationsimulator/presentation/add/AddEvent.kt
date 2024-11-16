package com.ispgr5.locationsimulator.presentation.add

import com.ispgr5.locationsimulator.data.storageManager.ConfigurationStorageManager
import com.ispgr5.locationsimulator.presentation.settings.SettingsState

/**
 * The UI Events the View can call
 */
sealed class AddEvent {
	data class EnteredName(val name: String) : AddEvent()
	data class EnteredDescription(val description: String) : AddEvent()
	data class SelectedImportConfiguration(val configurationStorageManager: ConfigurationStorageManager) : AddEvent()
	data class SaveConfiguration(val getDefaultValues: () -> SettingsState) : AddEvent()
}