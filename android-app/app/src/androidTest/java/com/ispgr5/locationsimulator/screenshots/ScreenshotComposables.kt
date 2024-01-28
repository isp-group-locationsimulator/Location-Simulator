@file:Suppress("TestFunctionName")

package com.ispgr5.locationsimulator.screenshots

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.add.AddScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.add.AddScreenState
import com.ispgr5.locationsimulator.presentation.delay.DelayScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.delay.DelayScreenState
import com.ispgr5.locationsimulator.presentation.delay.TimerState
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
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.configurations
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.selectScreenState
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.selectScreenStateDelete
import com.ispgr5.locationsimulator.screenshots.ScreenshotData.themeState
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType

data class ScreenshotScope(
    val screenshotName: String, val theme: ThemeState, val testLocale: String
)

@Composable
@Preview
fun HomeScreenScreenshot() {
    LocationSimulatorTheme(themeState = themeState) {
        val appTheme = remember { mutableStateOf(themeState) }
        val state by remember {
            mutableStateOf(
                HomeScreenState(
                    favoriteConfigurations = configurations.filter { it.isFavorite },
                    configurationsWithErrors = emptyList()
                )
            )
        }
        HomeScreenScaffold(
            homeScreenState = state,
            appTheme = appTheme,
            onInfoClick = {},
            onSelectProfile = {},
            onSelectFavourite = {},
            onSelectTheme = {},
            checkBatteryOptimizationStatus = { false },
            onLaunchBatteryOptimizerDisable = {}
        )
    }
}

@Composable
@Preview
fun InfoScreenScreenshot() {
    LocationSimulatorTheme(themeState = themeState) {
        InfoScreenScaffold(scaffoldState = rememberScaffoldState(), onBackClick = {})
    }
}

@Composable
@Preview
fun SelectScreenNormalScreenshot() {
    LocationSimulatorTheme(themeState = themeState) {
        SelectScreenScreenshotPreview(selectScreenState = selectScreenState)
    }
}


@Composable
@Preview
fun SelectScreenDeleteModeScreenshot() {
    LocationSimulatorTheme(themeState = themeState) {
        SelectScreenScreenshotPreview(selectScreenState = selectScreenStateDelete)
    }
}

@Composable
@Preview
fun AddScreenScreenshot() {
    LocationSimulatorTheme(themeState = themeState) {
        AddScreenScreenshotPreview(addScreenState = ScreenshotData.addScreenState)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun SettingsScreenVibrationScreenshot() {
    LocationSimulatorTheme(themeState = themeState) {
        SettingsScreenScreenshotPreview(
            state = ScreenshotData.settingsScreenState,
            pagerState = rememberPagerState(initialPage = SettingsPages.Vibration.ordinal) {
                SettingsPages.entries.size
            })
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun SettingsScreenSoundScreenshot() {
    LocationSimulatorTheme(themeState = themeState) {
        SettingsScreenScreenshotPreview(
            state = ScreenshotData.settingsScreenState,
            pagerState = rememberPagerState(initialPage = SettingsPages.Sound.ordinal) {
                SettingsPages.entries.size
            })
    }
}

@Composable
@Preview
fun DelayScreenScreenshot() {
    LocationSimulatorTheme(themeState = themeState) {
        DelayScreenScreenshotPreview(
            state = ScreenshotData.delayScreenState,
            initialTimerState = ScreenshotData.delayScreenInitialTimerState
        )
    }
}

@Composable
@Preview
fun RunScreenPausedScreenshot() {
    LocationSimulatorTheme(themeState = themeState) {
        RunScreenScreenshotPreview(
            configuration = configurations.first(),
            effectTimelineState = ScreenshotData.runScreenStatePaused,
            initialRefreshInstant = ScreenshotData.runScreenInitialRefresh
        )
    }
}

@Composable
@Preview
fun RunScreenActiveScreenshot() {
    LocationSimulatorTheme(themeState = themeState) {
        RunScreenScreenshotPreview(
            configuration = configurations.first(),
            effectTimelineState = ScreenshotData.runScreenStatePlaying,
            initialRefreshInstant = ScreenshotData.runScreenInitialRefresh
        )
    }
}

@Suppress("TestFunctionName")
@Composable
fun ScreenshotScope.Placeholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan.copy(alpha = 0.2f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = theme.themeType.labelStringRes),
            style = MaterialTheme.typography.h6
        )
        Text(
            text = screenshotName,
            style = MaterialTheme.typography.h5.copy(fontFamily = FontFamily.Monospace)
        )
        Text(
            text = testLocale,
            style = MaterialTheme.typography.h6.copy(fontFamily = FontFamily.Monospace)
        )
    }
}


private object ScreenshotData {
    val themeState = ThemeState(ThemeType.LIGHT)
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
        source = "Bellen.mp3",
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
}