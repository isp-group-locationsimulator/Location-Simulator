package com.ispgr5.locationsimulator.presentation.select

import com.ispgr5.locationsimulator.domain.model.Configuration

/**
 * the UI Events the View can call
 */
sealed class SelectEvent {
    data class DeleteConfiguration(val configuration: Configuration) : SelectEvent()
    data class SelectedConfiguration(val configuration: Configuration) : SelectEvent()
    data class ToggledConfiguration(val configuration: Configuration) : SelectEvent()
}