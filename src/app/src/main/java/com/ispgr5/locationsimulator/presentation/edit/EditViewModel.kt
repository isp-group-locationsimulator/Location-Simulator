package com.ispgr5.locationsimulator.presentation.edit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class EditViewModel @Inject constructor(
    private val configurationUseCases: ConfigurationUseCases
) : ViewModel() {

    //The provided state for the View
    private val _state = mutableStateOf(EditScreenState())
    val state: State<EditScreenState> = _state

    /**
     * handles ui Events
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
            is EditEvent.EnteredDuration -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        duration = stringInputToInt(event.duration, state.value.duration)
                    )
                }
            }
            is EditEvent.EnteredPause -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        pause = stringInputToInt(event.pause, state.value.pause)
                    )
                }
            }
            is EditEvent.SaveConfiguration -> {
                viewModelScope.launch {
                    try {
                        configurationUseCases.addConfiguration(
                            Configuration(
                                name = _state.value.name,
                                description = _state.value.description,
                                duration = _state.value.duration,
                                pause = _state.value.pause
                            )
                        )
                    } catch (e: InvalidConfigurationException) {
                        print("Configuration Input is wrong")
                        //TODO say User that Configuration is has errors
                        //TODO configuration will be deleted if configuration is wrong
                    }
                }
            }
        }
    }

    /**
     * cast user input String to Int from Number Fields
     */
    private fun stringInputToInt(number: String, currentValue: Int): Int {
        var newNumber = currentValue
        try {
            //if user press back button and input string is empty
            newNumber = if (number == "") {
                0
                //if current Value is 0 we want that the new Number don't start with 0 like 05
            } else if (currentValue == 0) {
                val newNumbertmp = number.replace("0", "")
                if (newNumbertmp == "")
                    0
                else
                    newNumbertmp.toInt()
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