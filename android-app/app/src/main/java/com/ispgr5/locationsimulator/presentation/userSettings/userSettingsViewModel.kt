package com.ispgr5.locationsimulator.presentation.userSettings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.network.ClientSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val configurationUseCases: ConfigurationUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var getConfigurationJob: Job? = null

    private val _state = mutableStateOf(UserSettingsState(selectedUser = savedStateHandle.get<String>("userIpAddress")!!))
    val state: State<UserSettingsState> = _state

    init {
        getConfigurations()
    }

    fun onEvent(event: UserSettingsEvent) {
        when (event) {
            is UserSettingsEvent.SelectConfiguration -> {
                _state.value = UserSettingsState(
                    selectedUser = _state.value.selectedUser,
                    availableConfigurations = _state.value.availableConfigurations,
                    selectedConfiguration = event.configID
                )
                val selectedConfig = _state.value.selectedConfiguration?.let { getConfigFromID(it) }
                for(device in ClientSingleton.deviceList.getAsList()) {
                    if(device.ipAddress == _state.value.selectedUser) {
                        val modifiedDevice = device.copy()
                        modifiedDevice.selectedConfig = selectedConfig
                        ClientSingleton.deviceList.updateDevice(modifiedDevice)
                    }
                }
            }
            is UserSettingsEvent.ExportConfiguration -> {
                // TODO: Implementiere die Export-Funktion
            }
            is UserSettingsEvent.SaveSettings -> {
                // TODO: Implementiere die Speicher-Funktion
            }
        }
    }

    /**
     * Fetches the Configurations from Database
     */
    private fun getConfigurations() {
        getConfigurationJob?.cancel()
        getConfigurationJob = configurationUseCases.getConfigurations()
            .onEach { configuration ->
                _state.value = _state.value.copy(
                    availableConfigurations = configuration
                )
            }
            .launchIn(viewModelScope)
    }

    private fun getConfigFromID(id: Int) : Configuration? {
        for (conf in _state.value.availableConfigurations) {
            if(conf.id == id) {
                return conf
            }
        }
        return null
    }
}
