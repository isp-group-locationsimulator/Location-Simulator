package com.ispgr5.locationsimulator.presentation.edit

/**
 * The State of the Edit Screen.
 * The Configuration Entry's with default Values
 */
data class EditScreenState(
    val name: String = "",
    val description: String = "",
    val duration: Int = 1,
    val pause: Int = 0
)