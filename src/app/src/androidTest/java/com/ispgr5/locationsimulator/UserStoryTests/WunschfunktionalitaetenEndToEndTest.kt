package com.ispgr5.locationsimulator.UserStoryTests

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.di.AppModule
import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import junit.framework.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
@UninstallModules(AppModule::class)
class WunschfunktionalitaetenEndToEndTest {

    // copy before every Integration or End-to-End-Test
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @SuppressLint("UnrememberedMutableState")
    @Before
    fun setUp() {
        hiltRule.inject() //always needed for dependencies

        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                val navController = rememberNavController()
                val themeState = mutableStateOf(ThemeState(isDarkTheme = false))
                LocationSimulatorTheme(themeState) {
                    composeRule.activity.NavigationAppHost(navController,themeState);
                }
            }
        }
    }

    /**
    * Testing User Srory:
    * -1.4.2.5 Als User möchte ich das Design zwischen Hell und Dunkel wechseln können oder die
    App mit ausgeschaltetem Bildschirm laufen zu lassen, um eine Übung in dunkler
    Umgebung durchführen zu können.
    */
    @Test
    fun testDarkAndLightMode(){
        //switch to dark mode
        composeRule.onNodeWithTag(TestTags.HOME_DARKMODE_SLIDER).performClick()

        //check if dark theme is setted in prefs
        var isDarkTheme = composeRule.activity.getSharedPreferences("prefs", ComponentActivity.MODE_PRIVATE).getBoolean("isDarkTheme", false)
        assert(isDarkTheme)
        //check if screen is dark
        assert(isDark( composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).onParent().captureToImage().asAndroidBitmap()))


        composeRule.onNodeWithTag(TestTags.HOME_DARKMODE_SLIDER).performClick()

        //check if light theme is setted in prefs.
        isDarkTheme = composeRule.activity.getSharedPreferences("prefs", ComponentActivity.MODE_PRIVATE).getBoolean("isDarkTheme", false)
        assert(!isDarkTheme)
        //check if screen is light
        assertFalse(isDark( composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).onParent().captureToImage().asAndroidBitmap()))
    }

    /**
     * checks if a given bitmap is dark
     */
    fun isDark(bitmap: Bitmap): Boolean {
        var dark = false
        val darkThreshold = bitmap.width * bitmap.height * 0.45f
        var darkPixels = 0
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (pixel in pixels) {
            val color = pixel
            val r: Int = Color.red(color)
            val g: Int = Color.green(color)
            val b: Int = Color.blue(color)
            val luminance = 0.299 * r + 0.0f + 0.587 * g + 0.0f + 0.114 * b + 0.0f
            if (luminance < 150) {
                darkPixels++
            }
        }
        if (darkPixels >= darkThreshold) {
            dark = true
        }
        return dark
    }


}