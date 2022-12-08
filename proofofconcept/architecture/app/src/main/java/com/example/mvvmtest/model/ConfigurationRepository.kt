package com.example.mvvmtest.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


class ConfigurationRepository {
    private var configuration: MutableState<Configuration> = mutableStateOf(Configuration(4, 1))

    fun replaceEntry(
        durationVibrateInSec: Int? = null,
        durationPauseVibrateInSec: Int? = null
    ) {
        configuration = mutableStateOf(
            Configuration(
                durationVibrateInSec = durationVibrateInSec ?: this.configuration.value.durationVibrateInSec,
                durationPauseVibrateInSec = durationPauseVibrateInSec ?: this.configuration.value.durationPauseVibrateInSec
            )
        )
    }

    fun replaceConfiguration(configuration: Configuration) {
        this.configuration = mutableStateOf(configuration)
    }

    fun returnConfiguration():Configuration {
        return this.configuration.value
    }


/*
    var durationVibrateInSec: MutableState<Int> = mutableStateOf(4)
    var durationPauseVibrateInSec: MutableState<Int> = mutableStateOf(1)

    fun addEntry(
        durationVibrateInSec: Int? = null,
        durationPauseVibrateInSec: Int? = null
    ){
        if (durationVibrateInSec != null){
            this.durationVibrateInSec = mutableStateOf(durationVibrateInSec)
        }
        if (durationPauseVibrateInSec != null){
            this.durationPauseVibrateInSec = mutableStateOf(durationPauseVibrateInSec)
        }

    }

    fun replaceConfiguration(configuration: Configuration){
        this.durationVibrateInSec = mutableStateOf(configuration.durationVibrateInSec)
        this.durationPauseVibrateInSec = mutableStateOf(configuration.durationPauseVibrateInSec)
    }

    fun returnConfiguration() = Configuration(durationVibrateInSec = this.durationVibrateInSec.value, durationPauseVibrateInSec = this.durationPauseVibrateInSec.value)

 */
}