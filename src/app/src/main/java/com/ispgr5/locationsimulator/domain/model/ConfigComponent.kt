package com.ispgr5.locationsimulator.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This class is the superclass of Vibration and Sound.
 * just here to create List<ConfigComponent>
 */
@Serializable
sealed class ConfigComponent {
    @SerialName("id")
    abstract val id: Int
    abstract fun copy(): ConfigComponent

    /**
     * Data class to Store one Vibration of the Pattern
     */
    @Serializable
    //The name for json. for example{"comp_type":"Vibration","minStrength":"4"}
    @SerialName("Vibration")
    data class Vibration(
        override val id: Int,
        val name: String,
        val minStrength: Int,
        val maxStrength: Int,
        //in ms
        val minPause: Int,
        val maxPause: Int,
        //in ms
        val minDuration: Int,
        val maxDuration: Int
    ) : ConfigComponent() {

        /**
         * simple copy function to copy a Vibration
         */
        override fun copy(): Vibration {
            return Vibration(id, name, minStrength, maxStrength, minPause, maxPause, minDuration, maxDuration)
        }

        /**
         * returns a copy of the Sound Object. You can pass default parameters.
         * If no parameters passed this function handles like override fun copy()
         */
        fun myCopy(
            id: Int = this.id,
            name: String = this.name,
            minStrength: Int = this.minStrength,
            maxStrength: Int = this.maxStrength,
            minPause: Int = this.minPause,
            maxPause: Int = this.maxPause,
            minDuration: Int = this.minDuration,
            maxDuration: Int = this.maxDuration
        ): Vibration {
            return Vibration(id, name, minStrength, maxStrength, minPause, maxPause, minDuration, maxDuration)
        }
    }

    /**
     * Data class to Store one Sound of the Pattern
     */
    @Serializable
    //The name for json. for example{"comp_type":"Sound","source":""}
    @SerialName("Sound")
    class Sound(
        override val id: Int,
        val source: String,
        val name : String,
        //volume in 0f..1f
        val minVolume: Float,
        val maxVolume: Float,
        //pause in ms
        val minPause: Int,
        val maxPause: Int
    ) : ConfigComponent() {

        /**
         * simple copy function to copy a Sound
         */
        override fun copy(): Sound {
            return Sound(id, source, name, minVolume, maxVolume, minPause, maxPause)
        }

        /**
         * returns a copy of the Sound Object. You can pass default parameters.
         * If no parameters passed this function handles like override fun copy()
         */
        fun myCopy(
            id: Int = this.id,
            source: String = this.source,
            name: String = this.name,
            minVolume: Float = this.minVolume,
            maxVolume: Float = this.maxVolume,
            minPause: Int = this.minPause,
            maxPause: Int = this.maxPause
        ): Sound {
            return Sound(id, source, name, minVolume, maxVolume, minPause, maxPause)
        }
    }
}