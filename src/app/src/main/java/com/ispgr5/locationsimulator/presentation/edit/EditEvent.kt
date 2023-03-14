package com.ispgr5.locationsimulator.presentation.edit

import com.ispgr5.locationsimulator.data.storageManager.ConfigurationStorageManager

/**
 * The UI Events the View can call
 */
sealed class EditEvent {
	data class EnteredName(val name: String) : EditEvent()
	data class EnteredDescription(val description: String) : EditEvent()
	data class SelectedImportConfiguration(val configurationStorageManager: ConfigurationStorageManager) :
		EditEvent()

	object SaveConfiguration : EditEvent()
}