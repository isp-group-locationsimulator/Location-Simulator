package com.ispgr5.locationsimulator.presentation.editTimeline

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.model.*
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.presentation.edit.EditEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class EditTimelineViewModel @Inject constructor(
    private val configurationUseCases: ConfigurationUseCases,
    //saveStateHandle is required to get the navigation Arguments like configurationId
    savedStateHandle: SavedStateHandle ) : ViewModel() {

    //The provided state for the View
    private val _state = mutableStateOf(EditTimelineState())
    val state: State<EditTimelineState> = _state

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
                            components = configuration.components
                        )
                    }
                }
            }
        }

        /*
        //create Configuration
        val sound1 = Sound("Schrei", 4, 80, 3, 5, true);
        val sound2 = Sound("Klopfen", 4, 5, 3, 5, true);
        val sound3 = Sound("Singen", 4, 5, 3, 5, true);
        val vib1 = Vibration(3,90,3,33,23,42)
        val vib2 = Vibration(34,90,3,23,23,42)
        val vib3 = Vibration(3,23,3,23,23,42)
        val components = ArrayList<ConfigComponent>()
        components.add(sound1);
        components.add(vib1)
        components.add(vib2)
        components.add(sound2);
        components.add(vib3)
        components.add(sound3);
        val configuration = Configuration("config1", "beschreibung", components);


        _state.value = _state.value.copy(
            name = configuration.name,
            description = configuration.description,
            components = configuration.components,
            current = sound1
        ) */
    }

    /**
     * handles ui Events
     */
    fun onEvent(event: EditTimelineEvent) {

        when (event) {
            is EditTimelineEvent.ChangedSoundVolume -> {
                viewModelScope.launch {
                    val compsCopy = state.value.components.toMutableList()
                    val component = compsCopy.get(state.value.currentTimelineIndex).copy()
                    when(component) {
                        is Sound -> {
                            component.minVolume = event.range.start.toInt()
                            component.maxVolume = event.range.endInclusive.toInt()
                        }
                    }
                    compsCopy.set(state.value.currentTimelineIndex, component)
                    _state.value = _state.value.copy(
                        components = compsCopy,
                        current = component
                    )
                }
            }
            is EditTimelineEvent.ChangedPause -> {
                viewModelScope.launch {
                    val compsCopy = state.value.components.toMutableList()
                    val component = compsCopy.get(state.value.currentTimelineIndex).copy()
                    when(component) {
                        is Sound -> {
                            component.minPause = event.range.start.toInt()
                            component.maxPause = event.range.endInclusive.toInt()
                        }
                        is Vibration -> {
                            component.minPause = event.range.start.toInt()
                            component.maxPause = event.range.endInclusive.toInt()
                        }
                    }
                    compsCopy.set(state.value.currentTimelineIndex, component)
                    _state.value = _state.value.copy(
                        components = compsCopy,
                        current = component
                    )
                }
            }
            is EditTimelineEvent.ChangedVibStrength -> {
                viewModelScope.launch {
                    val compsCopy = state.value.components.toMutableList()
                    val component = compsCopy.get(state.value.currentTimelineIndex).copy()
                    when(component) {
                        is Vibration -> {
                            component.minStrength = event.range.start.toInt()
                            component.maxStrength = event.range.endInclusive.toInt()
                        }
                    }
                    compsCopy.set(state.value.currentTimelineIndex, component)
                    _state.value = _state.value.copy(
                        components = compsCopy,
                        current = component
                    )
                }
            }
            is EditTimelineEvent.ChangedVibDuration -> {
                viewModelScope.launch {
                    val compsCopy = state.value.components.toMutableList()
                    val component = compsCopy.get(state.value.currentTimelineIndex).copy()
                    when(component) {
                        is Vibration -> {
                            component.minDuration = event.range.start.toInt()
                            component.maxDuration = event.range.endInclusive.toInt()
                        }
                    }
                    compsCopy.set(state.value.currentTimelineIndex, component)
                    _state.value = _state.value.copy(
                        components = compsCopy,
                        current = component
                    )
                }
            }
            is EditTimelineEvent.AddSound -> {
                viewModelScope.launch {
                    val sound = Sound("test",3,4,3, 7,false)
                    val listC = state.value.components.toMutableList()
                    listC.add(sound)
                    _state.value = _state.value.copy(
                        components = listC
                    )
                }
            }
            is EditTimelineEvent.SelectedTimelineItem -> {
                //find index
                val compsCopy = state.value.components.toMutableList()
                var index = 0;
                for(i in compsCopy.indices){
                    if(compsCopy[i] == event.selectConfigComp){
                        index = i
                        break
                    }
                }
                _state.value = _state.value.copy(
                    current =  event.selectConfigComp,
                    currentTimelineIndex = index
                )
            }
        }
        //TODO always save?
        saveConfiguration()
    }



    private fun saveConfiguration() {
        viewModelScope.launch {
            try {
                configurationUseCases.addConfiguration(
                    Configuration(
                        id = currentConfigurationId,
                        name = _state.value.name,
                        description = _state.value.description,
                        components = _state.value.components
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
