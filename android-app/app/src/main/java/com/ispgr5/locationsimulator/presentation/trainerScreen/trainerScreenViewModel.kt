package com.ispgr5.locationsimulator.presentation.trainerScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ispgr5.locationsimulator.presentation.homescreen.HomeScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrainerScreenViewModel @Inject constructor() : ViewModel() {
    var state = mutableStateOf(TrainerScreenState())
        private set

    fun onEvent(event: TrainerScreenEvent) {
        when (event) {
            is TrainerScreenEvent.OptionSelected -> {
                println("Selected device: ${event.deviceName}") // Später durch echte Logik ersetzen
            }
            is TrainerScreenEvent.StartTraining -> {
                println("Training started") // Später durch echte Logik ersetzen
            }
            is TrainerScreenEvent.ToggleInputFields ->{

                state.value = state.value.copy(
                    showInputFields = event.show)
            }
        }
    }
}
