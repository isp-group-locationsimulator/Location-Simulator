package com.ispgr5.locationsimulator.presentation.edit

import com.ispgr5.locationsimulator.domain.model.ConfigComponent

/**
 * The State of the Edit Screen.
 * The Configuration Entry's with default Values
 */
data class EditScreenState(
    val name: String = "",
    val description: String = "",
    val components: List<ConfigComponent> = emptyList()
)