package com.ispgr5.locationsimulator.presentation.homescreen

import androidx.compose.runtime.MutableState
import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.ui.theme.ThemeState

sealed class HomeScreenEvent {
	object SelectConfiguration : HomeScreenEvent()
	class DisableBatteryOptimization(val batteryOptDisableFunction: () -> Unit) : HomeScreenEvent()

	data class ChangedAppTheme(val isDarkTheme: Boolean, val activity: MainActivity, val darkTheme: MutableState<ThemeState>) : HomeScreenEvent()

}
