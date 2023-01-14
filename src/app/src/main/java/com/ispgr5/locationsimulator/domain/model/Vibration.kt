package com.ispgr5.locationsimulator.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * data class to Store one Vibration of the Pattern
 */
@Serializable
//The name for json. for example{"comp_type":"Vibration","minStrength":"4"}
@SerialName("Vibration")
data class Vibration(
    val minStrength: Int,
    val maxStrength: Int,
    val minPause: Int,
    val maxPause: Int,
) : ConfigComponent()