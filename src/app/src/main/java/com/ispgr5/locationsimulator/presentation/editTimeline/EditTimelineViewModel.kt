package com.ispgr5.locationsimulator.presentation.editTimeline

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.model.*
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

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

        //reset current selected to first
        if(state.value.components.isNotEmpty()){
            _state.value = _state.value.copy(
                current = state.value.components[0],
                currentTimelineIndex = 0
            )
        }
    }

    /**
     * handles ui Events
     */
    fun onEvent(event: EditTimelineEvent) {

        when (event) {
            is EditTimelineEvent.ChangedSoundVolume -> {
                viewModelScope.launch {
                    val compsCopy = state.value.components.toMutableList()
                    val component = compsCopy[state.value.currentTimelineIndex].copy()
                    when(component) {
                        is Sound -> {
                            component.minVolume = RangeConverter.floatTo8BitInt(event.range.start)
                            component.maxVolume = RangeConverter.floatTo8BitInt(event.range.endInclusive)
                        }
                    }
                    compsCopy[state.value.currentTimelineIndex] = component
                    _state.value = _state.value.copy(
                        components = compsCopy,
                        current = component
                    )
                }
            }
            is EditTimelineEvent.ChangedPause -> {
                viewModelScope.launch {
                    val compsCopy = state.value.components.toMutableList()
                    val component = compsCopy[state.value.currentTimelineIndex].copy()
                    when(component) {
                        is Sound -> {
                            component.minPause = RangeConverter.sToMs(event.range.start)
                            component.maxPause = RangeConverter.sToMs(event.range.endInclusive)
                        }
                        is Vibration -> {
                            component.minPause = RangeConverter.sToMs(event.range.start)
                            component.maxPause = RangeConverter.sToMs(event.range.endInclusive)
                        }
                    }
                    compsCopy[state.value.currentTimelineIndex] = component
                    _state.value = _state.value.copy(
                        components = compsCopy,
                        current = component
                    )
                }
            }
            is EditTimelineEvent.ChangedVibStrength -> {
                viewModelScope.launch {
                    val compsCopy = state.value.components.toMutableList()
                    val component = compsCopy[state.value.currentTimelineIndex].copy()
                    when(component) {
                        is Vibration -> {
                            component.minStrength = RangeConverter.floatTo8BitInt(event.range.start)
                            component.maxStrength = RangeConverter.floatTo8BitInt(event.range.endInclusive)
                        }
                    }
                    compsCopy[state.value.currentTimelineIndex] = component
                    _state.value = _state.value.copy(
                        components = compsCopy,
                        current = component
                    )
                }
            }
            is EditTimelineEvent.ChangedVibDuration -> {
                viewModelScope.launch {
                    val compsCopy = state.value.components.toMutableList()
                    val component = compsCopy[state.value.currentTimelineIndex].copy()
                    when(component) {
                        is Vibration -> {
                            component.minDuration = RangeConverter.sToMs(event.range.start)
                            component.maxDuration = RangeConverter.sToMs(event.range.endInclusive)
                        }
                    }
                    compsCopy[state.value.currentTimelineIndex] = component
                    _state.value = _state.value.copy(
                        components = compsCopy,
                        current = component
                    )
                }
            }
            is EditTimelineEvent.AddSound -> {
                viewModelScope.launch {
                    val sound = Sound("Klopfen.m4a",3,4,3, 7,false)
                    val listC = state.value.components.toMutableList()
                    listC.add(sound)
                    _state.value = _state.value.copy(
                        components = listC
                    )
                }
            }
            is EditTimelineEvent.AddVibration -> {
                viewModelScope.launch {
                    val vibration = Vibration(3,4,3, 7,6,8)
                    val listC = state.value.components.toMutableList()
                    listC.add(vibration)
                    _state.value = _state.value.copy(
                        components = listC
                    )
                }
            }
            is EditTimelineEvent.SelectedTimelineItem -> {
                //find index
                val compsCopy = state.value.components.toMutableList()
                var index = 0
                for(i in compsCopy.indices){
                    if(compsCopy[i] === event.selectConfigComp){
                        index = i
                        break
                    }
                }
                if (index == state.value.currentTimelineIndex){
                    _state.value = _state.value.copy(
                        current = null,
                        currentTimelineIndex = -1
                    )
                }else {
                    _state.value = _state.value.copy(
                        current = event.selectConfigComp,
                        currentTimelineIndex = index
                    )
                }
            }

            is EditTimelineEvent.ChangedName -> {
                //TODO limit
                _state.value = _state.value.copy(
                    name = event.name
                )
            }

            is EditTimelineEvent.ChangedDescription -> {
                //TODO limit
                _state.value = _state.value.copy(
                    description = event.description
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
