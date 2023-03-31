package com.ispgr5.locationsimulator.UserStoryTests

import android.annotation.SuppressLint
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.launchActivity
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.di.AppModule
import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineScreen
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class KernfunktionalitaetEndToEndTest {

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
     * -1.4.1.1 Als User möchte ich das Abspielen von Signalen starten können
     * -1.4.1.5 Als User möchte ich die Dauer und Stärke der Vibration, sowie die Länge zwischen
    Vibrationsintervallen einstellen können für mindestens ein Muster.
     */
    @Test
    fun create_a_new_Configuration_with_one_vibration_change_duration_strength_and_pause_and_select_it_to_run(){

            //in Home Screen go to select Config page
            composeRule.onNodeWithTag(TestTags.HOME_APPNAME).assertIsDisplayed()
            composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).assertIsDisplayed();
            composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).performClick();

            //in Select Config Screen

            //Check if add config Button exists
            composeRule.onNodeWithTag(TestTags.SELECT_ADD_BUTTON).assertIsDisplayed();
            //click on Add Config Button
            composeRule.onNodeWithTag(TestTags.SELECT_ADD_BUTTON).performClick();

            //in Add Screen

            //add Config
            val name = "TestName1";
            val description = "TestDescription1"

            composeRule.onNodeWithTag(TestTags.ADD_NAME_TEXTINPUT).performTextInput(name)
            composeRule.onNodeWithTag(TestTags.ADD_DESCRIPTION_TEXTINPUT).performTextInput(description)
            composeRule.onNodeWithTag(TestTags.ADD_SAVE_BUTTON).performClick()

            //in Select Config Screen

             //select for editing
            composeRule.onNodeWithText(name).assertIsDisplayed()
            composeRule.onNodeWithTag(TestTags.SELECT_CONFIG_BUTTON_PREFIX + name).performClick()
            composeRule.onNodeWithText(description).assertIsDisplayed()
            composeRule.onNodeWithTag(TestTags.SELECT_CONFIG_BUTTON_EDIT_PREFIX + name).performClick()

            //in Edit Screen
    }


}