package com.ispgr5.locationsimulator.endToEndTests

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.res.Configuration
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.di.AppModule
import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@HiltAndroidTest
@UninstallModules(AppModule::class)
@RunWith(Parameterized::class)
class CreateAndPlayConfigurationTest(val locale: Locale, val themeState: ThemeState) {

    // copy before every Integration or End-to-End-Test
    //Hilt Rule for Hilt Injections
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    //Compose Rule for Compose interactions
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
     * Testing the creation and usage of a configuration from start to end
     */
    @Test
    fun create_and_play_Configuration() {

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        //in Home Screen go to select Config page
        composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).assertExists()
        composeRule.onNodeWithTag(TestTags.HOME_SELECT_CONFIG_BUTTON).performClick()

        //in Select Config Screen

        //Check if add config Button exists
        composeRule.onNodeWithTag(TestTags.SELECT_ADD_BUTTON).assertExists()
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
        composeRule.onNodeWithText(name).assertExists()
        composeRule.onNodeWithTag(TestTags.SELECT_CONFIG_BUTTON_PREFIX + name).performClick()
        composeRule.onNodeWithText(description).assertExists()
        /** Die erstellte Konfiguration wird zum Bearbeiten ausgewählt.**/
        composeRule.onNodeWithTag(TestTags.SELECT_CONFIG_BUTTON_EDIT_PREFIX + name).performClick()

        //in Edit Screen


        /**Die erste Vibration wird in der Timeline ausgewählt.**/
        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[0].assertExists()
        composeRule.onAllNodesWithTag(TestTags.EDIT_CONFIG_ITEM)[0].performClick()


        /**Die Länge der Vibration und der nachfolgende Pause der Vibration wird verändert.**/
        composeRule.onNodeWithTag(TestTags.EDIT_VIB_SLIDER_DURATION, useUnmergedTree = true).assertExists() //TODO Check if values actually change
        composeRule.onNodeWithTag(TestTags.EDIT_VIB_SLIDER_DURATION, useUnmergedTree = true)
            .performTouchInput { swipeRight() }
        composeRule.onNodeWithTag(TestTags.EDIT_VIB_SLIDER_DURATION, useUnmergedTree = true)
            .performTouchInput { swipeRight() }
        composeRule.onNodeWithTag(TestTags.EDIT_SLIDER_PAUSE, useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag(TestTags.EDIT_SLIDER_PAUSE, useUnmergedTree = true).performTouchInput { swipeRight() }
        composeRule.onNodeWithTag(TestTags.EDIT_SLIDER_PAUSE, useUnmergedTree = true).performTouchInput { swipeRight() }

        /** Die Eingabe von präzisen Werten wird überprüft**/
        composeRule.onAllNodesWithTag(TestTags.EDIT_VIB_FIELD_DURATION)[0].assertExists()
        composeRule.onAllNodesWithTag(TestTags.EDIT_VIB_FIELD_DURATION)[1].assertExists()
        composeRule.onAllNodesWithTag(TestTags.EDIT_VIB_FIELD_DURATION)[1].performTextReplacement("abcd")
        val decimalFormat = DecimalFormat().apply {
            decimalFormatSymbols = DecimalFormatSymbols(locale)
        }
        val decimalSeparator = decimalFormat.decimalFormatSymbols.decimalSeparator.toString()
        val inputs = arrayOf("1.18", "0,5", "2,345", "999999.", "-0,78", "abcd")
        val correctResults = arrayOf("1${decimalSeparator}18", "0${decimalSeparator}50", "2${decimalSeparator}35", "0${decimalSeparator}00", "0${decimalSeparator}00", "0${decimalSeparator}00")

        for (i in inputs.indices) {
            composeRule.onAllNodesWithTag(TestTags.EDIT_VIB_FIELD_DURATION)[0].performClick()
            composeRule.onAllNodesWithTag(TestTags.EDIT_VIB_FIELD_DURATION)[0].performTextReplacement(inputs[i])
            composeRule.onAllNodesWithTag(TestTags.EDIT_VIB_FIELD_DURATION)[0].assertExists()
            composeRule.onAllNodesWithTag(TestTags.EDIT_VIB_FIELD_DURATION)[0].assertTextEquals(
                context.getString( R.string.min), inputs[i])
            composeRule.onNodeWithTag(TestTags.EDIT_VIB_SLIDER_DURATION, useUnmergedTree = true).performClick() //Clear focus from field, trigger format check
            composeRule.onAllNodesWithTag(TestTags.EDIT_VIB_FIELD_DURATION)[0].assertTextEquals(
                context.getString( R.string.min), correctResults[i])
        }


        composeRule.onNodeWithTag(TestTags.TOP_BAR_BACK_BUTTON).performClick() //TODO Test Run actually ended

        // in select screen
        /**Die erstellte Konfiguration wird zum Abspielen ausgwählt.**/
        composeRule.onNodeWithTag(TestTags.SELECT_CONFIG_BUTTON_SELECT_PREFIX + name).performClick()

        //in delay Screen
        /**Die Konfiguration wird gestartet.**/
        //swipe to bottom of Delay Screen so that Start Button is visible on smaller devices
        composeRule.onNodeWithTag(TestTags.DELAY_MAIN_COLUMN).performTouchInput { swipeUp() }
        composeRule.onNodeWithTag(TestTags.DELAY_START_BUTTON).performClick()

        //in run screen
        /** Das Abspielen wird gestoppt.**/
        composeRule.onNodeWithTag(TestTags.RUN_END_BUTTON).isDisplayed()
        composeRule.onNodeWithTag(TestTags.RUN_END_BUTTON).performClick()
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