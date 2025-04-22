package com.ispgr5.locationsimulator.presentation.run

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.network.ClientSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * The ViewModel for the Run Screen
 */
@HiltViewModel
class RunViewModel @Inject constructor(
	private val configurationUseCases: ConfigurationUseCases,
	savedStateHandler: SavedStateHandle
) : ViewModel() {

	private val _state = mutableStateOf(RunScreenState())
	val state: State<RunScreenState> = _state

	init {
		savedStateHandler.get<String>("configurationId")?.let { configID ->
			val id = configID.toInt()
			if(id == -1) {	// remote
				savedStateHandler.get<String>("configStr")?.let {
					val conf = Json.decodeFromString<Configuration?>(it)
					_state.value = _state.value.copy(
						configuration = conf
					)
				}
			} else {
				viewModelScope.launch {
					configurationUseCases.getConfiguration(id)?.let { conf ->
						_state.value = _state.value.copy(
							configuration = conf
						)
					}
				}
			}
		}
	}

	/**
	 * Handles UI Events
	 */
	fun onEvent(event: RunEvent) {
		when (event) {
			is RunEvent.StopClicked -> {
				event.stopServiceFunction()
			}
		}
	}
}

data class RunScreenState(
	val configuration: Configuration? = null
)