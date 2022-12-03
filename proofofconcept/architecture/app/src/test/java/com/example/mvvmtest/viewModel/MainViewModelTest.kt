package com.example.mvvmtest.viewModel

import junit.framework.TestCase
import org.junit.Test

class MainViewModelTest : TestCase() {

    lateinit var mainViewModel: MainViewModel

    public override fun setUp() {
        super.setUp()
        mainViewModel = MainViewModel()
    }

    fun testGetCurrentConfiguration() {}

    @Test
    fun testOnDurationVibrationChanges() {
        mainViewModel.onDurationVibrationChanges("45");
        assertEquals(45,mainViewModel.currentConfiguration.durationVibrateInSec.value)
        mainViewModel.onDurationVibrationChanges("");
        assertEquals(0,mainViewModel.currentConfiguration.durationVibrateInSec.value)
    }

    fun testOnDurationPauseVibrationChanges() {}
}