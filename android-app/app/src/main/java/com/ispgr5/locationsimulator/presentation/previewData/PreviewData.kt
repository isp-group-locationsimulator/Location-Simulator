package com.ispgr5.locationsimulator.presentation.previewData

import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.add.AddScreenState
import com.ispgr5.locationsimulator.presentation.delay.DelayScreenState
import com.ispgr5.locationsimulator.presentation.delay.TimerState
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineState
import com.ispgr5.locationsimulator.presentation.run.RunscreenPreviewData
import com.ispgr5.locationsimulator.presentation.select.SelectScreenState
import com.ispgr5.locationsimulator.presentation.settings.DefaultShippingSettings
import com.ispgr5.locationsimulator.presentation.settings.SettingsState
import com.ispgr5.locationsimulator.presentation.sound.SoundState
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType


object PreviewData {


    val themePreviewState = ThemeState(themeType = ThemeType.AUTO)

    private val defaultVibration = ConfigComponent.Vibration(
        id = 1,
        name = DefaultShippingSettings.DEFAULT_NAME_VIBRATION,
        minStrength = DefaultShippingSettings.MIN_STRENGTH_VIBRATION,
        maxStrength = DefaultShippingSettings.MAX_STRENGTH_VIBRATION,
        minPause = DefaultShippingSettings.MIN_PAUSE_VIBRATION,
        maxPause = DefaultShippingSettings.MAX_PAUSE_VIBRATION,
        minDuration = DefaultShippingSettings.MIN_DURATION_VIBRATION,
        maxDuration = DefaultShippingSettings.MAX_DURATION_VIBRATION
    )
    private val defaultSound = ConfigComponent.Sound(
        id = 2,
        name = "Sound",
        source = "barking.mp3",
        maxPause = DefaultShippingSettings.MAX_PAUSE_SOUND,
        minPause = DefaultShippingSettings.MIN_PAUSE_SOUND,
        minVolume = DefaultShippingSettings.MAX_VOLUME_SOUND,
        maxVolume = DefaultShippingSettings.MIN_VOLUME_SOUND
    )
    val previewConfigurations: List<Configuration> = listOf(
        Configuration(
            id = 1,
            name = "Default configuration",
            description = "The default configuration of the app as shipped",
            randomOrderPlayback = false,
            components = listOf(defaultVibration, defaultVibration),
            isFavorite = true
        ),
        Configuration(
            id = 2,
            name = "With sound",
            description = "A configuration with vibrations and sound",
            randomOrderPlayback = true,
            components = listOf(defaultVibration, defaultSound),
            isFavorite = false
        )
    )
    val selectScreenPreviewState = SelectScreenState(
        configurations = previewConfigurations,
        toggledConfiguration = previewConfigurations.first(),
        isInDeleteMode = false,
        selectedConfigurationForDeletion = null,
        configurationsWithErrors = listOf()
    )

    val selectScreenPreviewStateDelete = selectScreenPreviewState.copy(
        isInDeleteMode = true, selectedConfigurationForDeletion = previewConfigurations.first()
    )


    val addScreenPreviewState: AddScreenState = AddScreenState(
        name = "foo", description = "", randomOrderPlayback = false, components = emptyList()
    )

    val settingsScreenPreviewState = SettingsState()

    val delayScreenPreviewState = DelayScreenState(
        configuration = previewConfigurations.first()
    )

    val delayScreenInitialTimerState: TimerState = TimerState(setSeconds = 42L)

    val runScreenPreviewInitialRefresh = RunscreenPreviewData.baselineInstant
    val runScreenPreviewStatePaused = RunscreenPreviewData.effectTimelinePausedState
    val runScreenPreviewStatePlaying = RunscreenPreviewData.effectTimelinePlayingState

    val editTimelineState = EditTimelineState(
        name = previewConfigurations.first().name,
        description = previewConfigurations.first().description,
        randomOrderPlayback = previewConfigurations.first().randomOrderPlayback,
        components = previewConfigurations.first().components,
        current = previewConfigurations.first().components.first()
    )

    val soundScreenPreviewState = SoundState(
        soundNames = listOf("breathing.mp3", "barking.mp3", "coughing.mp3")
    )
}