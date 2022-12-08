package com.example.mvvmtest.viewModel.selectScreen

import com.example.mvvmtest.viewModel.MainViewModel


/**
 * The ViewModel for the Select Screen
 */
class SelectScreenViewModel(val mainViewModel: MainViewModel) {

    fun onDurationVibrationChanges(number: String) {
        try {
            mainViewModel.replaceStateConfigurationEntry(
                durationVibrateInSec = StringInputToInt(
                    number = number,
                    currentValue = mainViewModel.state.value.configuration.durationVibrateInSec
                )
            )
        } catch (e: Exception) {
            println("cast from String to Int failed. Maybe the number is too large")
            println(e)
        }
    }

    fun onDurationPauseVibrationChanges(number: String) {
        try {
            mainViewModel.replaceStateConfigurationEntry(
                durationPauseVibrateInSec = StringInputToInt(
                    number = number,
                    currentValue = mainViewModel.state.value.configuration.durationPauseVibrateInSec
                )
            )
        } catch (e: Exception) {
            println("cast from String to Int failed. Maybe the number is too large")
            println(e)
        }
    }

    fun onStartPressed() {
        //mainViewModel.replaceStateConfigurationEntry(
        //    durationPauseVibrateInSec = 999
        //)
    }

    fun onNextPrevPressed(direction: Int) {
        mainViewModel.changeSelectedConfiguration(direction = direction)
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