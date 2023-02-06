package com.ispgr5.locationsimulator.presentation.select

import com.ispgr5.locationsimulator.data.storageManager.ConfigurationStorageManager
import com.ispgr5.locationsimulator.domain.model.Configuration

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
        val configurationStorageManager: ConfigurationStorageManager
    ) : SelectEvent()
}