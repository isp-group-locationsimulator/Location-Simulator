package com.ispgr5.locationsimulator.domain.model

/**
 * data class to Store one Vibration of the Pattern
 */
data class Vibration(
    val minStrength: Int,
    val maxStrength: Int,
    val minPause: Int,
    val maxPause: Int,
): Config