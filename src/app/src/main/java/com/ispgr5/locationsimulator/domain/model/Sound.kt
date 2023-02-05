package com.ispgr5.locationsimulator.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class to Store one Sound of the Pattern
 */
@Serializable
//The name for json. for example{"comp_type":"Sound","source":""}
@SerialName("Sound")
data class Sound(
    val source: String,
    //volume in 0..255
    var minVolume: Int,
    var maxVolume: Int,
    //pause in ms
    var minPause: Int,
    var maxPause: Int,
    //TODO deprecated
    val isRandom: Boolean
) : ConfigComponent() {

    override fun copy(): Sound {
        return Sound(source, minVolume, maxVolume, minPause, maxPause, isRandom)
    }

    /**
     * returns a copy of the Sound Object. You can pass default parameters.
     * If no parameters passed this function handles like override fun copy()
     */
    fun myCopy(
        source: String = this.source,
        minVolume: Int = this.minVolume,
        maxVolume: Int = this.maxVolume,
        minPause: Int = this.minPause,
        maxPause: Int = this.maxPause,
        isRandom: Boolean = this.isRandom
    ): Sound {
        return Sound(source, minVolume, maxVolume, minPause, maxPause, isRandom)
    }
}