package com.ispgr5.locationsimulator.endToEndTests

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.di.AppModule
import com.ispgr5.locationsimulator.presentation.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import org.junit.After
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Locale
import com.ispgr5.locationsimulator.ui.theme.ThemeType


@HiltAndroidTest
@UninstallModules(AppModule::class)
@RunWith(Parameterized::class)
class SecondaryEndToEndTests(val locale: Locale, val themeState: ThemeState) {

    // copy before every Integration or End-to-End-Test
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var originalJvmLocale: Locale

    @SuppressLint("UnrememberedMutableState")
    @Before
    fun setUp() {
        hiltRule.inject() //always needed for dependencies

        originalJvmLocale = Locale.getDefault()

        // Update SharedPreferences for theme
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences("prefs", MODE_PRIVATE)
        prefs.edit()
            .putString("themeType", themeState.themeType.name)
            .putBoolean("dynamicColors", themeState.useDynamicColor)
            .apply()

        // Recreate activity to apply theme changes
        composeRule.activity.runOnUiThread {
            composeRule.activity.recreate() // Properly trigger activity restart
        }
        composeRule.waitForIdle() // Wait for activity to reload

        //Apply locale to activity
        val activity = composeRule.activity
        val config = Configuration(activity.resources.configuration)
        config.setLocale(locale)
        activity.resources.updateConfiguration(config, activity.resources.displayMetrics)

        Locale.setDefault(locale)
    }

    @After
    fun tearDown() {
        // Restore original JVM locale
        Locale.setDefault(originalJvmLocale)
    }


    /**
     * Testing User Story W2:
     * Der oder die User*in möchte Vibrationen und Sounds in einer Timeline anordnen können,
    um so vielfältige Konfigurationen erstellen zu können.
     */
    @Test
    fun test_Timeline() {

        //in Home Screen
        composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).performClick()


        /** Eine neu Konfiguration wird erstellt (inkl. der zwei Standardvibrationen denen der
        Name Vibration1 und Vibration2 ) gegeben wird. **/
        composeRule.onNodeWithTag(TestTags.SELECT_ADD_BUTTON).performClick()

        //in Add Screen
        //add Config
        val name = "TestName1"
        val description = "TestDescription1"
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeRule.onNodeWithTag(TestTags.ADD_NAME_TEXTINPUT).performTextInput(name)
        composeRule.onNodeWithTag(TestTags.ADD_DESCRIPTION_TEXTINPUT).performTextInput(description)
        composeRule.onNodeWithTag(TestTags.ADD_SAVE_BUTTON).performClick()

        //in Select Config Screen

        //select for editing
        // Die erstellte Konfiguration wird zum Bearbeiten ausgewählt.
        composeRule.onNodeWithText(name).assertExists()
        composeRule.onNodeWithTag(TestTags.SELECT_CONFIG_BUTTON_PREFIX + name).performClick()
        composeRule.onNodeWithTag(TestTags.SELECT_CONFIG_BUTTON_EDIT_PREFIX + name).performClick()
        //in Edit Screen

        //Die erste Vibration wird in der Timeline ausgewählt.
        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[0].performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_ITEM_NAME_TEXTINPUT)
            .performTextReplacement("Vibration1")

        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[1].performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_ITEM_NAME_TEXTINPUT)
            .performTextReplacement("Vibration2")

        /**Eine Vibration mit Namen Vibration3 wird hinzugefügt.**/
        composeRule.onNodeWithTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_BUTTON).performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_DIALOG_VIBRATION).performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_ITEM_NAME_TEXTINPUT)
            .performTextReplacement("Vibration3")


        /**Ein Sound mit Namen Sound wird hinzugefügt.**/
        composeRule.onNodeWithTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_BUTTON).performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_DIALOG_SOUND).performClick()
        //select first sound
        composeRule.onAllNodesWithTag(TestTags.SOUND_SELECT_BUTTON)[0].performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_ITEM_NAME_TEXTINPUT).performTextReplacement("Sound")

        /**Der Sound wird nach links in der Timeline geschoben.**/
        composeRule.onNodeWithTag(TestTags.EDIT_MOVE_LEFT).performClick()
        //click on Third Item
        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[2].performClick()
        //check if third Item is Sound


        /**Vibration2 wird nach rechts in der Timeline geschoben..**/
        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[1].performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_MOVE_RIGHT).performClick()

        /**Vibration1 wird nach rechts in der Timeline geschoben..**/
        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[0].performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_MOVE_RIGHT).performClick()

        /**Vibration3 wird nach links verschoben**/
        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[3].performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_MOVE_LEFT).performClick()

        /**s wird überprüft, dass die Timeline von links nach rechts wie folgt angeordnet ist:
         * Sound, Vibration1, Vibration3, Vibration2**/

        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[0].performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_ITEM_NAME_TEXTINPUT).assertTextEquals(context.getString(R.string.editTimeline_name), "Sound")
        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[1].performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_ITEM_NAME_TEXTINPUT).assertTextEquals(context.getString(R.string.editTimeline_name), "Vibration1")
        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[2].performClick()

        // composeRule.onNodeWithTag(TestTags.EDIT_NAME_TEXTINPUT).assertTextEquals("Vibration3")

        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[3].performClick()

        composeRule.onNodeWithTag(TestTags.EDIT_ITEM_NAME_TEXTINPUT).assertTextEquals(context.getString(R.string.editTimeline_name), "Vibration2")

    }


    /**
     * Testing dark and Light mode above API version 26, where screenshots are available to confirm the color is applied correctly
     */
    @SdkSuppress(minSdkVersion = 26) //Screenshots are only available on API 26 and up
    @Test
    fun testDarkAndLightMode() {
        /**Der Dark-Mode-Slider wird gedrückt.**/
        //switch to dark mode
        composeRule.onNodeWithTag(TestTags.HOME_DARKMODE).performClick()

        /**Es wird überprüft, dass nun im Sytem der Darkmode gesetzt ist.**/
        //check if dark theme is setted in prefs
        var isDarkTheme = composeRule.activity.getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("themeType", "") == "DARK"
        assert(isDarkTheme)
        //check if screen is dark
        /**Es wird überprüft, dass der Bildschirm dunkel ist.**/
        assert(
            isDark(
                composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).onParent()
                    .captureToImage().asAndroidBitmap()
            )
        )

        /**Wechsel in den Select Screen.**/
        composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).performClick()

        /**Es wird überprüft, dass der Bildschirm dunkel ist.**/
        assert(
            isDark(
                composeRule.onNodeWithTag(TestTags.SELECT_ADD_BUTTON).onParent().captureToImage()
                    .asAndroidBitmap()
            )
        )

        /** Wechsel in den Home Screen.**/
        composeRule.onNodeWithTag(TestTags.TOP_BAR_BACK_BUTTON).performClick()

        /**Der Dark-Mode-Slider wird gedrückt.**/
        composeRule.onNodeWithTag(TestTags.HOME_LIGHTMODE).performClick()

        /**Es wird überprüft, dass nun im System der Lightmdoe gesetzt ist.**/
        //check if light theme is setted in prefs.
        val isLightTheme =
            (composeRule.activity.getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("themeType", "") == "LIGHT")
        assert(isLightTheme)
        //check if screen is light

        /**Es wird überprüft, dass der Bildschirm nicht mehr dunkel ist.**/
        isDark(
            composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).onParent()
                .captureToImage().asAndroidBitmap()
        ).let { dark ->
            assert(!dark)
        }
    }

    /**
     * Testing dark and Light mode below API version 26, where screenshots are not available to confirm the color is applied correctly
     */
    @SdkSuppress(maxSdkVersion = 25)
    @Test
    fun testDarkAndLightMode_withoutScreenshot() {
        /**Der Dark-Mode-Slider wird gedrückt.**/
        //switch to dark mode
        composeRule.onNodeWithTag(TestTags.HOME_DARKMODE).performClick()

        /**Es wird überprüft, dass nun im Sytem der Darkmode gesetzt ist.**/
        //check if dark theme is setted in prefs
        var isDarkTheme = composeRule.activity.getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("themeType", "") == "DARK"
        assert(isDarkTheme)

        /**Wechsel in den Select Screen.**/
        composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).performClick()


        /** Wechsel in den Home Screen.**/
        composeRule.onNodeWithTag(TestTags.TOP_BAR_BACK_BUTTON).performClick()

        /**Der Dark-Mode-Slider wird gedrückt.**/
        composeRule.onNodeWithTag(TestTags.HOME_LIGHTMODE).performClick()

        /**Es wird überprüft, dass nun im System der Lightmdoe gesetzt ist.**/
        //check if light theme is setted in prefs.
        val isLightTheme = composeRule.activity.getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("themeType", "") == "LIGHT"
        assert(isLightTheme)
        composeRule.onNodeWithTag(TestTags.HOME_LIGHTMODE).assertExists()
    }

    /**
     * checks if a given bitmap has more than 60% of the Pixels with a luminance below 150
     */
    private fun isDark(bitmap: Bitmap): Boolean {
        var dark = false
        val darkThreshold = bitmap.width * bitmap.height * 0.6f
        var darkPixels = 0
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (pixel in pixels) {
            val r: Int = Color.red(pixel)
            val g: Int = Color.green(pixel)
            val b: Int = Color.blue(pixel)
            //luminance formula
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

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "Locale={0}, Theme={1}")
        fun parameters() = listOf(
            arrayOf(Locale.ENGLISH, ThemeState(ThemeType.LIGHT, true)),
            arrayOf(Locale.ENGLISH, ThemeState(ThemeType.LIGHT, false)), //LIGHT
            arrayOf(Locale.ENGLISH, ThemeState(ThemeType.DARK, true)),
            arrayOf(Locale.ENGLISH, ThemeState(ThemeType.DARK, false)),
            arrayOf(Locale.GERMANY, ThemeState(ThemeType.LIGHT, true)),
            arrayOf(Locale.GERMANY, ThemeState(ThemeType.LIGHT, false)),
            arrayOf(Locale.GERMANY, ThemeState(ThemeType.DARK, true)),
            arrayOf(Locale.GERMANY, ThemeState(ThemeType.DARK, false)),
        )
    }
}