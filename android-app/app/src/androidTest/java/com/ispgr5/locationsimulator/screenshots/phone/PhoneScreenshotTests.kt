package com.ispgr5.locationsimulator.screenshots.phone

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ispgr5.locationsimulator.di.AppModule
import com.ispgr5.locationsimulator.screenshots.ScreenshotScope
import com.ispgr5.locationsimulator.screenshots.ScreenshotTests
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.locale.LocaleUtil
import kotlin.time.Duration

private const val TAG = "PhoneScreenshot"

@HiltAndroidTest
@UninstallModules(AppModule::class)
abstract class PhoneScreenshotTests(private val themeState: ThemeState) : ScreenshotTests() {

    private fun modifyScreenshotName(screenshotName: String) = "${screenshotName.trim()}_${themeState.themeType.name}"
    override fun screenshot(
        content: @Composable (ScreenshotScope.() -> Unit)
    ) {
        runTest(
            timeout = Duration.parse("PT30S")
        ) {
            val currentLocale = try {
                LocaleUtil.getTestLocale()
            } catch (_: Exception) {
                "unknown"
            }
            val modifiedScreenshotName = modifyScreenshotName(getScreenshotNameFromNameRule())
            Log.i(TAG, "Taking screenshot '$modifiedScreenshotName' in ${themeState.themeType.name} mode with locale '$currentLocale'")
            composeTestRule.setContent {
                val screenshotScope by remember {
                    mutableStateOf(ScreenshotScope(modifiedScreenshotName, themeState))
                }
                LocationSimulatorTheme(themeState = themeState) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        screenshotScope.content()
                    }
                }
            }
            composeTestRule.awaitIdle()
            Thread.sleep(2000L)
            composeTestRule.awaitIdle()
            Screengrab.screenshot(modifiedScreenshotName)
            Log.i(TAG, "Taking screenshot '$modifiedScreenshotName' in ${themeState.themeType.name} mode with locale '$currentLocale'")
        }
    }
}

@HiltAndroidTest
@UninstallModules(AppModule::class)
class LightPhoneScreenshotTests : PhoneScreenshotTests(themeState = ThemeState(ThemeType.LIGHT))

@HiltAndroidTest
@UninstallModules(AppModule::class)
class DarkPhoneScreenshotTests : PhoneScreenshotTests(themeState = ThemeState(ThemeType.DARK))