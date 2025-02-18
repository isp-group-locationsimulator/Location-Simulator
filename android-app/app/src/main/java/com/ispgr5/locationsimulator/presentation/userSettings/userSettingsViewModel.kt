package com.ispgr5.locationsimulator.presentation.userSettings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class UserSettingsViewModel : ViewModel() {

    private val _state = mutableStateOf(UserSettingsState())
    val state: State<UserSettingsState> = _state

    fun onEvent(event: UserSettingsEvent) {
        when (event) {
            is UserSettingsEvent.SelectConfiguration -> {
                _state.value = UserSettingsState(
                    selectedUser = _state.value.selectedUser,
                    availableConfigurations = _state.value.availableConfigurations,
                    selectedConfiguration = event.configName
                )

            }
            is UserSettingsEvent.ExportConfiguration -> {
                // TODO: Implementiere die Export-Funktion
            }
            is UserSettingsEvent.SaveSettings -> {
                // TODO: Implementiere die Speicher-Funktion
            }
        }
    }
}
