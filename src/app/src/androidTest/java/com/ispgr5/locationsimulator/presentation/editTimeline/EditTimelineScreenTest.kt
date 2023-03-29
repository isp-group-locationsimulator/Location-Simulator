package com.ispgr5.locationsimulator.presentation.editTimeline

import android.annotation.SuppressLint
import android.os.Debug
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.di.AppModule
import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


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
        composeRule.setContent {
            val navController = rememberNavController()
            val themeState = mutableStateOf(ThemeState(isDarkTheme = false))
            LocationSimulatorTheme(themeState) {
                NavHost(
                    navController = navController,
                    startDestination = "editScreen"   //TODO
                    ){
                    composable( route = "editScreen"){
                        EditTimelineScreen(navController = navController)
                    }
                }
            }
        }
    }

    /**
    test if Dialog for adding Sound or Vibration opens up when clicking Add Button
     */
    @Test
    fun clickAddButtonAddConfigComponentDialog_isVisible(){

       // composeRule.onNodeWithTag(TestTags.EDIT_SCREEN_ADD_DIALOG).assertDoesNotExist()
        composeRule.onNodeWithTag(TestTags.EDIT_SCREEN_ADD_BUTTON).performClick();
        composeRule.onNodeWithTag(TestTags.EDIT_SCREEN_ADD_DIALOG).assertDoesNotExist();
        assertEquals(2,4);
        Log.d("Test","Hello Test");
    }

}