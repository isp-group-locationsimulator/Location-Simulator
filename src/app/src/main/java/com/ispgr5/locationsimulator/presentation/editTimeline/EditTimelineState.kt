package com.ispgr5.locationsimulator.presentation.editTimeline

import com.ispgr5.locationsimulator.domain.model.ConfigComponent

data class EditTimelineState(
    val name: String = "",
    val description: String = "",
    val components: List<ConfigComponent> = emptyList(),
    val currentTimelineIndex: Int = 0,
    val current: ConfigComponent? = null
    //both


    )