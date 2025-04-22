package com.ispgr5.locationsimulator.presentation.exportSettings

sealed class ExportSettingsEvent {
	data class SelectConfiguration(val configName: String) : ExportSettingsEvent()
	object ExportConfiguration : ExportSettingsEvent()
}

