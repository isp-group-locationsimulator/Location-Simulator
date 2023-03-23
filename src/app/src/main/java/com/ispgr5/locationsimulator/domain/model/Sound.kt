package com.ispgr5.locationsimulator.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class to Store one Sound of the Pattern
 */
@Serializable
//The name for json. for example{"comp_type":"Sound","source":""}
@SerialName("Sound")
class Sound(
	override val id: Int,
	val source: String,
	//volume in 0f..1f
	val minVolume: Float,
	val maxVolume: Float,
	//pause in ms
	val minPause: Int,
	val maxPause: Int,
	//TODO deprecated
	val isRandom: Boolean
) : ConfigComponent() {

	override fun copy(): Sound {
		return Sound(id, source, minVolume, maxVolume, minPause, maxPause, isRandom)
	}

	/**
	 * returns a copy of the Sound Object. You can pass default parameters.
	 * If no parameters passed this function handles like override fun copy()
	 */
	fun myCopy(
		id: Int = this.id,
		source: String = this.source,
		minVolume: Float = this.minVolume,
		maxVolume: Float = this.maxVolume,
		minPause: Int = this.minPause,
		maxPause: Int = this.maxPause,
		isRandom: Boolean = this.isRandom
	): Sound {
		return Sound(id, source, minVolume, maxVolume, minPause, maxPause, isRandom)
	}
}