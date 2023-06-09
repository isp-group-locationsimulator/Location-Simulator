package com.ispgr5.locationsimulator.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This class is the superclass of Vibration and Sound.
 * just here to create List<ConfigComponent>
 */
@Serializable
abstract class ConfigComponent {
	@SerialName("id") abstract val id: Int
	abstract fun copy(): ConfigComponent
}