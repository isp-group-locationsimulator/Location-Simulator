package com.ispgr5.locationsimulator.presentation.editTimeline

import androidx.navigation.NavController
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.presentation.settings.SettingsState

/**
 * Events which can happen in the Edit Screen
 */
sealed class EditTimelineEvent {
	data class ChangedSoundVolume(val range: ClosedFloatingPointRange<Float>) : EditTimelineEvent()
	data class ChangedPause(val range: ClosedFloatingPointRange<Float>) : EditTimelineEvent()
	data class ChangedVibStrength(val range: ClosedFloatingPointRange<Float>) : EditTimelineEvent()
	data class ChangedVibDuration(val range: ClosedFloatingPointRange<Float>) : EditTimelineEvent()
	data class SelectedTimelineItem(val selectConfigComp: ConfigComponent) : EditTimelineEvent()
	data class ChangedName(val name: String) : EditTimelineEvent()
	data class ChangedDescription(val description: String) : EditTimelineEvent()
	data class AddSound(val navController: NavController) : EditTimelineEvent()
	class AddVibration(val getDefaultValuesFunction: () -> SettingsState) : EditTimelineEvent()
	data class DeleteConfigurationComponent(val configComponent: ConfigComponent) : EditTimelineEvent()
	data class MoveConfCompLeft(val configComponent: ConfigComponent) : EditTimelineEvent()
	data class MoveConfCompRight(val configComponent: ConfigComponent) : EditTimelineEvent()
	data class ChangedRandomOrderPlayback(val randomOrderPlayback: Boolean) : EditTimelineEvent()
	data class ChangeConfigComponentName(val name: String) : EditTimelineEvent()
	data class CopyConfigComponent(val configComponent: ConfigComponent) : EditTimelineEvent()
}