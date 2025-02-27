package com.ispgr5.locationsimulator.presentation.trainerScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ispgr5.locationsimulator.domain.model.ConfigurationComponentRoomConverter
import com.ispgr5.locationsimulator.network.ClientHandler
import kotlinx.serialization.ExperimentalSerializationApi

class TrainerScreenViewModel : ViewModel() {
    var state = mutableStateOf(TrainerScreenState())
        private set

    @OptIn(ExperimentalSerializationApi::class)
    fun onEvent(event: TrainerScreenEvent) {
        when (event) {
            is TrainerScreenEvent.StartTraining -> {
                val conv = ConfigurationComponentRoomConverter()
                for (device in ClientHandler.deviceList.getAsList()) {
                    if (!device.isPlaying) {
                        ClientHandler.sendToClient(
                            device.user,
                            "start " + conv.componentListToString(
                                device.selectedConfig?.components ?: emptyList()
                            )
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
                val conv = ConfigurationComponentRoomConverter()
                ClientHandler.sendToClient(
                    event.device.user, "start " +
                            conv.componentListToString(
                                event.device.selectedConfig?.components ?: emptyList()
                            )
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
}
