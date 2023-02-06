package com.ispgr5.locationsimulator.presentation.select

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.model.Sound
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
     * Handles UI Events
     */
    fun onEvent(event: SelectEvent) {
        when (event) {
            is SelectEvent.ToggledConfiguration -> {
                viewModelScope.launch {
                    if (_state.value.toggledConfiguration?.id == event.configuration.id) {
                        _state.value = _state.value.copy(
                            toggledConfiguration = null
                        )
                    } else {
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
            is SelectEvent.SelectDeleteMode -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        isInDeleteMode = !_state.value.isInDeleteMode,
                        selectedConfigurationForDeletion = null
                    )
                }
            }
            is SelectEvent.SelectConfigurationForDeletion -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        selectedConfigurationForDeletion = event.configuration
                    )
                }
            }
            is SelectEvent.SelectedExportConfiguration -> {
                event.configurationStorageManager.safeConfigurationToStorage(event.configuration)
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

    /**
     * This function updated the Select Screen State by looking up the private dir
     * with the saved Sounds and compare it with the Sound names in all Configurations.
     *
     */
    fun updateConfigurationWithErrorsState(soundStorageManager: SoundStorageManager){
        val configurationsWithErrors = mutableListOf<Configuration>()
        val knownSounds = soundStorageManager.getSoundFileNames()
        for (conf in _state.value.configurations){
            var hasErrors = false
            for (comp in conf.components) {
                if (comp is Sound) {
                    var existInKnownSounds = false
                    for (knownSound in knownSounds) {
                        if (comp.source == knownSound){
                            existInKnownSounds = true
                            break
                        }
                    }
                    if (!existInKnownSounds){
                        hasErrors = true
                        break
                    }
                }
            }
            if (hasErrors){
                configurationsWithErrors.add(conf)
            }
        }
        _state.value = _state.value.copy(
            configurationsWithErrors = configurationsWithErrors
        )
    }

    /**
     * search in our private dir for Sounds an look up if there are Sounds used in the configuration but
     * they don't exist in our private dir
     * @return unknown Sound names in the given configuration
     */
    fun whatIsHisErrors(configuration: Configuration, soundStorageManager: SoundStorageManager):List<String>{
        val errors = mutableListOf<String>()
        val knownSounds = soundStorageManager.getSoundFileNames()
        for (comp in configuration.components){
            if (comp is Sound){
                var exist = false
                for (sound in knownSounds){
                    if (comp.source == sound){
                        exist = true
                        break
                    }
                }
                if (!exist){
                    errors.add(comp.source)
                }
            }
        }
        return errors.toSet().toList()
    }

}