package com.ispgr5.locationsimulator.presentation.trainerScreen

import com.ispgr5.locationsimulator.domain.model.Configuration

data class TrainerScreenState(
    val showInputFields: Boolean = true,
    val isPlayingMap: Map<String, Boolean> = emptyMap(),
    val defaultConfig: Configuration? = null
)


data class Device(
    val ipAddress: String,
    val user: String,
    var isPlaying: Boolean,
    var isConnected: Boolean,
    var selectedConfig: Configuration? = null
)