package com.ispgr5.locationsimulator.presentation.delay

/**
 * the UI Events the View can call
 */
sealed class DelayEvent {
    data class StartClicked(val startServiceFunction : () -> Unit) : DelayEvent()
}