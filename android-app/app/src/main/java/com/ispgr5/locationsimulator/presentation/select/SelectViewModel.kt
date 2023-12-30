package com.ispgr5.locationsimulator.presentation.select

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SelectViewModel"

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
                    if (_state.value.toggledConfiguration?.equals(event.configuration) == true) {
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
                Log.i(TAG, "delete: ${event.configuration.id} - ${event.configuration.name}")
                viewModelScope.launch {
                    configurationUseCases.deleteConfiguration(event.configuration)
                }
            }

            is SelectEvent.SelectNormalMode -> {
                Log.i(TAG, "normal mode")
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        isInDeleteMode = false,
                        selectedConfigurationForDeletion = null
                    )
                }
            }

            is SelectEvent.SelectDeleteMode -> {
                Log.i(TAG, "delete mode")
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        isInDeleteMode = true,
                        selectedConfigurationForDeletion = null
                    )
                }
            }

            is SelectEvent.SelectConfigurationForDeletion -> {
                Log.i(TAG, "select for deletion: ${event.configuration.id} - ${event.configuration.name}")
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        selectedConfigurationForDeletion = event.configuration
                    )
                }
            }

            is SelectEvent.SelectedExportConfiguration -> {
                event.configurationStorageManager.exportConfigurationUsingShareSheet(
                    event.context,
                    event.configuration
                )
            }

            is SelectEvent.FavoriteClicked -> {
                val favouriteConfiguration = event.configuration.copy(isFavorite = !event.configuration.isFavorite)
                val configurationListCopy = state.value.configurations.toMutableList()
                state.value.configurations.indexOfFirst { conf -> conf == event.configuration }.let {
                    configurationListCopy[it] = favouriteConfiguration
                }

                _state.value = _state.value.copy(
                    configurations = configurationListCopy
                )
                viewModelScope.launch {
                    configurationUseCases.addConfiguration(favouriteConfiguration)
                }
            }

            is SelectEvent.Duplicate -> {
                viewModelScope.launch {
                    configurationUseCases.getConfiguration(event.id!!)?.let { configuration ->
                        configurationUseCases.addConfiguration(
                            Configuration(
                                name = configuration.name,
                                randomOrderPlayback = configuration.randomOrderPlayback,
                                description = configuration.description,
                                components = configuration.components,
                                isFavorite = false,
                            )
                        )
                    }
                }
            }
        }
    }

    /**
     * Fetches the Configurations from Database
     */
    private fun getConfigurations() {
        //abort previous fetch if existing
        getConfigurationJob?.cancel()
        //Fetch the Configuration from Database
        getConfigurationJob = configurationUseCases.getConfigurations()
            .onEach { configuration ->
                //update the state with the Configurations from Database
                _state.value = _state.value.copy(
                    configurations = configuration
                )
            }
            //have to be in View model scope because Database request have to be called by Coroutine
            .launchIn(viewModelScope)
    }

    /**
     * This function updated the Select Screen State by looking up the private dir
     * with the saved Sounds and compare it with the Sound names in all Configurations.
     *
     */
    fun updateConfigurationWithErrorsState(soundStorageManager: SoundStorageManager) {
        val configurationsWithErrors = mutableListOf<Configuration>()
        val knownSounds = soundStorageManager.getSoundFileNames()
        for (conf in _state.value.configurations) {
            var hasErrors = false
            for (comp in conf.components) {
                if (comp is ConfigComponent.Sound) {
                    var existInKnownSounds = false
                    for (knownSound in knownSounds) {
                        if (comp.source == knownSound) {
                            existInKnownSounds = true
                            break
                        }
                    }
                    if (!existInKnownSounds) {
                        hasErrors = true
                        break
                    }
                }
            }
            if (hasErrors) {
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
    fun whatIsHisErrors(
        configuration: Configuration,
        soundStorageManager: SoundStorageManager
    ): List<String> {
        val errors = mutableListOf<String>()
        val knownSounds = soundStorageManager.getSoundFileNames()
        for (comp in configuration.components) {
            if (comp is ConfigComponent.Sound) {
                var exist = false
                for (sound in knownSounds) {
                    if (comp.source == sound) {
                        exist = true
                        break
                    }
                }
                if (!exist) {
                    errors.add(comp.source)
                }
            }
        }
        return errors.toSet().toList()
    }

}