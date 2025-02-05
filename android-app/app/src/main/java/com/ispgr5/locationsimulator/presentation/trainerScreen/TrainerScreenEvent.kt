package com.ispgr5.locationsimulator.presentation.trainerScreen

import com.ispgr5.locationsimulator.presentation.homescreen.HomeScreenEvent

sealed class TrainerScreenEvent {
    data class OptionSelected(val deviceName: String) : TrainerScreenEvent()
    object StartTraining : TrainerScreenEvent()
    data class ToggleInputFields(val show: Boolean) : TrainerScreenEvent()
}

