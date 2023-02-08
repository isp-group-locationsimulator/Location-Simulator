package com.ispgr5.locationsimulator.presentation.homescreen

sealed class HomeScreenEvent {
    object SelectConfiguration: HomeScreenEvent()
    class DisableBatteryOptimization(val batteryOptDisableFunction : () -> Unit) : HomeScreenEvent()

}
