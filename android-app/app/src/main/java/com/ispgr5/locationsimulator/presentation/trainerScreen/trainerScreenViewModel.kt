package com.ispgr5.locationsimulator.presentation.trainerScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.network.ClientHandler
import com.ispgr5.locationsimulator.network.ServerSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class TrainerScreenViewModel @Inject constructor(
    private val configurationUseCases: ConfigurationUseCases
) : ViewModel() {
    private var getConfigurationJob: Job? = null

    var state = mutableStateOf(TrainerScreenState())
        private set

    init {
        ServerSingleton.start()
        addDefaultConfiguration()
    }

    fun onEvent(event: TrainerScreenEvent) {
        when (event) {
            is TrainerScreenEvent.StartTraining -> {
                for (device in ClientHandler.deviceList.getAsList()) {
                    if (!device.isPlaying) {
                        val serializedConfig = Json.encodeToString(device.selectedConfig)
                        ClientHandler.sendToClient(
                            device.user, "start $serializedConfig"
                        )
                        val modifiedDevice = device.copy()
                        modifiedDevice.isPlaying = true
                        ClientHandler.deviceList.updateDevice(modifiedDevice)
                    }
                }
            }

            is TrainerScreenEvent.StopTraining -> {
                for (device in ClientHandler.deviceList.getAsList()) {
                    if (device.isPlaying) {
                        ClientHandler.sendToClient(
                            device.user,
                            "stop"
                        )
                        val modifiedDevice = device.copy()
                        modifiedDevice.isPlaying = false
                        ClientHandler.deviceList.updateDevice(modifiedDevice)
                    }
                }
            }

            is TrainerScreenEvent.StartDeviceTraining -> {
                val serializedConfig = Json.encodeToString(event.device.selectedConfig)
                ClientHandler.sendToClient(
                    event.device.user, "start $serializedConfig"
                )
            }

            is TrainerScreenEvent.StopDeviceTraining -> {
                ClientHandler.sendToClient(event.device.user, "stop")
            }

            is TrainerScreenEvent.ToggleInputFields -> {
                state.value = state.value.copy(
                    showInputFields = event.show
                )
            }
        }
    }

    private fun addDefaultConfiguration() {
        getConfigurationJob?.cancel()
        getConfigurationJob = configurationUseCases.getConfigurations()
            .onEach { configuration ->
                state.value = state.value.copy(
                    defaultConfig = configuration.firstOrNull()
                )
            }
            .launchIn(viewModelScope)
    }
}
