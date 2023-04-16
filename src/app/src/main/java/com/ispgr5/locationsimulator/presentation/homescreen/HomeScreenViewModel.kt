package com.ispgr5.locationsimulator.presentation.homescreen

import android.content.SharedPreferences
import androidx.activity.ComponentActivity
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
import javax.inject.Inject

/**
 * The ViewModel for the DelayScreen
 */
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
	private val configurationUseCases: ConfigurationUseCases
) : ViewModel() {

	// The provided state for the View
	private val _state = mutableStateOf(HomeScreenState())
	val state: State<HomeScreenState> = _state

	//state of the Database fetch needed when we quickly change between Screens. Aboard two running fetchJobs
	private var getConfigurationJob: Job? = null

	/**
	 * Everytime we change to this Screen the Configurations will be fetched from Database
	 */
	init {
		//Aboard previous fetch if existing
		getConfigurationJob?.cancel()
		//Fetch the Configuration from Database
		getConfigurationJob = configurationUseCases.getFavoriteConfigurations()
			.onEach { configuration ->
				//update the state with the Configurations from Database
				_state.value = _state.value.copy(
					favoriteConfigurations = configuration
				)
			}
			//have to be in View model scope because Database request have to be called by Coroutine
			.launchIn(viewModelScope)
	}

	/**
	 * Handles UI Events
	 */
	fun onEvent(event: HomeScreenEvent) {
		when (event) {
			is HomeScreenEvent.SelectConfiguration -> {
				return
			}
			is HomeScreenEvent.DisableBatteryOptimization -> {
				event.batteryOptDisableFunction()
			}
			is HomeScreenEvent.ChangedAppTheme -> {
				event.darkTheme.value = event.darkTheme.value.copy(isDarkTheme = event.isDarkTheme)
				val editor: SharedPreferences.Editor = event.activity.getSharedPreferences("prefs", ComponentActivity.MODE_PRIVATE).edit()
				editor.putBoolean("isDarkTheme", event.isDarkTheme)
				editor.apply()
			}
		}
	}

	/**
	 * This function updated the Select Screen State by looking up the private dir
	 * with the saved Sounds and compare it with the Sound names in all Configurations.
	 * COPY FROM SELECTVIEWMODEL
	 */
	fun updateConfigurationWithErrorsState(soundStorageManager: SoundStorageManager) {
		val configurationsWithErrors = mutableListOf<Configuration>()
		val knownSounds = soundStorageManager.getSoundFileNames()
		for (conf in _state.value.favoriteConfigurations) {
			var hasErrors = false
			for (comp in conf.components) {
				if (comp is Sound) {
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
	 * COPY FROM SELECTVIEWMODEL
	 * @return unknown Sound names in the given configuration
	 */
	fun whatIsHisErrors(
		configuration: Configuration,
		soundStorageManager: SoundStorageManager
	): List<String> {
		val errors = mutableListOf<String>()
		val knownSounds = soundStorageManager.getSoundFileNames()
		for (comp in configuration.components) {
			if (comp is Sound) {
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