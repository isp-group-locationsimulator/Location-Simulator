package com.ispgr5.locationsimulator.presentation.Add

import com.ispgr5.locationsimulator.data.storageManager.ConfigurationStorageManager

/**
 * The UI Events the View can call
 */
sealed class AddEvent {
	data class EnteredName(val name: String) : AddEvent()
	data class EnteredDescription(val description: String) : AddEvent()
	data class SelectedImportConfiguration(val configurationStorageManager: ConfigurationStorageManager) :
		AddEvent()

	object SaveConfiguration : AddEvent()
}