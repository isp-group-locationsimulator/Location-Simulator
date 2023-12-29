package com.ispgr5.locationsimulator.presentation.settings



/**
 * The State of the Settings Screen.
 */
data class SettingsState(
    var minPauseSound : Int = DefaultShippingSettings.MIN_PAUSE_SOUND,
    var maxPauseSound : Int = DefaultShippingSettings.MAX_PAUSE_SOUND,
    var minVolumeSound : Float = DefaultShippingSettings.MIN_VOLUME_SOUND,
    var maxVolumeSound : Float = DefaultShippingSettings.MAX_VOLUME_SOUND,
    var minPauseVibration : Int = DefaultShippingSettings.MIN_PAUSE_VIBRATION,
    var maxPauseVibration : Int = DefaultShippingSettings.MAX_PAUSE_VIBRATION,
    var minStrengthVibration : Int = DefaultShippingSettings.MIN_STRENGTH_VIBRATION,
    var maxStrengthVibration : Int = DefaultShippingSettings.MAX_STRENGTH_VIBRATION,
    var minDurationVibration : Int = DefaultShippingSettings.MIN_DURATION_VIBRATION,
    var maxDurationVibration : Int = DefaultShippingSettings.MAX_DURATION_VIBRATION,
    var defaultNameVibration : String = DefaultShippingSettings.DEFAULT_NAME_VIBRATION
)

object DefaultShippingSettings {
    const val MIN_PAUSE_SOUND: Int = 0
    const val MAX_PAUSE_SOUND: Int = 1000
    const val MIN_VOLUME_SOUND: Float = 0f
    const val MAX_VOLUME_SOUND: Float = 1f
    const val MIN_PAUSE_VIBRATION: Int = 0
    const val MAX_PAUSE_VIBRATION: Int = 1000
    const val MIN_STRENGTH_VIBRATION: Int = 0
    const val MAX_STRENGTH_VIBRATION: Int = 255
    const val MIN_DURATION_VIBRATION: Int = 0
    const val MAX_DURATION_VIBRATION: Int = 1000
    const val DEFAULT_NAME_VIBRATION = "Vibration"
}