package com.ispgr5.locationsimulator.presentation.trainerScreen

sealed class TrainerScreenEvent {
    data object StartTraining : TrainerScreenEvent()
    data object StopTraining : TrainerScreenEvent()
    data class StartDeviceTraining(val device: Device): TrainerScreenEvent()
    data class StopDeviceTraining(val device: Device): TrainerScreenEvent()
    data class ToggleInputFields(val show: Boolean) : TrainerScreenEvent()
}

