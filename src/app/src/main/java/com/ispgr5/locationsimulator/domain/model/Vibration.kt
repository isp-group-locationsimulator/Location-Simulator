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
	override val id: Int,
	val minStrength: Int,
	val maxStrength: Int,
	val minPause: Int,
	val maxPause: Int,
	val minDuration: Int,
	val maxDuration: Int
) : ConfigComponent() {
	override fun copy(): Vibration {
		return Vibration(id, minStrength, maxStrength, minPause, maxPause, minDuration, maxDuration)
	}

	/**
	 * returns a copy of the Sound Object. You can pass default parameters.
	 * If no parameters passed this function handles like override fun copy()
	 */
	fun myCopy(
		id: Int = this.id,
		minStrength: Int = this.minStrength,
		maxStrength: Int = this.maxStrength,
		minPause: Int = this.minPause,
		maxPause: Int = this.maxPause,
		minDuration: Int = this.minDuration,
		maxDuration: Int = this.maxDuration
	): Vibration {
		return Vibration(id, minStrength, maxStrength, minPause, maxPause, minDuration, maxDuration)
	}
}