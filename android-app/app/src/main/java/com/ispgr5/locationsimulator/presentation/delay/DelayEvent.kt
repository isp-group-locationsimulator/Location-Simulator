package com.ispgr5.locationsimulator.presentation.delay

import com.ispgr5.locationsimulator.domain.model.ConfigComponent

/**
 * The UI Events the View can call
 */
sealed class DelayEvent {
    data class StartClicked(val startServiceFunction: (String, List<ConfigComponent>, Boolean) -> Unit) :
        DelayEvent()

    data class RemoteStart(
        val configStr: String,
        val startServiceFunction: (String, List<ConfigComponent>, Boolean) -> Unit
    ) : DelayEvent()

    data class TrainerStart(
        val hours: Long, val minutes: Long, val seconds: Long,
        val startServiceFunction: (String, List<ConfigComponent>, Boolean) -> Unit
    ) : DelayEvent()
}