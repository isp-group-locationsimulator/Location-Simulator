@file:Suppress("TestFunctionName")

package com.ispgr5.locationsimulator.screenshots

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineScreenshotPreview
import com.ispgr5.locationsimulator.presentation.editTimeline.components.VibrationSupportHintMode
import com.ispgr5.locationsimulator.presentation.homescreen.HomeScreenScaffold
import com.ispgr5.locationsimulator.presentation.homescreen.HomeScreenState
import com.ispgr5.locationsimulator.presentation.run.RunScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.screenshotData.ScreenshotData.configurations
import com.ispgr5.locationsimulator.presentation.select.SelectScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.settings.SettingsPages
import com.ispgr5.locationsimulator.presentation.settings.SettingsScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.sound.SoundScreenScreenshotPreview
import com.ispgr5.locationsimulator.ui.theme.ThemeState

data class ScreenshotScope(
    val screenshotName: String, val theme: ThemeState
)

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