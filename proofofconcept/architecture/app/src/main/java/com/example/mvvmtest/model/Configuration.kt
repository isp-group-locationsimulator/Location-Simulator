package com.example.mvvmtest.model

/**
 * This class contains only the data for a Configuration
 * all types a mutable so the composables can observe them
 */
data class Configuration(
    //The values are default values, shown when app starts
    var durationVibrateInSec: Int,
    var durationPauseVibrateInSec: Int
)