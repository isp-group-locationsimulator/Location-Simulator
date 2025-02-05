package com.ispgr5.locationsimulator.presentation.userSettings

sealed class UserSettingsEvent {
	data class SelectConfiguration(val configName: String) : UserSettingsEvent()
	object ExportConfiguration : UserSettingsEvent()
	object SaveSettings : UserSettingsEvent()
}

