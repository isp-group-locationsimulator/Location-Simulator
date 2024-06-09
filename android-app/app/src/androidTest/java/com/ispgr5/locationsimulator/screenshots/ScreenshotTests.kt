package com.ispgr5.locationsimulator.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import com.ispgr5.locationsimulator.di.AppModule
import com.ispgr5.locationsimulator.presentation.add.AddScreenPreview
import com.ispgr5.locationsimulator.presentation.delay.DelayScreenPreview
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineDialogShownPreview
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineNormalPreview
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineUnsupportedIntensityPreview
import com.ispgr5.locationsimulator.presentation.homescreen.HomeScreenPreview
import com.ispgr5.locationsimulator.presentation.homescreen.InfoScreenPreview
import com.ispgr5.locationsimulator.presentation.run.RunScreenActivePreview
import com.ispgr5.locationsimulator.presentation.run.RunScreenPausedPreview
import com.ispgr5.locationsimulator.presentation.select.SelectScreenDeleteModePreview
import com.ispgr5.locationsimulator.presentation.select.SelectScreenNormalPreview
import com.ispgr5.locationsimulator.presentation.settings.SettingsScreenSoundPreview
import com.ispgr5.locationsimulator.presentation.settings.SettingsScreenVibrationPreview
import com.ispgr5.locationsimulator.presentation.sound.SoundScreenForDeletionPreview
import com.ispgr5.locationsimulator.presentation.sound.SoundScreenPlayingPreview
import com.ispgr5.locationsimulator.presentation.sound.SoundScreenStoppedPreview
import com.ispgr5.locationsimulator.ui.theme.ThemeState
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


data class ScreenshotScope(
    val screenshotName: String, val theme: ThemeState
)


@HiltAndroidTest
@UninstallModules(AppModule::class)
abstract class ScreenshotTests {

    @get:Rule(order = 0)
    val hiltAndroidRule by lazy {
        HiltAndroidRule(this)
    }

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
            HomeScreenPreview()
        }
    }

    @Test
    fun infoScreen() {
        screenshot {
            InfoScreenPreview()
        }
    }

    @Test
    fun selectScreenNormal() {
        screenshot {
            SelectScreenNormalPreview()
        }
    }

    @Test
    fun selectScreenDeleteModeActive() {
        screenshot {
            SelectScreenDeleteModePreview()
        }
    }

    @Test
    fun addScreen() {
        screenshot {
            AddScreenPreview()
        }
    }

    @Test
    fun settingsScreenVibration() {
        screenshot {
            SettingsScreenVibrationPreview()
        }
    }

    @Test
    fun settingsScreenSound() {
        screenshot {
            SettingsScreenSoundPreview()
        }
    }

    @Test
    fun delayScreenLight() {
        screenshot {
            DelayScreenPreview()
        }
    }

    @Test
    fun runScreenPaused() {
        screenshot {
            RunScreenPausedPreview()
        }
    }

    @Test
    fun runScreenActive() {
        screenshot {
            RunScreenActivePreview()
        }
    }

    @Test
    fun editTimelineScreenNormal() {
        screenshot {
            EditTimelineNormalPreview()
        }
    }

    @Test
    fun editTimelineScreenDialogShown() {
        screenshot {
            EditTimelineDialogShownPreview()
        }
    }

    @Test
    fun editTimelineScreenNoVibrationControl() {
        screenshot {
            EditTimelineUnsupportedIntensityPreview()
        }
    }

    @Test
    fun soundScreenPlaying() {
        screenshot {
            SoundScreenPlayingPreview()
        }
    }

    @Test
    fun soundScreenStopped() {
        screenshot {
            SoundScreenStoppedPreview()
        }
    }

    @Test
    fun soundScreenForDeletion() {
        screenshot {
            SoundScreenForDeletionPreview()
        }
    }
}
