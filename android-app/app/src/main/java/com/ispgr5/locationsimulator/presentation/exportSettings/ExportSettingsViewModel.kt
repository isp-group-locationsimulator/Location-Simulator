package com.ispgr5.locationsimulator.presentation.exportSettings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ExportSettingsViewModel : ViewModel() {

    private val _state = mutableStateOf(ExportSettingsState())
    val state: State<ExportSettingsState> = _state

    fun onEvent(event: ExportSettingsEvent) {
        when (event) {
            is ExportSettingsEvent.SelectConfiguration -> {
                _state.value = _state.value.copy(selectedConfiguration = event.configName)
            }
            is ExportSettingsEvent.ExportConfiguration -> {
                // TODO: Implementiere die Export-Funktion
            }
        }
    }
}
