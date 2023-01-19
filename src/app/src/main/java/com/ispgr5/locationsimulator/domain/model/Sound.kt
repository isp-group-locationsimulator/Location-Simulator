package com.ispgr5.locationsimulator.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * data class to Store one Sound of the Pattern
 */
@Serializable
//The name for json. for example{"comp_type":"Sound","source":""}
@SerialName("Sound")
data class Sound(
    val source: String,
    val minVolume: Int,
    val maxVolume: Int,
    val minPause: Int,
    val maxPause: Int,
    val isRandom: Boolean
) : ConfigComponent()