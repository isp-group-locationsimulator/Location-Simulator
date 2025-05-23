package com.ispgr5.locationsimulator.presentation.editTimeline

import android.annotation.SuppressLint
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.di.AppModule
import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.presentation.settings.SettingsState
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.ispgr5.locationsimulator.R

/**
 * Testing the Edit Screen
 * The Timeline already gets tested engough in automatic User Story Test W2
 */

@HiltAndroidTest
@UninstallModules(AppModule::class)
class EditTimelineScreenTest{

    // copy before every Integration or End-to-End-Test
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @SuppressLint("UnrememberedMutableState")
    @Before
    fun setUp(){
        hiltRule.inject() //always needed for dependencies
        composeRule.activity.runOnUiThread {
            composeRule.activity.setContent {
                val navController = rememberNavController()
                val snackbarHostState = remember {
                    SnackbarHostState()
                }
                LocationSimulatorTheme {
                    NavHost(
                        navController = navController,
                        startDestination = "testEditTimeline"    //TODO
                    ) {
                        composable("testEditTimeline") {
                            EditTimelineScreen(
                                navController = navController,
                                snackbarHostState = snackbarHostState,
                                getDefaultValuesFunction = getDefaultValuesTest
                            )
                        }
                    }
                }
            }
        }
    }
    /** stub for getting default values */
    private val getDefaultValuesTest : () -> SettingsState =
        fun(): SettingsState {
            return SettingsState()
        }

    /**
    test if Dialog for adding Sound or Vibration opens up when clicking Add Button
     */
    @Test
    fun clickAddButtonAddConfigComponentDialog_isVisible(){
        composeRule.onNodeWithTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_DIALOG).assertDoesNotExist()
        composeRule.onNodeWithTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_BUTTON).performClick()
        composeRule.onNodeWithTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_DIALOG).assertExists()
    }

    /**
     * Testing the changing of the name and description
     */
    @Test
    fun changeNameAndDescription(){
        val name = "testName123"
        val description = "testDescription123"

        //change Name and Description
        composeRule.onNodeWithTag(TestTags.EDIT_CONFIG_NAME_TEXTINPUT).performTextReplacement(name)
        composeRule.onNodeWithTag(TestTags.EDIT_CONFIG_DESCRIPTION_TEXTINPUT).performTextReplacement(description)

        //check if Name and Description were changed correctly
        composeRule.onNodeWithTag(TestTags.EDIT_CONFIG_NAME_TEXTINPUT).assertTextEquals(composeRule.activity.getString(R.string.editTimeline_name), name)
        composeRule.onNodeWithTag(TestTags.EDIT_CONFIG_DESCRIPTION_TEXTINPUT).assertTextEquals(composeRule.activity.getString(R.string.editTimeline_description), description)

    }

}
