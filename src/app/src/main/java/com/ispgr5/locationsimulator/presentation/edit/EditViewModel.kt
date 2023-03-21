package com.ispgr5.locationsimulator.presentation.edit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.model.InvalidConfigurationException
import com.ispgr5.locationsimulator.domain.model.Vibration
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for the EditScreen
 */
@HiltViewModel
class EditViewModel @Inject constructor(
	private val configurationUseCases: ConfigurationUseCases,
	//saveStateHandle is required to get the navigation Arguments like configurationId
	savedStateHandle: SavedStateHandle
) : ViewModel() {

	//The provided state for the View
	private val _state = mutableStateOf(EditScreenState())
	val state: State<EditScreenState> = _state

	//The current Configuration Id to override the Configuration with same id in Database
	private var currentConfigurationId: Int? = null

	init {
		savedStateHandle.get<Int>("configurationId")?.let { configurationId ->
			if (configurationId != -1) {
				viewModelScope.launch {
					configurationUseCases.getConfiguration(configurationId)?.also { configuration ->
						currentConfigurationId = configuration.id
						_state.value = _state.value.copy(
							name = configuration.name,
							description = configuration.description,
							randomOrderPlayback = configuration.randomOrderPlayback,
							components = configuration.components
						)
					}
				}
			}
		}
	}

	/**
	 * Handles UI Events
	 */
	fun onEvent(event: EditEvent) {
		when (event) {
			is EditEvent.EnteredName -> {
				viewModelScope.launch {
					_state.value = _state.value.copy(
						name = event.name
					)
				}
			}
			is EditEvent.EnteredDescription -> {
				viewModelScope.launch {
					_state.value = _state.value.copy(
						description = event.description
					)
				}
			}
			is EditEvent.SaveConfiguration -> {
				viewModelScope.launch {
					try {
						configurationUseCases.addConfiguration(
							Configuration(
								id = currentConfigurationId,
								name = _state.value.name,
								randomOrderPlayback = _state.value.randomOrderPlayback,
								description = _state.value.description,
								//TODO This is just an example for testing. Enter the user input here
								components = listOf(
									Vibration(
										id = 1,
										minStrength = 1,
										maxStrength = 255,
										minPause = 3,
										maxPause = 8,
										minDuration = 1,
										maxDuration = 2
									),
									Vibration(
										id = 2,
										minStrength = 1,
										maxStrength = 255,
										minPause = 3,
										maxPause = 8,
										minDuration = 3,
										maxDuration = 4
									),
								)
							)
						)
					} catch (e: InvalidConfigurationException) {
						print("Configuration Input is wrong")
						//TODO say User that Configuration is has errors
						//TODO configuration will be deleted if configuration is wrong
					}
				}
			}
			is EditEvent.SelectedImportConfiguration -> {
				event.configurationStorageManager.pickFileAndSafeToDatabase(configurationUseCases = configurationUseCases)
			}
		}
	}

	/**
	 * Cast user input String to Int from Number Fields
	 */
	private fun stringInputToInt(number: String, currentValue: Int): Int {
		var newNumber = currentValue
		try {
			//if user press back button and input string is empty
			newNumber = if (number == "") {
				0
				//if current Value is 0 we want that the new Number don't start with 0 like 05
			} else if (currentValue == 0) {
				val newNumberTmp = number.replace("0", "")
				if (newNumberTmp == "")
					0
				else
					newNumberTmp.toInt()
				//default
			} else {
				number.toInt()
			}
			return newNumber
		} catch (e: Exception) {
			//if user press enter button, negative numbers, letters... than don't change the field value
			return newNumber
		}
	}
}