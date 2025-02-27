package com.ispgr5.locationsimulator.presentation.userSettings

import com.ispgr5.locationsimulator.domain.model.Configuration

data class UserSettingsState(
    val selectedUser: String = "User1",
    val availableConfigurations: List<Configuration> = emptyList(),
    val selectedConfiguration: Int? = null
)
