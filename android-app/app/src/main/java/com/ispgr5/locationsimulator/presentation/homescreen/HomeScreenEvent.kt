package com.ispgr5.locationsimulator.presentation.homescreen

import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.ui.theme.ThemeState

/**
 * Events which can happen in the Home Screen
 */
sealed class HomeScreenEvent {
	data object SelectConfiguration : HomeScreenEvent()
	class DisableBatteryOptimization(val batteryOptDisableFunction: () -> Unit) : HomeScreenEvent()
	data class ChangedAppTheme(val activity: MainActivity, val themeState: ThemeState) : HomeScreenEvent()
}
