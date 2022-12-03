package com.example.mvvmtest.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


data class Configuration(
    val durationVibrateInSec: MutableState<Int> = mutableStateOf(4),
    var durationPauseVibrateInSec: Int = 1
)