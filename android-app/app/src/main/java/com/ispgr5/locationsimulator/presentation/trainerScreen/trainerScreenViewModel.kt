package com.ispgr5.locationsimulator.presentation.trainerScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.network.ClientSingleton
import com.ispgr5.locationsimulator.network.Commands
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
        ClientSingleton.start()
        addDefaultConfiguration()
    }

    fun onEvent(event: TrainerScreenEvent) {
        when (event) {
            is TrainerScreenEvent.StartTraining -> {
                for (device in ClientSingleton.deviceList.getAsList()) {
                    if (!device.isPlaying) {
                        val serializedConfig = Json.encodeToString(device.selectedConfig)
                        ClientSingleton.send(
                            device.ipAddress, Commands.formatStart(serializedConfig)
                        )
                    }
                }
            }

            is TrainerScreenEvent.StopTraining -> {
                for (device in ClientSingleton.deviceList.getAsList()) {
                    if (device.isPlaying) {
                        ClientSingleton.send(
                            device.ipAddress,
                            Commands.STOP
                        )
                    }
                }
            }

            is TrainerScreenEvent.StartDeviceTraining -> {
                val serializedConfig = Json.encodeToString(event.device.selectedConfig)
                ClientSingleton.send(
                    event.device.ipAddress, Commands.formatStart(serializedConfig)
                )
            }

            is TrainerScreenEvent.TestVibrationPress -> {
                val config = Configuration(
                    "testVibration", "", false, listOf(
                        ConfigComponent.Vibration(0, "testVibration", 255, 255, 0, 0, 10000, 10000)
                    )
                )
                val serializedConfig = Json.encodeToString(config)
                ClientSingleton.send(
                    event.device.ipAddress, Commands.formatStart(serializedConfig)
                )
            }

            is TrainerScreenEvent.TestSoundPress -> {
                val config = Configuration(
                    "testSound", "", false, listOf(
                        ConfigComponent.Sound(0, "Bellen.mp3", "testSound", 1f, 1f, 0, 0)
                    )
                )
                val serializedConfig = Json.encodeToString(config)
                ClientSingleton.send(
                    event.device.ipAddress, Commands.formatStart(serializedConfig)
                )
            }

            is TrainerScreenEvent.StopDeviceTraining -> {
                ClientSingleton.send(event.device.ipAddress, Commands.STOP)
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
