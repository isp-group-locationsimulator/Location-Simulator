package com.ispgr5.locationsimulator.presentation.connection

enum class ConnectionStatus {
    CONNECTING,
    SUCCESS,
    FAILED
}

data class ConnectionScreenState(
    val connectionStatus: ConnectionStatus = ConnectionStatus.CONNECTING
)