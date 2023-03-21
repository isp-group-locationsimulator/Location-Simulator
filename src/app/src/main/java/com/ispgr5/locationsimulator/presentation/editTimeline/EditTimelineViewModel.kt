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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

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
                            randomOrderPlayback = configuration.randomOrderPlayback,
                            components = configuration.components
                        )
                    }
                }
            }
        }
    }

    /**
     * handles ui Events
     */
    fun onEvent(event: EditTimelineEvent) {
        when (event) {
            is EditTimelineEvent.ChangedSoundVolume -> {
                if (state.value.current == null){return}
                viewModelScope.launch {
                    var component = state.value.current!!.copy()
                    when (component) {
                        is Sound -> {
                            component = component.myCopy(
                                minVolume = RangeConverter.transformPercentageToFactor(event.range.start),
                                maxVolume = RangeConverter.transformPercentageToFactor(event.range.endInclusive)
                            )
                        }
                    }
                    _state.value = _state.value.copy(
                        components = state.value.components.map { if (it === state.value.current ) component else it },
                        current = component
                    )
                }
            }
            is EditTimelineEvent.ChangedPause -> {
                if (state.value.current == null){return}
                viewModelScope.launch {
                    var component = state.value.current!!.copy()
                    when (component) {
                        is Sound -> {
                            component = (component as Sound).myCopy(
                                minPause = RangeConverter.sToMs(event.range.start),
                                maxPause = RangeConverter.sToMs(event.range.endInclusive)
                            )
                        }
                        is Vibration -> {
                            component = (component as Vibration).myCopy(
                                minPause = RangeConverter.sToMs(event.range.start),
                                maxPause = RangeConverter.sToMs(event.range.endInclusive)
                            )
                        }
                    }
                    _state.value = _state.value.copy(
                        components = state.value.components.map { if (it === state.value.current ) component else it },
                        current = component
                    )
                }
            }
            is EditTimelineEvent.ChangedVibStrength -> {
                if (state.value.current == null){return}
                viewModelScope.launch {
                    var component = state.value.current!!.copy()
                    when (component) {
                        is Vibration -> {
                            component = (component as Vibration).myCopy(
                                minStrength = RangeConverter.floatTo8BitInt(event.range.start),
                                maxStrength = RangeConverter.floatTo8BitInt(event.range.endInclusive)
                            )
                        }
                    }
                    _state.value = _state.value.copy(
                        components = state.value.components.map { if (it === state.value.current ) component else it },
                        current = component
                    )
                }
            }
            is EditTimelineEvent.ChangedVibDuration -> {
                if (state.value.current == null){return}
                viewModelScope.launch {
                    var component = state.value.current!!.copy()
                    when (component) {
                        is Vibration -> {
                            component = (component as Vibration).myCopy(
                                minDuration = RangeConverter.sToMs(event.range.start),
                                maxDuration = RangeConverter.sToMs(event.range.endInclusive)
                            )
                        }
                    }
                    _state.value = _state.value.copy(
                        components = state.value.components.map { if (it === state.value.current ) component else it },
                        current = component
                    )
                }
            }
            is EditTimelineEvent.AddSound -> {
                viewModelScope.launch {
                    event.navController.navigate("sound?configurationId=${currentConfigurationId}")
                }
            }
            is EditTimelineEvent.AddVibration -> {
                viewModelScope.launch {
                    val vibration = Vibration(
                        id = (state.value.components.maxByOrNull { it.id }?.id ?: 0) +1,
                        3, 4, 3, 7, 6, 8)
                    val listC = state.value.components.toMutableList()
                    listC.add(vibration)
                    _state.value = _state.value.copy(
                        components = listC
                    )
                }
            }
            is EditTimelineEvent.SelectedTimelineItem -> {
                _state.value = _state.value.copy(
                    current = if (event.selectConfigComp === state.value.current){null}else{event.selectConfigComp}
                )
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
            is EditTimelineEvent.DeleteConfigurationComponent -> {
                viewModelScope.launch {
                    val compsCopy = state.value.components.toMutableList()
                    compsCopy.remove(event.configComponent)
                    _state.value = _state.value.copy(
                        components = compsCopy,
                        current = null
                    )
                }
            }
            is EditTimelineEvent.MoveConfCompLeft -> {
                val index = getIndex(state.value.components, event.configComponent)
                val compsCopy = state.value.components.toMutableList()
                if (index > 0) {
                    compsCopy[index] = compsCopy[index - 1]
                    compsCopy[index - 1] = event.configComponent
                    _state.value = _state.value.copy(
                        components = compsCopy
                    )
                }
            }
            is EditTimelineEvent.MoveConfCompRight -> {
                val index = getIndex(state.value.components, event.configComponent)
                val compsCopy = state.value.components.toMutableList()
                if (index < compsCopy.size - 1) {
                    compsCopy[index] = compsCopy[index + 1]
                    compsCopy[index + 1] = event.configComponent
                    _state.value = _state.value.copy(
                        components = compsCopy
                    )
                }
            }
            is EditTimelineEvent.ChangedRandomOrderPlayback -> {
                _state.value = _state.value.copy(
                    randomOrderPlayback = event.randomOrderPlayback
                )
            }
        }
        saveConfiguration()
    }

    private fun getIndex(
        configComponents: List<ConfigComponent>,
        configComponent: ConfigComponent
    ): Int {
        for (i in configComponents.indices) {
            if (configComponents[i] === configComponent) {
                return i
            }
        }
        return -1
    }

    private fun saveConfiguration() {
        viewModelScope.launch {
            try {
                configurationUseCases.addConfiguration(
                    Configuration(
                        id = currentConfigurationId,
                        name = _state.value.name,
                        description = _state.value.description,
                        randomOrderPlayback = _state.value.randomOrderPlayback,
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
