package com.ispgr5.locationsimulator.presentation.select

import com.ispgr5.locationsimulator.domain.model.Configuration

/**
 * The State of the Select Screen.
 * A list of Configurations, that is empty by default
 */
data class SelectScreenState(
    val configurations: List<Configuration> = emptyList(),
    val toggledConfiguration : Configuration? = null
)