package com.ispgr5.locationsimulator.presentation.editTimeline

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.model.Sound
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
        /*
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
        }*/

        //create Configuration
        val sound1 = Sound("Schrei", 4, 80, 3, 5, true);
        val sound2 = Sound("Klopfen", 4, 5, 3, 5, true);
        val sound3 = Sound("Singen", 4, 5, 3, 5, true);
        val components = ArrayList<ConfigComponent>()
        components.add(sound1);
        components.add(sound2);
        components.add(sound3);
        val configuration = Configuration("config1", "beschreibung", components);

        _state.value = _state.value.copy(
            name = configuration.name,
            description = configuration.description,
            components = configuration.components,
            current = sound1
        )
    }

    /**
     * handles ui Events
     */
    fun onEvent(event: EditTimelineEvent) {
        when (event) {
            is EditTimelineEvent.ChangedSoundVolume -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        volumeMax = event.range.endInclusive,
                        volumeMin = event.range.start
                    )
                }
            }
            is EditTimelineEvent.ChangedPause -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        pauseMax = event.range.endInclusive,
                        pauseMin = event.range.start
                    )
                }
            }
            is EditTimelineEvent.SelectedTimelineItem -> {

                val selectedConfigComp = event.selectConfigComp
                saveCurrentConfigComp()
                when (selectedConfigComp) {
                    is Sound -> {
                        viewModelScope.launch {
                            _state.value = _state.value.copy(
                                pauseMax = selectedConfigComp.maxPause.toFloat(),
                                pauseMin = selectedConfigComp.minPause.toFloat(),
                                volumeMax = selectedConfigComp.maxVolume.toFloat(),
                                volumeMin = selectedConfigComp.minVolume.toFloat(),
                                current = selectedConfigComp
                            )
                        }
                    }
                }

            }
        }
    }

    private fun saveCurrentConfigComp(){
        val current = _state.value.current
        when (current){
            is Sound -> {
                current.minVolume = _state.value.volumeMin.toInt()
                current.maxVolume = _state.value.volumeMax.toInt()
                current.minPause = _state.value.pauseMin.toInt()
                current.maxPause = _state.value.pauseMax.toInt()
                //TODO
            }
        }

    }
}
