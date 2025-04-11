package com.ispgr5.locationsimulator.presentation.userSettings

sealed class UserSettingsEvent {
	data class SelectConfiguration(val configID: Int) : UserSettingsEvent()
}

