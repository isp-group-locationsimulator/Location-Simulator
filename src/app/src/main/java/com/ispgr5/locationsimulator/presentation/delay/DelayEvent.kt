package com.ispgr5.locationsimulator.presentation.delay

import com.ispgr5.locationsimulator.domain.model.ConfigComponent

/**
 * the UI Events the View can call
 */
sealed class DelayEvent {
    data class StartClicked(val startServiceFunction : (List<ConfigComponent>) -> Unit) : DelayEvent()
}