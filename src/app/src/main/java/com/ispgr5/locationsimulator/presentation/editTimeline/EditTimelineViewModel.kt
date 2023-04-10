package com.ispgr5.locationsimulator.presentation.editTimeline


import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.model.*
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.presentation.settings.SettingsState
import com.ispgr5.locationsimulator.presentation.util.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View Model of the Edit Screen
 */
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
                    configurationUseCases.getConfiguration(configurationId)
                        ?.also { configuration ->
                            var newSound: Sound? = null
                            var components = configuration.components


                            savedStateHandle.get<String>("soundNameToAdd")?.let { soundSource ->
                                if (soundSource != "") {

                                    val minVolume =  savedStateHandle.get<Float>("minVolume")
                                    val maxVolume =  savedStateHandle.get<Float>("maxVolume")
                                    val minPause =  savedStateHandle.get<Int>("minPause")
                                    val maxPause =  savedStateHandle.get<Int>("maxPause")

                                    val componentsCopy = configuration.components.toMutableList()
                                    newSound = Sound(
                                        (componentsCopy.maxByOrNull { it.id }?.id ?: 0) + 1,
                                        soundSource,soundSource, minVolume!!, maxVolume!!, minPause!!, maxPause!!
                                    )
                                    componentsCopy.add(
                                        newSound!!
                                    )
                                    configurationUseCases.addConfiguration(
                                        Configuration(
                                            id = configurationId,
                                            name = configuration.name,
                                            description = configuration.description,
                                            randomOrderPlayback = configuration.randomOrderPlayback,
                                            components = componentsCopy
                                        )
                                    )
                                    components = componentsCopy
                                }
                            }
                            currentConfigurationId = configuration.id
                            _state.value = _state.value.copy(
                                name = configuration.name,
                                description = configuration.description,
                                randomOrderPlayback = configuration.randomOrderPlayback,
                                components = components,
                                current = newSound
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
                        components = state.value.components.map { if (it === state.value.current) component else it },
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
                        components = state.value.components.map { if (it === state.value.current) component else it },
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
                                minStrength = RangeConverter.floatToEightBitInt(event.range.start),
                                maxStrength = RangeConverter.floatToEightBitInt(event.range.endInclusive)
                            )
                        }
                    }
                    _state.value = _state.value.copy(
                        components = state.value.components.map { if (it === state.value.current) component else it },
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
                        components = state.value.components.map { if (it === state.value.current) component else it },
                        current = component
                    )
                }
            }
            is EditTimelineEvent.ChangeConfigComponentName -> {
                if (state.value.current == null){return}
                viewModelScope.launch {
                    var component = state.value.current!!.copy()
                    when (component) {
                        is Vibration -> {
                            component = (component as Vibration).myCopy(
                                name = event.name
                            )
                        }
                        is Sound -> {
                            component = (component as Sound).myCopy(
                                name = event.name
                            )
                        }
                    }
                    _state.value = _state.value.copy(
                        components = state.value.components.map { if (it === state.value.current) component else it },
                        current = component
                    )
                }
            }
            is EditTimelineEvent.AddSound -> {
                viewModelScope.launch {
                    event.navController.navigate( Screen.SoundScreen.createRoute(currentConfigurationId!!))
                }
            }
            is EditTimelineEvent.AddVibration -> {
                viewModelScope.launch {
                    val defaultValues: SettingsState = event.getDefaultValuesFunction()
                    val vibration = Vibration(
                        id = (state.value.components.maxByOrNull { it.id }?.id ?: 0) + 1,
                        name = defaultValues.defaultNameVibration,
                        minStrength = defaultValues.minStrengthVibration,
                        maxStrength = defaultValues.maxStrengthVibration,
                        minPause = defaultValues.minPauseVibration,
                        maxPause = defaultValues.maxPauseVibration,
                        minDuration = defaultValues.minDurationVibration,
                        maxDuration = defaultValues.maxDurationVibration
                    )
                    val listC = state.value.components.toMutableList()
                    listC.add(vibration)
                    currentConfigurationId = vibration.id
                    _state.value = _state.value.copy(
                        components = listC, current = vibration
                    )
                }
            }
            is EditTimelineEvent.SelectedTimelineItem -> {
                _state.value = _state.value.copy(
                    current = if (event.selectConfigComp === state.value.current) {
                        null
                    } else {
                        event.selectConfigComp
                    }
                )
            }

            is EditTimelineEvent.ChangedName -> {
                //Character limit of 50
                _state.value = _state.value.copy(
                    name = event.name.take(50)
                )
            }

            is EditTimelineEvent.ChangedDescription -> {
                //Character Limit of 300
                _state.value = _state.value.copy(
                    description = event.description.take(300)
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
            is EditTimelineEvent.CopyConfigComponent -> {
                if (state.value.current == null){return}
                viewModelScope.launch {
                    val componentsListCopy = state.value.components.toMutableList()
                    var component = state.value.current!!.copy()
                    when (component) {
                        is Vibration -> {
                            component = (component as Vibration).myCopy(
                                id = (componentsListCopy.maxByOrNull { it.id }?.id ?: 0) + 1
                            )
                        }
                        is Sound -> {
                            component = (component as Sound).myCopy(
                                id = (componentsListCopy.maxByOrNull { it.id }?.id ?: 0) + 1
                            )
                        }
                    }
                    componentsListCopy.add(component)
                    _state.value = _state.value.copy(
                        components = componentsListCopy,
                        current = component
                    )
                }
            }
        }
        saveConfiguration()
    }

    /**
     * Returns the index a configComponent has in a list of ConfigComponents
     */
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

    /**
     * Save a Configuration
     */
    private fun saveConfiguration( ) {
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
            }
        }
    }
}
