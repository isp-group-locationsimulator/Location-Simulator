package com.ispgr5.locationsimulator.presentation.settings



/**
 * The State of the Settings Screen.
 */
data class SettingsState(
    var minPauseSound : Int = 0,
    var maxPauseSound : Int = 1000,
    var minVolumeSound : Float = 0f,
    var maxVolumeSound : Float = 1f,

    var minPauseVibration : Int = 0,
    var maxPauseVibration : Int = 1000,
    var minStrengthVibration : Int = 0,
    var maxStrengthVibration : Int = 255,
    var minDurationVibration : Int = 0,
    var maxDurationVibration : Int = 1000,
    var defaultNameVibration : String = "Vibration"
)