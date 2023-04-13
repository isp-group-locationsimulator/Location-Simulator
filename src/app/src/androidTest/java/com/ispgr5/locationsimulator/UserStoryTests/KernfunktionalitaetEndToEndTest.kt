package com.ispgr5.locationsimulator.UserStoryTests

import android.annotation.SuppressLint
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
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
import okhttp3.internal.wait
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class KernfunktionalitaetEndToEndTest {

    // copy before every Integration or End-to-End-Test
    //Hilt Rule for Hilt Injections
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    //Compose Rule for Compose interactions
    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @SuppressLint("UnrememberedMutableState")
    @Before
    fun setUp() {
        hiltRule.inject() //always needed for dependencies

        //set Content
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                val navController = rememberNavController()
                val themeState = mutableStateOf(ThemeState(isDarkTheme = false))
                LocationSimulatorTheme(themeState) {
                    composeRule.activity.NavigationAppHost(navController,themeState)
                }
            }
        }
    }

    /**
     * Testing User Story K5:
     * Der oder die User*in möchte die Dauer und Stärke der Vibration, sowie die Länge
    zwischen Vibrationsintervallen einstellen können, für mindestens ein Muster
     */
    @Test
    fun K5_create_and_play_Configuration(){

            //in Home Screen go to select Config page
            composeRule.onNodeWithTag(TestTags.HOME_APPNAME).assertIsDisplayed()
            composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).assertIsDisplayed()
            composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).performClick()

            //in Select Config Screen

            //Check if add config Button exists
            composeRule.onNodeWithTag(TestTags.SELECT_ADD_BUTTON).assertIsDisplayed()
            //click on Add Config Button
            composeRule.onNodeWithTag(TestTags.SELECT_ADD_BUTTON).performClick()


            /**Eine neue Konfiguration wird erstellt**/
            //in Add Screen

            //add Config
            val name = "TestName1"
            val description = "TestDescription1"

            composeRule.onNodeWithTag(TestTags.ADD_NAME_TEXTINPUT).performTextInput(name)
            composeRule.onNodeWithTag(TestTags.ADD_DESCRIPTION_TEXTINPUT).performTextInput(description)
            composeRule.onNodeWithTag(TestTags.ADD_SAVE_BUTTON).performClick()

            //in Select Config Screen

            /**Es wird überprüft, ob die erstellte Konfiguration korrekt zur Auswahl angezeigt wird.**/
             //select for editing
            composeRule.onNodeWithText(name).assertIsDisplayed()
            composeRule.onNodeWithTag(TestTags.SELECT_CONFIG_BUTTON_PREFIX + name).performClick()
            composeRule.onNodeWithText(description).assertIsDisplayed()
            /** Die erstellte Konfiguration wird zum Bearbeiten ausgewählt.**/
            composeRule.onNodeWithTag(TestTags.SELECT_CONFIG_BUTTON_EDIT_PREFIX + name).performClick()

            //in Edit Screen


            /**Die erste Vibration wird in der Timeline ausgewählt.**/
            composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[0].performClick()


            /**Die Länge der Vibration und der nachfolgende Pause der Vibration wird verändert.**/

            composeRule.onNodeWithTag(TestTags.EDIT_VIB_SLIDER_DURATION).performGesture { swipeRight() }
            composeRule.onNodeWithTag(TestTags.EDIT_VIB_SLIDER_DURATION).performGesture { swipeRight() }
            composeRule.onNodeWithTag(TestTags.EDIT_SLIDER_PAUSE).performGesture { swipeRight() }
            composeRule.onNodeWithTag(TestTags.EDIT_SLIDER_PAUSE).performGesture { swipeRight() }

            composeRule.onNodeWithTag(TestTags.TOP_BAR_BACK_BUTTON).performClick()

            // in select screen
            /**Die erstellte Konfiguration wird zum Abspielen ausgwählt.**/
            composeRule.onNodeWithTag(TestTags.SELECT_CONFIG_BUTTON_SELECT_PREFIX + name).performClick()

            //in delay Screen
            /**Die Konfiguration wird gestartet.**/
            //swipe to bottom of Delay Screen so that Start Button is visible on smaller devices
            composeRule.onNodeWithTag(TestTags.DELAY_MAIN_COLUMN).performGesture { swipeUp() }
            composeRule.onNodeWithTag(TestTags.DELAY_START_BUTTON).performClick()

            //in run screen
            /** Das Abspielen wird gestoppt.**/
            composeRule.onNodeWithTag(TestTags.RUN_END_BUTTON).performClick()
    }


}