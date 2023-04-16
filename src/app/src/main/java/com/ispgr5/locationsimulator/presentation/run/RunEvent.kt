package com.ispgr5.locationsimulator.presentation.run

/**
 * The UI Events the View can call
 */
sealed class RunEvent {
	data class StopClicked(val stopServiceFunction: () -> Unit) : RunEvent()
}