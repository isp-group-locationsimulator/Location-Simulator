package com.ispgr5.locationsimulator.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import com.ispgr5.locationsimulator.di.AppModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy
import tools.fastlane.screengrab.cleanstatusbar.CleanStatusBar
import tools.fastlane.screengrab.locale.LocaleTestRule

@HiltAndroidTest
@UninstallModules(AppModule::class)
abstract class ScreenshotTests {

    @get:Rule(order = 0)
    val hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Rule(order = 2)
    @JvmField
    val localeTestRule = LocaleTestRule()

    @Rule(order = 3)
    @JvmField
    val testNameRule = TestName()

    //todo rule for dark mode might be doable?

    @Before
    fun init() {
        hiltAndroidRule.inject()
        Screengrab.setDefaultScreenshotStrategy(
            UiAutomatorScreenshotStrategy()
        )
        CleanStatusBar.enableWithDefaults()
    }

    @After
    fun afterAll() {
        CleanStatusBar.disable()
    }

    abstract fun screenshot(content: @Composable (ScreenshotScope.() -> Unit))

    fun getScreenshotNameFromNameRule() = testNameRule.methodName.toSnakeCase()

    private fun String.toSnakeCase(): String {
        val pattern = "(?<=.)[A-Z]".toRegex()
        return this.replace(pattern, "_$0").lowercase()
    }

    @Test
    fun homeScreen() {
        screenshot {
            HomeScreenScreenshot()
        }
    }

    @Test
    fun infoScreen() {
        screenshot {
            InfoScreenScreenshot()
        }
    }

    @Test
    fun selectScreenNormal() {
        screenshot {
            SelectScreenNormalScreenshot()
        }
    }

    @Test
    fun selectScreenDeleteModeActive() {
        screenshot {
            SelectScreenDeleteModeScreenshot()
        }
    }

    @Test
    fun addScreen() {
        screenshot {
            AddScreenScreenshot()
        }
    }

    @Test
    fun settingsScreenVibration() {
        screenshot {
            SettingsScreenVibrationScreenshot()
        }
    }

    @Test
    fun settingsScreenSound() {
        screenshot {
            SettingsScreenSoundScreenshot()
        }
    }

    @Test
    fun delayScreenLight() {
        screenshot {
            DelayScreenScreenshot()
        }
    }

    @Test
    fun runScreenPaused() {
        screenshot {
            RunScreenPausedScreenshot()
        }
    }

    @Test
    fun runScreenActive() {
        screenshot {
            RunScreenActiveScreenshot()
        }
    }

    @Test
    fun editTimelineScreenNormal() {
        screenshot {
            EditTimelineNormalScreenshot()
        }
    }

    @Test
    fun editTimelineScreenDialogShown() {
        screenshot {
            EditTimelineDialogShownScreenshot()
        }
    }

    @Test
    fun editTimelineScreenNoVibrationControl() {
        screenshot {
            EditTimelineUnsupportedIntensityScreenshot()
        }
    }

    @Test
    fun soundScreenPlaying() {
        screenshot {
            SoundScreenScreenshot()
        }
    }

    @Test
    fun soundScreenStopped() {
        screenshot {
            SoundScreenStoppedScreenshot()
        }
    }

    @Test
    fun soundScreenForDeletion() {
        screenshot {
            SoundScreenForDeletionScreenshot()
        }
    }
}
