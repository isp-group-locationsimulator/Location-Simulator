package com.example.mvvmtest.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * This class contains only the data for a Configuration
 * all types a mutable so the composables can observe them
 */
data class Configuration(
    //The values are default values, shown when app starts
    val durationVibrateInSec: MutableState<Int> = mutableStateOf(4),
    var durationPauseVibrateInSec: MutableState<Int> = mutableStateOf(1)
)