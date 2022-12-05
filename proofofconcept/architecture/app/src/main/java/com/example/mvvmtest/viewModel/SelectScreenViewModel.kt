package com.example.mvvmtest.viewModel

import androidx.navigation.NavController
import com.example.mvvmtest.model.Configuration

/**
 * The ViewModel for the Select Screen
 */
class SelectScreenViewModel(val currentConfiguration: Configuration) {

    fun onDurationVibrationChanges(number: String) {
        try {
            currentConfiguration.durationVibrateInSec.value = StringInputToInt(
                number = number,
                currentValue = currentConfiguration.durationVibrateInSec.value
            )
        } catch (e: Exception) {
            println("cast from String to Int failed. Maybe the number is too large")
            println(e)
        }
    }

    fun onDurationPauseVibrationChanges(number: String) {
        try {
            currentConfiguration.durationPauseVibrateInSec.value = StringInputToInt(
                number = number,
                currentValue = currentConfiguration.durationPauseVibrateInSec.value
            )
        } catch (e: Exception) {
            println("cast from String to Int failed. Maybe the number is too large")
            println(e)
        }
    }

    fun onStartPressed(){
        //currentConfiguration.durationVibrateInSec.value = 999
    }

    private fun StringInputToInt(number: String, currentValue: Int): Int {
        if (number.equals("")) {
            return 0
        } else if (currentValue == 0) {
            return number.replace("0", "").toInt()
        } else {
            return number.toInt()
        }
    }
}