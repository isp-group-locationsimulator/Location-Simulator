package com.ispgr5.locationsimulator.presentation.delay

import com.ispgr5.locationsimulator.domain.model.Configuration

/**
 * The State of the Delay Screen
 * The Selected Configuration
 */
data class DelayScreenState(
    val configuration: Configuration? = null,
    val remoteIpAddress: String? = null
)