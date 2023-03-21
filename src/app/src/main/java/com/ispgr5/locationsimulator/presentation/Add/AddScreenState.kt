package com.ispgr5.locationsimulator.presentation.Add

import com.ispgr5.locationsimulator.domain.model.ConfigComponent

/**
 * The State of the Edit Screen.
 * The Configuration Entry's with default Values
 */
data class AddScreenState(
	val name: String = "",
	val description: String = "",
	val randomOrderPlayback: Boolean = false,
	val components: List<ConfigComponent> = emptyList()
)