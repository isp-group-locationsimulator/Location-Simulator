package com.ispgr5.locationsimulator.presentation.editTimeline

import androidx.navigation.NavController
import com.ispgr5.locationsimulator.domain.model.ConfigComponent

sealed class EditTimelineEvent {
    data class ChangedSoundVolume( val range: ClosedFloatingPointRange<Float>) : EditTimelineEvent()
    data class ChangedPause(val range: ClosedFloatingPointRange<Float>) : EditTimelineEvent()
    data class ChangedVibStrength(val range: ClosedFloatingPointRange<Float>) : EditTimelineEvent()
    data class ChangedVibDuration(val range: ClosedFloatingPointRange<Float>) : EditTimelineEvent()
    data class SelectedTimelineItem(val selectConfigComp: ConfigComponent) : EditTimelineEvent()
    data class ChangedName(val name: String) : EditTimelineEvent()
    data class ChangedDescription(val description: String) : EditTimelineEvent()
    data class AddSound(val navController: NavController) : EditTimelineEvent()
    object AddVibration : EditTimelineEvent()
    data class DeleteConfigurationComponent(val configComponent: ConfigComponent):EditTimelineEvent()
    data class MoveConfCompLeft(val configComponent: ConfigComponent):EditTimelineEvent()
    data class MoveConfCompRight(val configComponent: ConfigComponent):EditTimelineEvent()
    data class ChangedRandomOrderPlayback(val randomOrderPlayback: Boolean) : EditTimelineEvent()
}