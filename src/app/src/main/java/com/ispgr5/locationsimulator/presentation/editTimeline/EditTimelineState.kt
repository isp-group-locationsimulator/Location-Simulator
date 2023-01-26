package com.ispgr5.locationsimulator.presentation.editTimeline

import com.ispgr5.locationsimulator.domain.model.ConfigComponent

data class EditTimelineState(
    val name: String = "",
    val description: String = "",
    val components: List<ConfigComponent> = emptyList(),
    val current: ConfigComponent? = null,

    //both
    val pauseMin : Float = 10f,
    val pauseMax : Float = 20f,

    //sound
    val volumeMin : Float = 10f,
    val volumeMax : Float = 20f,

    //vibration
    val strengthMin : Float = 10f,
    val strengthMax : Float = 40f,
    val durationMin : Float = 30f,
    val durationMax : Float = 50f,

    )