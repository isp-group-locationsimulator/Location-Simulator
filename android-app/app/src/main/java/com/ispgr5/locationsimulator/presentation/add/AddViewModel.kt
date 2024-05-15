package com.ispgr5.locationsimulator.presentation.add

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.model.InvalidConfigurationException
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for the EditScreen
 */
@HiltViewModel
class AddViewModel @Inject constructor(
    val configurationUseCases: ConfigurationUseCases,
) : ViewModel() {

    //The provided state for the View
    private val _state = mutableStateOf(AddScreenState())
    val state: State<AddScreenState> = _state

    /**
     * Handles UI Events
     */
    fun onEvent(
        event: AddEvent
    ) {
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
                        val defaultValues = event.getDefaultValues()
                        configurationUseCases.addConfiguration(
                            Configuration(
                                name = _state.value.name,
                                randomOrderPlayback = _state.value.randomOrderPlayback,
                                description = _state.value.description,
                                components = listOf(
                                    ConfigComponent.Vibration(
                                        id = 1,
                                        name = defaultValues.defaultNameVibration,
                                        minStrength = defaultValues.minStrengthVibration,
                                        maxStrength = defaultValues.maxStrengthVibration,
                                        minPause = defaultValues.minPauseVibration,
                                        maxPause = defaultValues.maxPauseVibration,
                                        minDuration = defaultValues.minDurationVibration,
                                        maxDuration = defaultValues.maxDurationVibration
                                    ),
                                    ConfigComponent.Vibration(
                                        id = 2,
                                        name = defaultValues.defaultNameVibration,
                                        minStrength = defaultValues.minStrengthVibration,
                                        maxStrength = defaultValues.maxStrengthVibration,
                                        minPause = defaultValues.minPauseVibration,
                                        maxPause = defaultValues.maxPauseVibration,
                                        minDuration = defaultValues.minDurationVibration,
                                        maxDuration = defaultValues.maxDurationVibration
                                    ),
                                )
                            )
                        )
                    } catch (e: InvalidConfigurationException) {
                        print("Configuration Input is wrong")
                    }
                }
            }

            is AddEvent.SelectedImportConfiguration -> {
                event.configurationStorageManager.pickFileAndSafeToDatabase()
            }
        }
    }
}