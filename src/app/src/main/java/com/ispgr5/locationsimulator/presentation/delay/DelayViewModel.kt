package com.ispgr5.locationsimulator.presentation.delay

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
     * handles ui Events
     */
    fun onEvent(event: DelayEvent) {
        when (event){
            is DelayEvent.StartClicked -> {
                if(state.value.configuration != null)
                    event.startServiceFunction(state.value.configuration!!.components)
            }
        }
    }
}