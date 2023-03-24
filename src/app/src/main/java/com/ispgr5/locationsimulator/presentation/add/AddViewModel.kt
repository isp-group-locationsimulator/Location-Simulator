package com.ispgr5.locationsimulator.presentation.add

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
class AddViewModel @Inject constructor(
	private val configurationUseCases: ConfigurationUseCases,
) : ViewModel() {

	//The provided state for the View
	private val _state = mutableStateOf(AddScreenState())
	val state: State<AddScreenState> = _state

	/**
	 * Handles UI Events
	 */
	fun onEvent(event: AddEvent) {
		when (event) {
			is AddEvent.EnteredName -> {
				viewModelScope.launch {
					_state.value = _state.value.copy(
						name = event.name
					)
				}
			}
			is AddEvent.EnteredDescription -> {
				viewModelScope.launch {
					_state.value = _state.value.copy(
						description = event.description
					)
				}
			}
			is AddEvent.SaveConfiguration -> {
				viewModelScope.launch {
					try {
						configurationUseCases.addConfiguration(
							Configuration(
								name = _state.value.name,
								randomOrderPlayback = _state.value.randomOrderPlayback,
								description = _state.value.description,
								components = listOf(
									Vibration(
										id = 1,
										name = "default Name",
										minStrength = 1,
										maxStrength = 255,
										minPause = 3,
										maxPause = 8,
										minDuration = 1,
										maxDuration = 3000
									),
									Vibration(
										id = 2,
										name = "default Name",
										minStrength = 1,
										maxStrength = 255,
										minPause = 3,
										maxPause = 8,
										minDuration = 2000,
										maxDuration = 7000
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
			is AddEvent.SelectedImportConfiguration -> {
				event.configurationStorageManager.pickFileAndSafeToDatabase(configurationUseCases = configurationUseCases)
			}
		}
	}
}