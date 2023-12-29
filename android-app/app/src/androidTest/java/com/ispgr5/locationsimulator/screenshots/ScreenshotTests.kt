package com.ispgr5.locationsimulator.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import com.ispgr5.locationsimulator.di.AppModule
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.homescreen.HomeScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.settings.DefaultShippingSettings
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy
import tools.fastlane.screengrab.locale.LocaleTestRule

@HiltAndroidTest
@UninstallModules(AppModule::class)
class ScreenshotTests {

    @get:Rule(order = 0)
    val hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Rule(order = 2)
    @JvmField
    val localeTestRule = LocaleTestRule()

    @Before
    fun init() {
        hiltAndroidRule.inject()
        Screengrab.setDefaultScreenshotStrategy(
            UiAutomatorScreenshotStrategy()
        )
    }

    @Test
    fun homeScreenLight() {
        screenshotLight("home_screen") {
            HomeScreenScreenshotPreview(
                themeState = ThemeState(ThemeType.LIGHT),
                configurations = ScreenshotData.configurations
            )
        }
    }

    @Test
    fun homeScreenDark() {
        screenshotDark("home_screen") {
            HomeScreenScreenshotPreview(
                themeState = ThemeState(ThemeType.DARK),
                configurations = ScreenshotData.configurations.filter { it.isFavorite }
            )
        }
    }

    private fun screenshot(
        theme: ThemeType,
        screenshotName: String,
        content: @Composable () -> Unit
    ) {
        composeTestRule.setContent {
            LocationSimulatorTheme(themeState = ThemeState(theme)) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    content()
                }
            }
        }
        Screengrab.screenshot("$screenshotName-${theme.name}")
    }

    private fun screenshotLight(screenshotName: String, content: @Composable () -> Unit) {
        screenshot(ThemeType.LIGHT, screenshotName, content)
    }

    private fun screenshotDark(screenshotName: String, content: @Composable () -> Unit) {
        screenshot(ThemeType.DARK, screenshotName, content)
    }
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
    val configurations: List<Configuration> = listOf(
        Configuration(
            name = "Default configuration",
            description = "The default configuration of the app as shipped",
            randomOrderPlayback = false,
            components = listOf(defaultVibration, defaultVibration),
            isFavorite = true
        )
    )
}