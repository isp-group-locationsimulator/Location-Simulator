package com.ispgr5.locationsimulator.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Data class to Store one Vibration of the Pattern
 */
@Serializable
//The name for json. for example{"comp_type":"Vibration","minStrength":"4"}
@SerialName("Vibration")
data class Vibration(
    var minStrength: Int,
    var maxStrength: Int,
    var minPause: Int,
    var maxPause: Int,
    var minDuration: Int,
    var maxDuration: Int
) : ConfigComponent(){
    override fun copy() :Vibration{
        return Vibration(minStrength,maxStrength,minPause,maxPause,minDuration,maxDuration)
    }
}