package com.ispgr5.locationsimulator.presentation.edit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.presentation.select.SelectEvent
import com.ispgr5.locationsimulator.presentation.select.SelectScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class EditViewModel @Inject constructor(
    private val configurationUseCases: ConfigurationUseCases
) : ViewModel() {

    // The provided state for the View
    private val _state = mutableStateOf(EditScreenState())
    val state: State<EditScreenState> = _state

    /**
     * handles ui Events
     */
    fun onEvent(event: EditEvent) {
        when (event) {
            is EditEvent.addDuration -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy( duration = event.duration.toInt())
                }
            }
            is EditEvent.addPause -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy( pause = event.pause.toInt())
                }
            }
            is EditEvent.save -> {
                viewModelScope.launch {
                    configurationUseCases.addConfiguration(
                        Configuration(duration = _state.value.duration ,
                            pause = _state.value.pause, name = "helloWorld" + _state.value.duration + _state.value.pause))
                }
            }
        }
    }

}