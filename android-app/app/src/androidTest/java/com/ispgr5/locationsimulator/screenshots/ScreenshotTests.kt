package com.ispgr5.locationsimulator.screenshots

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import com.ispgr5.locationsimulator.di.AppModule
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
import tools.fastlane.screengrab.locale.LocaleUtil

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

    //todo rule for dark mode might be doable?

    @Before
    fun init() {
        hiltAndroidRule.inject()
        Screengrab.setDefaultScreenshotStrategy(
            UiAutomatorScreenshotStrategy()
        )
    }

    private fun screenshot(
        screenshotName: String,
        content: @Composable ScreenshotScope.() -> Unit
    ) {
        val currentLocale = try {
            LocaleUtil.getTestLocale()
        } catch (_: Exception) {
            "en-US"
        }
        composeTestRule.setContent {
            val themeState by remember {
                mutableStateOf(ThemeState(ThemeType.LIGHT))
            }
            val screenshotScope by remember {
                mutableStateOf(ScreenshotScope(screenshotName, themeState))
            }
            LocationSimulatorTheme(themeState = themeState) {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.primarySurface
                ) {
                    screenshotScope.content()
                }
            }
        }
        when (currentLocale.lowercase()) {
            "en-us" -> Thread.sleep(2000L)
            else -> Thread.sleep(10 * 1000L)
        }
        Screengrab.screenshot(screenshotName)
        Log.i("Screenshot", "took screenshot $screenshotName, with locale en-US")
        Thread.sleep(100L)
    }

    @Test
    fun homeScreen() {
        screenshot("home_screen") {
            HomeScreenScreenshot()
        }
    }

    @Test
    fun infoScreen() {
        screenshot("info_screen") {
            InfoScreenScreenshot()
        }
    }

    @Test
    fun selectScreenNormal() {
        screenshot("select_screen_normal") {
            SelectScreenNormalScreenshot()
        }
    }

    @Test
    fun selectScreenDelete() {
        screenshot("select_screen_delete") {
            SelectScreenDeleteModeScreenshot()
        }
    }

    @Test
    fun addScreen() {
        screenshot("add_screen") {
            AddScreenScreenshot()
        }
    }

    @Test
    fun settingsScreenVibration() {
        screenshot("settings_screen_vibration") {
            SettingsScreenVibrationScreenshot()
        }
    }

    @Test
    fun settingsScreenSound() {
        screenshot("settings_screen_sound") {
            SettingsScreenSoundScreenshot()
        }
    }

    @Test
    fun delayScreenLight() {
        screenshot("delay_screen") {
            DelayScreenScreenshot()
        }
    }

    @Test
    fun runScreenPausedLight() {
        screenshot("run_screen_paused") {
            RunScreenPausedScreenshot()
        }
    }

    @Test
    fun runScreenActiveLight() {
        screenshot("run_screen_active") {
            RunScreenActiveScreenshot()
        }
    }

    @Test
    fun editTimelineScreenNormal() {
        screenshot("edit_timeline_screen_normal") {
            EditTimelineNormalScreenshot()
        }
    }

    @Test
    fun editTimelineScreenDialogShown() {
        screenshot("edit_timeline_screen_dialog") {
            EditTimelineDialogShownScreenshot()
        }
    }

    @Test
    fun editTimelineScreenNoVibrationControl() {
        screenshot("edit_timeline_screen_no_vib_control") {
            EditTimelineUnsupportedIntensityScreenshot()
        }
    }

    @Test
    fun soundScreenPlaying() {
        screenshot("sound_screen_playing") {
            SoundScreenScreenshot()
        }
    }

    @Test
    fun soundScreenStopped() {
        screenshot("sound_screen_stopped") {
            SoundScreenStoppedScreenshot()
        }
    }

    @Test
    fun soundScreenForDeletion() {
        screenshot("sound_screen_deletion") {
            SoundScreenForDeletionScreenshot()
        }
    }
}
