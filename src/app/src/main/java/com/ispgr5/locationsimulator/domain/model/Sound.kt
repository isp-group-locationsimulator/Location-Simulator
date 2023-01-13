package com.ispgr5.locationsimulator.domain.model

/**
 * data class to Store one Sound of the Pattern
 */
data class Sound(
    val source: String,
    val minVolume: Int,
    val maxVolume: Int,
    val minPause: Int,
    val maxPause: Int,
    val isRandom: Boolean
): Config
