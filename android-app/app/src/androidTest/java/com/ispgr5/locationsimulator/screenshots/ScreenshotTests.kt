package com.ispgr5.locationsimulator.screenshots

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.font.FontFamily
import com.ispgr5.locationsimulator.di.AppModule
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.add.AddScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.add.AddScreenState
import com.ispgr5.locationsimulator.presentation.homescreen.HomeScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.homescreen.InfoScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.select.SelectScreenScreenshotPreview
import com.ispgr5.locationsimulator.presentation.select.SelectScreenState
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

    private fun screenshot(
        theme: ThemeType,
        screenshotName: String,
        content: @Composable () -> Unit
    ) {
        composeTestRule.setContent {
            LocationSimulatorTheme(themeState = ThemeState(theme)) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    content()
                }
            }
        }
        Thread.sleep(100L)
        Screengrab.screenshot("$screenshotName-${theme.name}")
    }

    private fun screenshotLight(screenshotName: String, content: @Composable () -> Unit) {
        screenshot(ThemeType.LIGHT, screenshotName, content)
    }

    private fun screenshotDark(screenshotName: String, content: @Composable () -> Unit) {
        screenshot(ThemeType.DARK, screenshotName, content)
    }

    @Test
    fun homeScreenLight() {
        screenshotLight("home_screen") {
            HomeScreenScreenshotPreview(
                themeState = ThemeState(ThemeType.LIGHT),
                configurations = ScreenshotData.configurations.filter { it.isFavorite }
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

    @Test
    fun infoScreenLight() {
        screenshotLight("info_screen") {
            InfoScreenScreenshotPreview()
        }
    }

    @Test
    fun infoScreenDark() {
        screenshotDark("info_screen") {
            InfoScreenScreenshotPreview()
        }
    }

    @Test
    fun selectScreenNormalLight() {
        screenshotLight("select_screen_normal") {
            SelectScreenScreenshotPreview(selectScreenState = ScreenshotData.selectScreenState)
        }
    }

    @Test
    fun selectScreenNormalDark() {
        screenshotDark("select_screen_normal") {
            SelectScreenScreenshotPreview(selectScreenState = ScreenshotData.selectScreenState)
        }
    }

    @Test
    fun selectScreenDeleteLight() {
        screenshotLight("select_screen_delete") {
            SelectScreenScreenshotPreview(selectScreenState = ScreenshotData.selectScreenStateDelete)
        }
    }

    @Test
    fun selectScreenDeleteDark() {
        screenshotDark("select_screen_delete") {
            SelectScreenScreenshotPreview(selectScreenState = ScreenshotData.selectScreenStateDelete)
        }
    }

    @Test
    fun addScreenLight() {
        screenshotLight("add_screen") {
            AddScreenScreenshotPreview(
                addScreenState = ScreenshotData.addScreenState
            )
        }
    }

    @Test
    fun addScreenDark() {
        screenshotDark("add_screen") {
            AddScreenScreenshotPreview(
                addScreenState = ScreenshotData.addScreenState
            )
        }
    }

    @Test
    fun settingsScreenLight() {
        screenshotLight("settings_screen") {
            // TODO:
            Placeholder()
        }
    }

    @Test
    fun settingsScreenDark() {
        screenshotDark("settings_screen") {
            // TODO:
            Placeholder()
        }
    }

    @Test
    fun delayScreenLight() {
        screenshotLight("delay_screen") {
            // TODO:
            Placeholder()
        }
    }

    @Test
    fun delayScreenDark() {
        screenshotDark("delay_screen") {
            // TODO:
            Placeholder()
        }
    }

    @Test
    fun runScreenLight() {
        screenshotLight("run_screen") {
            // TODO:
            Placeholder()
        }
    }

    @Test
    fun runScreenDark() {
        screenshotDark("run_screen") {
            // TODO:
            Placeholder()
        }
    }

    @Test
    fun editTimelineScreenLight() {
        screenshotLight("edit_timeline_screen") {
            // TODO:
            Placeholder()
        }
    }

    @Test
    fun editTimelineScreenDark() {
        screenshotDark("edit_timeline_screen") {
            // TODO:
            Placeholder()
        }
    }

    @Test
    fun soundScreenLight() {
        screenshotLight("sound_screen") {
            // TODO:
            Placeholder()
        }
    }

    @Test
    fun soundScreenDark() {
        screenshotDark("sound_screen") {
            // TODO:
            Placeholder()
        }
    }


}

@Suppress("TestFunctionName")
@Composable
fun Placeholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan.copy(alpha = 0.2f)),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val caller = Exception().stackTrace
            .take(10)
            .joinToString("\n")
        Text(
            text = caller,
            style = MaterialTheme.typography.subtitle1.copy(fontFamily = FontFamily.Monospace)
        )
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
    val selectScreenState = SelectScreenState(
        configurations = configurations,
        toggledConfiguration = configurations.first(),
        isInDeleteMode = false,
        selectedConfigurationForDeletion = null,
        configurationsWithErrors = configurations
    )

    val selectScreenStateDelete = selectScreenState.copy(
        isInDeleteMode = true,
        selectedConfigurationForDeletion = configurations.first()
    )


    val addScreenState: AddScreenState = AddScreenState(
        name = "foo",
        description = "",
        randomOrderPlayback = false,
        components = emptyList()
    )
}