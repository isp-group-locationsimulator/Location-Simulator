package com.ispgr5.locationsimulator.presentation.util

/**Class to get the Route to the Screens*/
sealed class Screen(val route: String) {


    data object HomeScreen : Screen("homeScreen")
    data object InfoScreen : Screen("infoScreen")
    data object HelpScreen: Screen("helpScreen")
    data object SelectScreen : Screen("selectScreen")
    data object AddScreen : Screen("addScreen")
    data object SettingsScreen : Screen("settingsScreen")
    data object TrainerScreen: Screen("trainerScreen")
    data object ConnectionScreen: Screen("connectionScreen?userName={userName}")
    {
        fun createRoute(userName: String) = "connectionScreen?userName=$userName"
    }
    data object DelayScreen : Screen("delayScreen?configurationId={configurationId}")
    {
        fun createRoute(configurationId: Int) = "delayScreen?configurationId=$configurationId"
    }
    data object UserSettingsScreen : Screen("userSettingsScreen?userName={userName}")
    {
        fun createRoute(userName: String) = "userSettingsScreen?userName=$userName"
    }
    data object ExportSettingsScreen : Screen("exportSettingsScreen?userName={userName}")
    {
        fun createRoute(userName: String) = "exportSettingsScreen?userName=$userName"
    }

    data object RunScreen : Screen("runScreen?configurationId={configurationId}") {
        fun createRoute(configurationId: Int) = "runScreen?configurationId=$configurationId"
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