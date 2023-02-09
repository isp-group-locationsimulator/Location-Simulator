package com.ispgr5.locationsimulator.presentation.editTimeline

import com.ispgr5.locationsimulator.domain.model.ConfigComponent

sealed class EditTimelineEvent {
    data class ChangedSoundVolume( val range: ClosedFloatingPointRange<Float>) : EditTimelineEvent()
    data class ChangedPause(val range: ClosedFloatingPointRange<Float>) : EditTimelineEvent()
    data class ChangedVibStrength(val range: ClosedFloatingPointRange<Float>) : EditTimelineEvent()
    data class ChangedVibDuration(val range: ClosedFloatingPointRange<Float>) : EditTimelineEvent()
    data class SelectedTimelineItem(val selectConfigComp: ConfigComponent) : EditTimelineEvent()
    data class ChangedName(val name: String) : EditTimelineEvent()
    data class ChangedDescription(val description: String) : EditTimelineEvent()
    object AddSound : EditTimelineEvent()
    object AddVibration : EditTimelineEvent()
    data class DeleteConfigurationComponent(val configComponent: ConfigComponent):EditTimelineEvent()
    data class MoveConfCompUp(val configComponent: ConfigComponent):EditTimelineEvent()
    data class MoveConfCompDown(val configComponent: ConfigComponent):EditTimelineEvent()
    data class RecordAudio(val recordAudio : () -> Unit) : EditTimelineEvent()
}