package com.example.mvvmtest.viewModel

import androidx.lifecycle.ViewModel
import com.example.mvvmtest.model.Configuration


class MainViewModel : ViewModel() {

    val currentConfiguration = Configuration()


    fun onDurationVibrationChanges(number: String) {
        try {
            if (number.equals("")) {
                currentConfiguration.durationVibrateInSec.value = 0
            } else if (currentConfiguration.durationVibrateInSec.value == 0) {
                currentConfiguration.durationVibrateInSec.value = number.replace("0", "").toInt()
            } else {
                currentConfiguration.durationVibrateInSec.value = number.toInt()
            }
            currentConfiguration.durationVibrateInSec.value = number.toInt()
        } catch (e: Exception) {
            println("cast from String to Int failed. Maybe the number is too large")
            println(e)
        }
    }

    fun onDurationPauseVibrationChanges(numberValue: Int) {
        currentConfiguration.durationPauseVibrateInSec = numberValue
    }
}