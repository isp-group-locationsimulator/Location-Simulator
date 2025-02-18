package com.ispgr5.locationsimulator.presentation.trainerScreen

data class TrainerScreenState(
    val devices: List<Device> = emptyList(),
    val showInputFields: Boolean = true,
    val isPlayingMap: Map<String, Boolean> = emptyMap()
)


data class Device(
    val user: String,
    val name: String,
    val isConnected: Boolean
)