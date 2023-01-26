package com.ispgr5.locationsimulator.presentation.editTimeline

import com.ispgr5.locationsimulator.domain.model.ConfigComponent

data class EditTimelineState(
    val name: String = "",
    val description: String = "",
    val components: List<ConfigComponent> = emptyList(),
    val current: ConfigComponent? = null,
    val volumeMin : Float = 10f,
    val volumeMax : Float = 20f,
    val pauseMin : Float = 10f,
    val pauseMax : Float = 20f
)