package com.ispgr5.locationsimulator.presentation.userSettings

data class UserSettingsState(
    val selectedUser: String = "User1",
    val availableConfigurations: List<String> = listOf("Lautes Atmen", "Husten", "Kratzen"),
    val selectedConfiguration: String? = null
)
