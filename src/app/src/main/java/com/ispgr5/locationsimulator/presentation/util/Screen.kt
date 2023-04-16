package com.ispgr5.locationsimulator.presentation.util

import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineEvent

/**Class to get the Route to the Screens*/
sealed class Screen(val route: String) {
    object HomeScreen: Screen("homeScreen")
    object InfoScreen: Screen("infoScreen")
    object SelectScreen: Screen("selectScreen")
    object AddScreen : Screen("addScreen")
    object SettingsScreen : Screen("settingsScreen")
    object DelayScreen : Screen("delayScreen?configurationId={configurationId}"){
        fun createRoute(configurationId : Int) = "delayScreen?configurationId=$configurationId"
    }
    object RunScreen : Screen("runScreen")
    object StopService : Screen("stopService") //not a screen just to stop service  //TODO is needed?
    object EditTimelineScreen : Screen("editTimeline?" +
            "configurationId={configurationId}" +
            "&soundNameToAdd={soundNameToAdd}" +
            "&minVolume={minVolume}" +
            "&maxVolume={maxVolume}" +
            "&minPause={minPause}" +
            "&maxPause={maxPause}"){
        fun createRoute(configurationId : Int, soundNameToAdd : String = "",
                        minVolume: Float = 0f, maxVolume : Float =1f, minPause : Int =100, maxPause : Int =200)
        = "editTimeline?" +
                "configurationId=$configurationId" +
                "&soundNameToAdd=$soundNameToAdd" +
                "&minVolume=$minVolume" +
                "&maxVolume=$maxVolume" +
                "&minPause=$minPause" +
                "&maxPause=$maxPause"
    }
    object SoundScreen : Screen("sound?configurationId={configurationId}"){
        fun createRoute(configurationId : Int) = "sound?configurationId=$configurationId"
    }
    //sound screen
}