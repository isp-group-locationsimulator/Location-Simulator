package com.example.mvvmtest.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmtest.model.Configuration
import com.example.mvvmtest.model.ConfigurationRepository
import com.example.mvvmtest.model.memory.MemoryInterface
import com.example.mvvmtest.view.selectScreen.selectScreenState
import kotlinx.coroutines.launch

/**
 * This class ist the Main View Model
 * it contains the current Configuration
 * and getter for the different viewModels
 */
class MainViewModel : ViewModel() {

    private var currentSelectedConfiguration:Int  = 0

    private var repository = ConfigurationRepository()

    private val _state = mutableStateOf(
        selectScreenState(
            configuration = repository.returnConfiguration()
        )
    )

    val state: State<selectScreenState> get() = _state


    private var alleConfiguration: Array<Configuration> = MemoryInterface().getAllConfigrurationFromMemory()

    init {
        if (this.alleConfiguration.isNotEmpty()) {
            repository.replaceConfiguration(alleConfiguration[this.currentSelectedConfiguration])
        }
        viewModelScope.launch {
            _state.value = state.value.copy(
                configuration = repository.returnConfiguration()
            )
        }
    }

    fun replaceStateConfigurationEntry(
        durationVibrateInSec: Int? = null,
        durationPauseVibrateInSec: Int? = null
    ){
        viewModelScope.launch {
            repository.replaceEntry(durationVibrateInSec = durationVibrateInSec, durationPauseVibrateInSec = durationPauseVibrateInSec)
            val updatedConfiguration = repository.returnConfiguration()
            _state.value = _state.value.copy(configuration = updatedConfiguration)
        }
    }

    fun replaceStateConfiguration(configuration: Configuration){
        viewModelScope.launch {
            repository.replaceConfiguration(configuration = configuration)
            val updatedConfiguration = repository.returnConfiguration()
            _state.value = _state.value.copy(configuration = updatedConfiguration)
        }
    }

    fun changeSelectedConfiguration(direction: Int){
        var newConfigurationNumber = this.currentSelectedConfiguration
        newConfigurationNumber += direction
        if (newConfigurationNumber >= 0 && newConfigurationNumber < this.alleConfiguration.size){
            try {
                val newConfiguration = this.alleConfiguration[newConfigurationNumber]
                this.currentSelectedConfiguration = newConfigurationNumber
                this.replaceStateConfiguration(newConfiguration)
            }catch (e: Exception){
                println("Die gewünschte Konfiguration scheint nicht zu existieren")
            }
        }
    }
}