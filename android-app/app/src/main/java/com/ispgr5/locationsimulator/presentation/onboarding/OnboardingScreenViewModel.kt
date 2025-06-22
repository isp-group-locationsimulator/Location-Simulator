package com.ispgr5.locationsimulator.presentation.onboarding

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.ispgr5.locationsimulator.data.preferences.PREF_NAME
import com.ispgr5.locationsimulator.data.preferences.PreferencesKeys
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingScreenViewModel @Inject constructor() : ViewModel() {

    private var vibrationTested: Boolean = false
    private var batteryOptimizationSet: Boolean = false

    @Suppress("unused")
    fun isReady(): Boolean = vibrationTested && batteryOptimizationSet

    fun onEvent(event: OnboardingScreenEvent) {
        when (event) {
            is OnboardingScreenEvent.GrantedBatteryOptimization -> {
                batteryOptimizationSet = true
            }
            is OnboardingScreenEvent.ChangedAppTheme -> {
                event.activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    .edit {
                        putString(PreferencesKeys.THEME_TYPE.name, event.themeState.themeType.name)
                        putBoolean(
                            PreferencesKeys.DYNAMIC_COLORS.name,
                            event.themeState.useDynamicColor
                        )
                    }
            }
        }
    }

}