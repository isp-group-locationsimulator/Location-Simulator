package com.ispgr5.locationsimulator.presentation.connection

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ispgr5.locationsimulator.network.ClientSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * The ViewModel for the DelayScreen
 */
@HiltViewModel
class ConnectionViewModel @Inject constructor(
    //saveStateHandle is required to get the navigation Arguments like configurationId
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val state = MutableLiveData(ConnectionScreenState())

    private var tryThread = Thread()
    private val name = savedStateHandle.get<String>("userName") ?: ""

    init {
        tryServerConnection()
    }

    fun tryServerConnection() {
        ClientSingleton.close()
        tryThread.join()
        state.postValue(ConnectionScreenState(ConnectionStatus.CONNECTING))

        tryThread = Thread {
            if (ClientSingleton.start(name)) {
                state.postValue(ConnectionScreenState(ConnectionStatus.SUCCESS))
            } else {
                state.postValue(ConnectionScreenState(ConnectionStatus.FAILED))
            }
        }
        tryThread.start()
    }
}