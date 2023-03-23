package com.ispgr5.locationsimulator.presentation.editTimeline

import com.ispgr5.locationsimulator.domain.model.ConfigComponent

data class EditTimelineState(
	val name: String = "",
	val description: String = "",
	val randomOrderPlayback: Boolean = false,
	val components: List<ConfigComponent> = emptyList(),
	val current: ConfigComponent? = null
)