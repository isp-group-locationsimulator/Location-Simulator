package com.ispgr5.locationsimulator.presentation.delay

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.network.ClientHandler
import com.ispgr5.locationsimulator.network.ClientSingleton
import com.ispgr5.locationsimulator.network.Commands
import com.ispgr5.locationsimulator.network.ServerSingleton
import com.ispgr5.locationsimulator.presentation.ChosenRole
import com.ispgr5.locationsimulator.presentation.trainerScreen.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * The ViewModel for the DelayScreen
 */
@HiltViewModel
class DelayViewModel @Inject constructor(
	private val configurationUseCases: ConfigurationUseCases,
	//saveStateHandle is required to get the navigation Arguments like configurationId
	savedStateHandle: SavedStateHandle
) : ViewModel() {

	private val _state = mutableStateOf(DelayScreenState())
	val state: State<DelayScreenState> = _state

	/**
	 * Get the selected Configuration from Database
	 */
	init {
		val chosenRole = ChosenRole.valueOf(savedStateHandle.get<Int>("chosenRole")) ?: ChosenRole.STANDALONE

		when(chosenRole) {
            ChosenRole.STANDALONE -> addConfiguration(savedStateHandle)
            ChosenRole.REMOTE -> {
				val remoteName = ServerSingleton.remoteName
				if(remoteName != null) {
					ServerSingleton.start(remoteName)
				}
				addConfiguration(savedStateHandle)
			}
            ChosenRole.TRAINER -> {
				val ipAddress = savedStateHandle.get<String>("remoteIpAddress")
				if(ipAddress != "255.255.255.255") {
					addConfiguration(savedStateHandle)
					_state.value = _state.value.copy(
						remoteIpAddress = ipAddress
					)
				}
			}
        }
	}

	private fun addConfiguration(savedStateHandle: SavedStateHandle) {
		savedStateHandle.get<Int>("configurationId")?.let { configurationId ->
			viewModelScope.launch {
				configurationUseCases.getConfiguration(configurationId)?.also { configuration ->
					_state.value = _state.value.copy(
						configuration = configuration
					)
				}
			}
		}
	}

	/**
	 * Handles UI Events
	 */
	fun onEvent(event: DelayEvent) {
		when (event) {
			is DelayEvent.StartClicked -> {
				if (state.value.configuration != null) {
					ClientHandler.sendToClients(Commands.IS_PLAYING)
					ClientHandler.isPlayingState.set(true)
					event.startServiceFunction(
						state.value.configuration!!.name,
						state.value.configuration!!.components,
						state.value.configuration!!.randomOrderPlayback
					)
				}
			}

			is DelayEvent.RemoteStart -> {
				val conf = Json.decodeFromString<Configuration?>(event.configStr)
				if (conf != null) {
					ClientHandler.sendToClients(Commands.IS_PLAYING)
					ClientHandler.isPlayingState.set(true)
					event.startServiceFunction(
						conf.name,
						conf.components,
						conf.randomOrderPlayback
					)
				}
			}

			is DelayEvent.TrainerStart -> {
				val ipAddress = state.value.remoteIpAddress
				for (device in ClientSingleton.deviceList.getAsList()) {
					if((ipAddress == null || device.ipAddress == ipAddress) && !device.isPlaying) {
						val serializedConfig = Json.encodeToString(device.selectedConfig)
						ClientSingleton.send(
							device.ipAddress,
							Commands.formatStart(
								serializedConfig,
								event.hours,
								event.minutes,
								event.seconds
							)
						)
					}
				}
			}
		}
	}
}