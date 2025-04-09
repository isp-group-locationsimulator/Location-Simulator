package com.ispgr5.locationsimulator.presentation.util

import com.ispgr5.locationsimulator.presentation.ChosenRole

/**Class to get the Route to the Screens*/
sealed class Screen(val route: String) {


    data object HomeScreen : Screen("homeScreen")
    data object InfoScreen : Screen("infoScreen")
    data object HelpScreen: Screen("helpScreen")
    data object SelectScreen : Screen("selectScreen?chosenRole={chosenRole}")
    {
        fun createRoute(chosenRole: Int = ChosenRole.STANDALONE.value) = "selectScreen?chosenRole=$chosenRole"
    }
    data object AddScreen : Screen("addScreen")
    data object SettingsScreen : Screen("settingsScreen")
    data object TrainerScreen: Screen("trainerScreen")
    data object DelayScreen : Screen("delayScreen?configurationId={configurationId},chosenRole={chosenRole},remoteIpAddress={remoteIpAddress}")
    {
        fun createRoute(configurationId: Int, chosenRole: Int = 0, remoteIpAddress: String = "255.255.255.255") =
            "delayScreen?configurationId=$configurationId,chosenRole=$chosenRole,remoteIpAddress=$remoteIpAddress"
    }
    data object UserSettingsScreen : Screen("userSettingsScreen?userName={userName},userIpAddress={userIpAddress}")
    {
        fun createRoute(userName: String, userIpAddress: String) = "userSettingsScreen?userName=$userName,userIpAddress=$userIpAddress"
    }
    data object ExportSettingsScreen : Screen("exportSettingsScreen?userName={userName}")
    {
        fun createRoute(userName: String) = "exportSettingsScreen?userName=$userName"
    }

    data object RunScreen : Screen("runScreen?configurationId={configurationId},configStr={configStr}") {
        fun createRoute(configurationId: Int, configStr: String) = "runScreen?configurationId=$configurationId,configStr=$configStr"
    }

    data object StopService :
        Screen("stopService") //not a screen just to stop service  //TODO is needed?

    data object EditTimelineScreen : Screen(
        "editTimeline?" +
                "configurationId={configurationId}" +
                "&soundNameToAdd={soundNameToAdd}" +
                "&minVolume={minVolume}" +
                "&maxVolume={maxVolume}" +
                "&minPause={minPause}" +
                "&maxPause={maxPause}"
    ) {
        fun createRoute(
            configurationId: Int,
            soundNameToAdd: String = "",
            minVolume: Float = 0f,
            maxVolume: Float = 1f,
            minPause: Int = 100,
            maxPause: Int = 200
        ) = "editTimeline?" +
                "configurationId=$configurationId" +
                "&soundNameToAdd=$soundNameToAdd" +
                "&minVolume=$minVolume" +
                "&maxVolume=$maxVolume" +
                "&minPause=$minPause" +
                "&maxPause=$maxPause"
    }

    data object SoundScreen : Screen("sound?configurationId={configurationId}") {
        fun createRoute(configurationId: Int) = "sound?configurationId=$configurationId"
    }
    //sound screen
}