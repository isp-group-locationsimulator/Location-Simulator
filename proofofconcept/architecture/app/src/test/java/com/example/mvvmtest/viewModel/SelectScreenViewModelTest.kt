package com.example.mvvmtest.viewModel

import androidx.navigation.compose.rememberNavController
import junit.framework.TestCase

class SelectScreenViewModelTest : TestCase() {

    lateinit var selectScreenViewModel: SelectScreenViewModel

    public override fun setUp() {
        super.setUp()
        selectScreenViewModel = MainViewModel().getSelectScreenViewModel()
    }

    fun testOnDurationVibrationChanges() {
        //normal input
        selectScreenViewModel.onDurationVibrationChanges("45")
        assertEquals(45,selectScreenViewModel.currentConfiguration.durationVibrateInSec.value)
        //empty input
        selectScreenViewModel.onDurationVibrationChanges("")
        assertEquals(0,selectScreenViewModel.currentConfiguration.durationVibrateInSec.value)
        //normal input after empty field
        selectScreenViewModel.onDurationVibrationChanges("")
        selectScreenViewModel.onDurationVibrationChanges("8")
        assertEquals(8, selectScreenViewModel.currentConfiguration.durationVibrateInSec.value)
    }
}