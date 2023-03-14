package com.ispgr5.locationsimulator.presentation.homescreen

import com.ispgr5.locationsimulator.domain.model.Configuration

data class HomeScreenState(
    val favoriteConfigurations: List<Configuration> = emptyList(),
    val configurationsWithErrors:List<Configuration> = emptyList()
)