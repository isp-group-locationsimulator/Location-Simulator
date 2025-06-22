package com.ispgr5.locationsimulator.presentation.onboarding

import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.ui.theme.ThemeState

sealed class OnboardingScreenEvent {
    data class ChangedAppTheme(val activity: MainActivity, val themeState: ThemeState): OnboardingScreenEvent()
    data object GrantedBatteryOptimization: OnboardingScreenEvent()
}