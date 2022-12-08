package com.example.mvvmtest.model.memory

import com.example.mvvmtest.model.Configuration


class MemoryInterface {

    fun getAllConfigrurationFromMemory(): Array<Configuration>{
        return arrayOf(
            Configuration(durationVibrateInSec = 999, durationPauseVibrateInSec = 999),
            Configuration(durationVibrateInSec = 1, durationPauseVibrateInSec = 1),
            Configuration(durationVibrateInSec = 50, durationPauseVibrateInSec = 30)
        )
    }
}