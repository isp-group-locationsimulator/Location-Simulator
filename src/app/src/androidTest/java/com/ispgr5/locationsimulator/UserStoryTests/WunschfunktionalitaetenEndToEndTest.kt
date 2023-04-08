package com.ispgr5.locationsimulator.UserStoryTests

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
import okhttp3.internal.wait
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
     * Testing User Story W2:
     * Der oder die User*in möchte Vibrationen und Sounds in einer Timeline anordnen können,
    um so vielfältige Konfigurationen erstellen zu können.
     */
    @Test
    fun W2_test_Timeline(){

        //in Home Screen
        composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).performClick()



       /** Eine neu Konfiguration wird erstellt (inkl. der zwei Standardvibrationen denen der
                Name Vibration1 und Vibration2 ) gegeben wird. **/
        composeRule.onNodeWithTag(TestTags.SELECT_ADD_BUTTON).performClick()

        //in Add Screen
        //add Config
        val name = "TestName1"
        val description = "TestDescription1"

        composeRule.onNodeWithTag(TestTags.ADD_NAME_TEXTINPUT).performTextInput(name)
        composeRule.onNodeWithTag(TestTags.ADD_DESCRIPTION_TEXTINPUT).performTextInput(description)
        composeRule.onNodeWithTag(TestTags.ADD_SAVE_BUTTON).performClick()

        //in Select Config Screen

        //select for editing
        // Die erstellte Konfiguration wird zum Bearbeiten ausgewählt.
        composeRule.onNodeWithText(name).assertIsDisplayed()
        composeRule.onNodeWithTag(TestTags.SELECT_CONFIG_BUTTON_PREFIX + name).performClick()
        composeRule.onNodeWithTag(TestTags.SELECT_CONFIG_BUTTON_EDIT_PREFIX + name).performClick()
        //in Edit Screen

        //Die erste Vibration wird in der Timeline ausgewählt.
        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[0].performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_NAME_TEXTINPUT).performTextReplacement("Vibration1")

        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[1].performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_NAME_TEXTINPUT).performTextReplacement("Vibration2")

        /**Eine Vibration mit Namen Vibration3 wird hinzugefügt.**/
        composeRule.onNodeWithTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_BUTTON).performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_DIALOG_VIBRATION).performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_NAME_TEXTINPUT).performTextReplacement("Vibration3")


        /**Ein Sound mit Namen Sound wird hinzugefügt.**/
        composeRule.onNodeWithTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_BUTTON).performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_DIALOG_SOUND).performClick()
        //select first sound
        composeRule.onAllNodesWithTag(TestTags.SOUND_SELECT_BUTTON)[0].performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_NAME_TEXTINPUT).performTextReplacement("Sound")

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
        composeRule.onNodeWithTag(TestTags.EDIT_NAME_TEXTINPUT).assertTextEquals("Sound")
        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[1].performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_NAME_TEXTINPUT).assertTextEquals("Vibration1")
        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[2].performClick()

       // composeRule.onNodeWithTag(TestTags.EDIT_NAME_TEXTINPUT).assertTextEquals("Vibration3")

        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[3].performClick()

        composeRule.onNodeWithTag(TestTags.EDIT_NAME_TEXTINPUT).assertTextEquals("Vibration2")

    }


    /**
    * Testing User Story W4:
    * Der oder die User*in möchte das Design zwischen Hell und Dunkel wechseln können.
    */
    @Test
    fun W4_testDarkAndLightMode(){
        /**Der Dark-Mode-Slider wird gedrückt.**/
        //switch to dark mode
        composeRule.onNodeWithTag(TestTags.HOME_DARKMODE_SLIDER).performClick()

        /**Es wird überprüft, dass nun im Sytem der Darkmode gesetzt ist.**/
        //check if dark theme is setted in prefs
        var isDarkTheme = composeRule.activity.getSharedPreferences("prefs", ComponentActivity.MODE_PRIVATE).getBoolean("isDarkTheme", false)
        assert(isDarkTheme)
        //check if screen is dark
        /**Es wird überprüft, dass der Bildschirm dunkel ist.**/
        assert(isDark( composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).onParent().captureToImage().asAndroidBitmap()))

        /**Wechsel in den Select Screen.**/
        composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).performClick()

        /**Es wird überprüft, dass der Bildschirm dunkel ist.**/
        assert(isDark( composeRule.onNodeWithTag(TestTags.SELECT_ADD_BUTTON).onParent().captureToImage().asAndroidBitmap()))

        /** Wechsel in den Home Screen.**/
        composeRule.onNodeWithTag(TestTags.TOP_BAR_BACK_BUTTON).performClick()

        /**Der Dark-Mode-Slider wird gedrückt.**/
        composeRule.onNodeWithTag(TestTags.HOME_DARKMODE_SLIDER).performClick()

        /**Es wird überprüft, dass nun im System der Lightmdoe gesetzt ist.**/
        //check if light theme is setted in prefs.
        isDarkTheme = composeRule.activity.getSharedPreferences("prefs", ComponentActivity.MODE_PRIVATE).getBoolean("isDarkTheme", false)
        assertFalse(isDarkTheme)
        //check if screen is light

        /**Es wird überprüft, dass der Bildschirm nicht mehr dunkel ist.**/
        assertFalse(isDark( composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).onParent().captureToImage().asAndroidBitmap()))
    }

    /**
     * checks if a given bitmap has more than 60% of the Pixels with a luminance below 150
     */
    fun isDark(bitmap: Bitmap): Boolean {
        var dark = false
        val darkThreshold = bitmap.width * bitmap.height * 0.6f
        var darkPixels = 0
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (pixel in pixels) {
            val color = pixel
            val r: Int = Color.red(color)
            val g: Int = Color.green(color)
            val b: Int = Color.blue(color)
            //luminacne formula
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