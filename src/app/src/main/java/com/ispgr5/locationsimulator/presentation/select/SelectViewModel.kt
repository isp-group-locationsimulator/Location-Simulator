package com.ispgr5.locationsimulator.presentation.select

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for the SelectScreen
 */
@HiltViewModel
class SelectViewModel @Inject constructor(
    private val configurationUseCases: ConfigurationUseCases
) : ViewModel() {

    // The provided state for the View
    private val _state = mutableStateOf(SelectScreenState())
    val state: State<SelectScreenState> = _state

    //state of the Database fetch needed when we quickly change between Screens. Aboard two running fetchJobs
    private var getConfigurationJob: Job? = null

    /**
     * Everytime we change to this Screen the Configurations will be fetched from Database
     */
    init {
        getConfigurations()
    }

    /**
     * handles ui Events
     */
    fun onEvent(event: SelectEvent) {
        when (event) {
            is SelectEvent.ToggledConfiguration -> {
                viewModelScope.launch {
                    if (_state.value.toggledConfiguration?.id == event.configuration.id){
                        _state.value = _state.value.copy(
                            toggledConfiguration = null
                        )
                    }else{
                        _state.value = _state.value.copy(
                            toggledConfiguration = event.configuration
                        )
                    }
                }
            }
            is SelectEvent.SelectedConfiguration -> {
                //do nothing the View will navigate
                return
            }
            is SelectEvent.DeleteConfiguration -> {
                viewModelScope.launch {
                    configurationUseCases.deleteConfiguration(event.configuration)
                }
            }
        }
    }

    /**
     * Fetches the Configurations from Database
     */
    private fun getConfigurations() {
        //Aboard previous fetch if existing
        getConfigurationJob?.cancel()
        //Fetch the Configuration from Database
        getConfigurationJob = configurationUseCases.getConfigurations()
            .onEach { configuration ->
                //update the state with the Configurations from Database
                _state.value = _state.value.copy(
                    configurations = configuration
                )
                //TODO this line will clear the whole Database. Delete this line after use
                //configurationUseCases.deleteConfiguration(configuration[0])
            }
            //have to be in View model scope because Database request have to be called by Coroutine
            .launchIn(viewModelScope)
    }
}