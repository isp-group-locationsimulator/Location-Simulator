@file:Suppress("TestFunctionName")

package com.ispgr5.locationsimulator.screenshots

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.add.AddScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.add.AddScreenState
import com.ispgr5.locationsimulator.presentation.delay.DelayScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.delay.DelayScreenState
import com.ispgr5.locationsimulator.presentation.delay.TimerState
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineScreenshotPreview
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineState
import com.ispgr5.locationsimulator.presentation.editTimeline.components.VibrationSupportHintMode
import com.ispgr5.locationsimulator.presentation.homescreen.HomeScreenScaffold
import com.ispgr5.locationsimulator.presentation.homescreen.HomeScreenState
import com.ispgr5.locationsimulator.presentation.homescreen.InfoScreenScaffold
import com.ispgr5.locationsimulator.presentation.run.RunScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.run.RunscreenPreviewData
import com.ispgr5.locationsimulator.presentation.select.SelectScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.select.SelectScreenState
import com.ispgr5.locationsimulator.presentation.settings.DefaultShippingSettings
import com.ispgr5.locationsimulator.presentation.settings.SettingsPages
import com.ispgr5.locationsimulator.presentation.settings.SettingsScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.settings.SettingsState
import com.ispgr5.locationsimulator.presentation.sound.SoundScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.sound.SoundState
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.addScreenState
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.configurations
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.delayScreenInitialTimerState
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.delayScreenState
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.editTimelineState
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.runScreenInitialRefresh
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.runScreenStatePaused
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.runScreenStatePlaying
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.selectScreenState
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.selectScreenStateDelete
import com.ispgr5.locationsimulator.ui.theme.ThemeState

data class ScreenshotScope(
    val screenshotName: String, val theme: ThemeState
)

@Composable
@Preview
fun ScreenshotScope.HomeScreenScreenshot() {
    val state by remember {
        mutableStateOf(
            HomeScreenState(
                favoriteConfigurations = configurations.filter { it.isFavorite },
                configurationsWithErrors = emptyList()
            )
        )
    }
    val themeState = remember {
        mutableStateOf(this.theme)
    }
    HomeScreenScaffold(
        homeScreenState = state,
        appTheme = themeState,
        onInfoClick = {},
        onSelectProfile = {},
        onSelectFavourite = {},
        onSelectTheme = {},
        checkBatteryOptimizationStatus = { false },
        onLaunchBatteryOptimizerDisable = {}
    )
}

@Composable
@Preview
fun InfoScreenScreenshot() {
    InfoScreenScaffold(scaffoldState = rememberScaffoldState(), onBackClick = {})
}

@Composable
@Preview
fun SelectScreenNormalScreenshot() {
    SelectScreenScreenshotPreview(selectScreenState = selectScreenState)
}


@Composable
@Preview
fun SelectScreenDeleteModeScreenshot() {
    SelectScreenScreenshotPreview(selectScreenState = selectScreenStateDelete)
}

@Composable
@Preview
fun AddScreenScreenshot() {
    AddScreenScreenshotPreview(addScreenState = addScreenState)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun SettingsScreenVibrationScreenshot() {
    SettingsScreenScreenshotPreview(
        state = ScreenshotData.settingsScreenState,
        pagerState = rememberPagerState(initialPage = SettingsPages.Vibration.ordinal) {
            SettingsPages.entries.size
        })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun SettingsScreenSoundScreenshot() {
    SettingsScreenScreenshotPreview(
        state = ScreenshotData.settingsScreenState,
        pagerState = rememberPagerState(initialPage = SettingsPages.Sound.ordinal) {
            SettingsPages.entries.size
        })
}

@Composable
@Preview
fun DelayScreenScreenshot() {
    DelayScreenScreenshotPreview(
        state = delayScreenState,
        timerState = remember {
            mutableStateOf(delayScreenInitialTimerState)
        }
    )
}

@Composable
@Preview
fun RunScreenPausedScreenshot() {
    RunScreenScreenshotPreview(
        configuration = configurations.first(),
        effectTimelineState = runScreenStatePaused,
        initialRefreshInstant = runScreenInitialRefresh
    )
}

@Composable
@Preview
fun RunScreenActiveScreenshot() {
    RunScreenScreenshotPreview(
        configuration = configurations.first(),
        effectTimelineState = runScreenStatePlaying,
        initialRefreshInstant = runScreenInitialRefresh
    )
}

@Composable
@Preview
fun EditTimelineNormalScreenshot() {
    EditTimelineScreenshotPreview(
        isDialogShown = false, state = editTimelineState,
        vibrationSupportHintMode = VibrationSupportHintMode.SUPPRESSED
    )
}

@Composable
@Preview
fun EditTimelineDialogShownScreenshot() {
    EditTimelineScreenshotPreview(
        isDialogShown = true,
        state = editTimelineState,
        vibrationSupportHintMode = VibrationSupportHintMode.SUPPRESSED
    )
}

@Composable
@Preview
fun EditTimelineUnsupportedIntensityScreenshot() {
    EditTimelineScreenshotPreview(
        isDialogShown = false,
        state = editTimelineState,
        vibrationSupportHintMode = VibrationSupportHintMode.ENFORCED
    )
}

@Composable
@Preview
fun SoundScreenScreenshot() {
    SoundScreenScreenshotPreview(
        state = ScreenshotData.soundScreenState,
        currentPlayingSoundName = ScreenshotData.soundScreenState.soundNames.first()
    )
}

@Composable
@Preview
fun SoundScreenStoppedScreenshot() {
    SoundScreenScreenshotPreview(
        state = ScreenshotData.soundScreenState
    )
}

@Composable
@Preview
fun SoundScreenForDeletionScreenshot() {
    SoundScreenScreenshotPreview(
        state = ScreenshotData.soundScreenState,
        selectedForDeletion = ScreenshotData.soundScreenState.soundNames.last()
    )
}

private object ScreenshotData {
    val defaultVibration = ConfigComponent.Vibration(
        id = 1,
        name = DefaultShippingSettings.DEFAULT_NAME_VIBRATION,
        minStrength = DefaultShippingSettings.MIN_STRENGTH_VIBRATION,
        maxStrength = DefaultShippingSettings.MAX_STRENGTH_VIBRATION,
        minPause = DefaultShippingSettings.MIN_PAUSE_VIBRATION,
        maxPause = DefaultShippingSettings.MAX_PAUSE_VIBRATION,
        minDuration = DefaultShippingSettings.MIN_DURATION_VIBRATION,
        maxDuration = DefaultShippingSettings.MAX_DURATION_VIBRATION
    )
    val defaultSound = ConfigComponent.Sound(
        id = 2,
        name = "Sound",
        source = "barking.mp3",
        maxPause = DefaultShippingSettings.MAX_PAUSE_SOUND,
        minPause = DefaultShippingSettings.MIN_PAUSE_SOUND,
        minVolume = DefaultShippingSettings.MAX_VOLUME_SOUND,
        maxVolume = DefaultShippingSettings.MIN_VOLUME_SOUND
    )
    val configurations: List<Configuration> = listOf(
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
    val selectScreenState = SelectScreenState(
        configurations = configurations,
        toggledConfiguration = configurations.first(),
        isInDeleteMode = false,
        selectedConfigurationForDeletion = null,
        configurationsWithErrors = listOf()
    )

    val selectScreenStateDelete = selectScreenState.copy(
        isInDeleteMode = true, selectedConfigurationForDeletion = configurations.first()
    )


    val addScreenState: AddScreenState = AddScreenState(
        name = "foo", description = "", randomOrderPlayback = false, components = emptyList()
    )

    val settingsScreenState = SettingsState()

    val delayScreenState = DelayScreenState(
        configuration = configurations.first()
    )

    val delayScreenInitialTimerState: TimerState = TimerState(setSeconds = 42L)

    val runScreenInitialRefresh = RunscreenPreviewData.baselineInstant
    val runScreenStatePaused = RunscreenPreviewData.effectTimelinePausedState
    val runScreenStatePlaying = RunscreenPreviewData.effectTimelinePlayingState

    val editTimelineState = EditTimelineState(
        name = configurations.first().name,
        description = configurations.first().description,
        randomOrderPlayback = configurations.first().randomOrderPlayback,
        components = configurations.first().components,
        current = configurations.first().components.first()
    )

    val soundScreenState = SoundState(
        soundNames = listOf("breathing.mp3", "barking.mp3", "coughing.mp3")
    )
}