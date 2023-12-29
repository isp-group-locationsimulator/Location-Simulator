package com.ispgr5.locationsimulator.screenshots

import android.content.res.Resources.Theme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.ispgr5.locationsimulator.di.AppModule
import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent
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
    fun homeScreen() {
        composeTestRule.setContent {
            LocationSimulatorTheme(themeState = ScreenshotData.themeState.value) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Button(onClick = { }) {
                        Text("Test button")
                    }
                }
            }
        }
        Screengrab.screenshot("home_screen")
    }

    @Test
    fun addScreenLight() {
        screenshotLight("select_screen") {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                OutlinedTextField(value = "select", {})
            }
        }
    }

    @Test
    fun addScreenDark() {
        screenshotDark("select_screen") {
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                OutlinedTextField(value = "select", {})
            }
        }
    }

    private fun screenshot(theme: ThemeType, screenshotName: String, content: @Composable () -> Unit) {
        composeTestRule.setContent {
            LocationSimulatorTheme(themeState = ThemeState(theme)) {
                content()
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
    val themeState = mutableStateOf(ThemeState(themeType = ThemeType.LIGHT))
}