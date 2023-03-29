package com.ispgr5.locationsimulator.presentation.settings


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.model.RangeConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for the SettingsScreen
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(

) : ViewModel() {

    //The provided state for the View
    private val _state = mutableStateOf(SettingsState())
    val state: State<SettingsState> = _state


    /**
     * handles ui Events
     */
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ChangedSoundVolume -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        minVolumeSound = RangeConverter.transformPercentageToFactor(event.range.start),
                        maxVolumeSound = RangeConverter.transformPercentageToFactor(event.range.endInclusive)
                    )
                    event.saveDefaultValuesFunction(state)
                }
            }
            is SettingsEvent.ChangedSoundPause -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        minPauseSound = RangeConverter.sToMs(event.range.start),
                        maxPauseSound = RangeConverter.sToMs(event.range.endInclusive)
                    )
                    event.saveDefaultValuesFunction(state)
                }
            }
            is SettingsEvent.ChangedVibStrength -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        minStrengthVibration = RangeConverter.floatTo8BitInt(event.range.start),
                        maxStrengthVibration = RangeConverter.floatTo8BitInt(event.range.endInclusive)
                    )
                    event.saveDefaultValuesFunction(state)
                }
            }
            is SettingsEvent.ChangedVibDuration -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        minDurationVibration = RangeConverter.sToMs(event.range.start),
                        maxDurationVibration = RangeConverter.sToMs(event.range.endInclusive)
                    )
                    event.saveDefaultValuesFunction(state)
                }
            }
            is SettingsEvent.ChangedVibPause -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        minPauseVibration = RangeConverter.sToMs(event.range.start),
                        maxPauseVibration = RangeConverter.sToMs(event.range.endInclusive)
                    )
                    event.saveDefaultValuesFunction(state)
                }
            }
            is SettingsEvent.EnteredName -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        defaultNameVibration = event.name
                    )
                    event.saveDefaultValuesFunction(state)
                }
            }
        }
    }
}