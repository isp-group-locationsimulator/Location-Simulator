package com.ispgr5.locationsimulator.presentation.run

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * The ViewModel for the Run Screen
 */
@HiltViewModel
class RunViewModel @Inject constructor() : ViewModel() {

    /**
     * Handles UI Events
     */
    fun onEvent(event: RunEvent) {
        when (event) {
            is RunEvent.StopClicked -> {
                event.stopServiceFunction()
            }
        }
    }
}