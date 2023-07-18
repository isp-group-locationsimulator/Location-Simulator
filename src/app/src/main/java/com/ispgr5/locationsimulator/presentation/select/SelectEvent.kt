package com.ispgr5.locationsimulator.presentation.select

import android.content.Context
import com.ispgr5.locationsimulator.data.storageManager.ConfigurationStorageManager
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent

/**
 * The UI Events the View can call
 */
sealed class SelectEvent {
	data class DeleteConfiguration(val configuration: Configuration) : SelectEvent()
	data class SelectedConfiguration(val configuration: Configuration) : SelectEvent()
	data class ToggledConfiguration(val configuration: Configuration) : SelectEvent()
	data class SelectConfigurationForDeletion(val configuration: Configuration) : SelectEvent()
	object SelectDeleteMode : SelectEvent()
	data class SelectedExportConfiguration(
		val configuration: Configuration,
		val configurationStorageManager: ConfigurationStorageManager,
		val context: Context
	) : SelectEvent()

	data class FavoriteClicked(val configuration: Configuration, val makeSnackbar: (SnackbarContent) -> Unit) :
		SelectEvent()
	data class Duplicate(val id:Int?) : SelectEvent()
}