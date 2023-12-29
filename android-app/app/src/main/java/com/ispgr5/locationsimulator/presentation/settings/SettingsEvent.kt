package com.ispgr5.locationsimulator.presentation.settings

import androidx.compose.runtime.State


/**
 * The UI Events the View can call
 */
sealed class SettingsEvent {
    data class ChangedSoundVolume(val range: ClosedFloatingPointRange<Float>, val saveDefaultValuesFunction: (state : State<SettingsState>) -> Unit) : SettingsEvent()
    data class ChangedSoundPause(val range: ClosedFloatingPointRange<Float>, val saveDefaultValuesFunction: (state : State<SettingsState>) -> Unit) : SettingsEvent()
    data class ChangedVibStrength(val range: ClosedFloatingPointRange<Float>, val saveDefaultValuesFunction: (state : State<SettingsState>) -> Unit) : SettingsEvent()
    data class ChangedVibDuration(val range: ClosedFloatingPointRange<Float>, val saveDefaultValuesFunction: (state : State<SettingsState>) -> Unit) : SettingsEvent()
    data class ChangedVibPause(val range: ClosedFloatingPointRange<Float>, val saveDefaultValuesFunction: (state : State<SettingsState>) -> Unit) : SettingsEvent()
    data class EnteredName(val name: String, val saveDefaultValuesFunction: (state : State<SettingsState>) -> Unit) : SettingsEvent()
}