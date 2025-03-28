package com.ispgr5.locationsimulator.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.MediaStore
import android.view.WindowManager
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.data.storageManager.ConfigurationStorageManager
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.ConfigurationComponentRoomConverter
import com.ispgr5.locationsimulator.domain.useCase.ConfigurationUseCases
import com.ispgr5.locationsimulator.network.ClientSingleton
import com.ispgr5.locationsimulator.presentation.add.AddScreen
import com.ispgr5.locationsimulator.presentation.delay.DelayScreen
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineScreen
import com.ispgr5.locationsimulator.presentation.exportSettings.ExportSettingsScreen
import com.ispgr5.locationsimulator.presentation.homescreen.HelpScreen
import com.ispgr5.locationsimulator.presentation.homescreen.HomeScreenScreen
import com.ispgr5.locationsimulator.presentation.homescreen.InfoScreen
import com.ispgr5.locationsimulator.presentation.run.RunScreen
import com.ispgr5.locationsimulator.presentation.run.ServiceIntentKeys
import com.ispgr5.locationsimulator.presentation.run.SimulationService
import com.ispgr5.locationsimulator.presentation.select.SelectScreen
import com.ispgr5.locationsimulator.presentation.settings.SettingsScreen
import com.ispgr5.locationsimulator.presentation.settings.SettingsState
import com.ispgr5.locationsimulator.presentation.sound.SoundDialog
import com.ispgr5.locationsimulator.presentation.sound.SoundScreen
import com.ispgr5.locationsimulator.presentation.trainerScreen.TrainerScreenScreen
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent
import com.ispgr5.locationsimulator.presentation.userSettings.UserSettingsScreen
import com.ispgr5.locationsimulator.presentation.util.Screen
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.FileOutputStream
import javax.inject.Inject

val LocalThemeState = compositionLocalOf {
    ThemeState(themeType = ThemeType.AUTO)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // With this soundStorageManager we can access the filesystem wherever we want
    private lateinit var soundStorageManager: SoundStorageManager
    private lateinit var configurationStorageManager: ConfigurationStorageManager
    private var popUpState = mutableStateOf(false)
    private var recordedAudioUri: Uri? = null

    private val snackbarContent: MutableState<SnackbarContent?> = mutableStateOf(null)

    @Inject
    lateinit var configurationUseCases: ConfigurationUseCases

    private val recordAudioIntent = registerForActivityResult(RecordAudioContract()) {
        popUpState.value = true
        recordedAudioUri = it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val wifiManager: WifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        ClientSingleton.lock = wifiManager.createMulticastLock("ClientLock")

        soundStorageManager = SoundStorageManager(this@MainActivity)
        MainScope().launch {
            installFilesOnFirstStartup()
        }
        configurationStorageManager = ConfigurationStorageManager(
            mainActivity = this,
            soundStorageManager = soundStorageManager,
            context = this,
            snackbarContent = snackbarContent,
            configurationUseCases = configurationUseCases
        )
        val storedThemeType =
            getSharedPreferences("prefs", MODE_PRIVATE).getString("themeType", ThemeType.LIGHT.name)
                ?.let {
                    ThemeType.valueOf(it)
                } ?: ThemeType.LIGHT
        val storedDynamicColors =
            getSharedPreferences("prefs", MODE_PRIVATE).getBoolean("dynamicColors", false)

        val themeState = mutableStateOf(
            ThemeState(themeType = storedThemeType, useDynamicColor = storedDynamicColors)
        )

        setContent {
            CompositionLocalProvider(LocalThemeState provides themeState.value) {
                LocationSimulatorTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(), color = colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        val context = LocalContext.current
                        val powerManager by remember {
                            mutableStateOf(context.getSystemService(POWER_SERVICE) as PowerManager)
                        }
                        HandleIncomingIntent(intent)
                        NavigationAppHost(
                            navController = navController,
                            themeState = themeState,
                            snackbarContent = snackbarContent,
                            powerManager = powerManager
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun HandleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        if (intent.action in listOf(Intent.ACTION_SEND, Intent.ACTION_VIEW)) {
            LaunchedEffect(key1 = intent) {
                val newConfigurationName =
                    configurationStorageManager.handleImportFromIntent(intent)
                if (newConfigurationName != null) {
                    val feedbackMessage =
                        getString(R.string.success_reading_configuration_name).format(
                            newConfigurationName
                        )
                    snackbarContent.value = SnackbarContent(feedbackMessage, SnackbarDuration.Short)
                }
            }
        }
    }


    /**
     * create Navigation App Host controller, which is responsible for all navigation
     */
    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun NavigationAppHost(
        navController: NavHostController,
        themeState: MutableState<ThemeState>,
        snackbarContent: MutableState<SnackbarContent?>,
        powerManager: PowerManager,
    ) {
        val context = LocalContext.current
        val snackbarHostState = remember {
            SnackbarHostState()
        }
        NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
            composable(Screen.HomeScreen.route) {
                HomeScreenScreen(
                    navController = navController,
                    checkBatteryOptimizationStatus = {
                        when {
                            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> true
                            else -> powerManager.isIgnoringBatteryOptimizations(context.packageName)
                        }
                    },
                    batteryOptDisableFunction = { disableBatteryOptimization(powerManager) },
                    soundStorageManager = soundStorageManager,
                    activity = this@MainActivity,
                    appTheme = themeState,
                    snackbarHostState = snackbarHostState,
                    snackbarContent = snackbarContent
                )
            }
            composable(Screen.InfoScreen.route) {
                InfoScreen(navController = navController)
            }
            composable(Screen.HelpScreen.route) {
                HelpScreen(
                    navController=navController, appTheme = themeState
                )
            }
            composable(route = Screen.SelectScreen.route) {
                SelectScreen(
                    navController = navController,
                    configurationStorageManager = configurationStorageManager,
                    soundStorageManager = soundStorageManager,
                    snackbarHostState = snackbarHostState,
                    snackbarContent = snackbarContent
                )
            }
            composable(Screen.AddScreen.route) {
                AddScreen(
                    navController = navController,
                    configurationStorageManager = configurationStorageManager,
                    getDefaultValuesFunction = getDefaultValues
                )
            }
            composable(Screen.SettingsScreen.route) {
                SettingsScreen(
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    saveDefaultValuesFunction = saveDefaultValues,
                    getDefaultValuesFunction = getDefaultValues
                )
            }
            composable(
                route = Screen.DelayScreen.route,
                arguments = listOf(NavigationArguments.configurationId, navArgument("userIpAddress") { type = NavType.StringType })
            ) { backStackEntry ->
                val userIpAddress = backStackEntry.arguments?.getString("userIpAddress") ?: "127.0.0.1"
                DelayScreen(
                    navController = navController,
                    startServiceFunction = startService,
                    soundsDirUri = this@MainActivity.filesDir.toString() + "/Sounds/",
                    userIpAddress = userIpAddress
                )
            }
            composable(
                route = Screen.RunScreen.route,
                arguments = listOf(navArgument("configStr") { type = NavType.StringType })
            ) {
                RunScreen(
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    stopServiceFunction = { stopService() },
                )
            }
            composable(Screen.StopService.route) {
                navController.navigateUp()
            }
            composable(
                Screen.EditTimelineScreen.route,
                arguments = NavigationArguments.allNavArguments
            ) {
                EditTimelineScreen(
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    getDefaultValuesFunction = getDefaultValues,
                )
            }
            composable(
                Screen.SoundScreen.route, arguments = listOf(NavigationArguments.configurationId)
            ) {
                SoundScreen(
                    navController = navController,
                    soundStorageManager = soundStorageManager,
                    soundsDirUri = this@MainActivity.filesDir.toString() + "/Sounds/",
                    recordAudio = { recordAudio() },
                    getDefaultValuesFunction = getDefaultValues,
                )
                SoundDialog(
                    popUpState = popUpState
                ) { fileName ->
                    saveAudioFile(fileName)
                    popUpState.value = false
                }
            }
            composable(
                route = Screen.UserSettingsScreen.route,
                arguments = listOf(navArgument("userName") { type = NavType.StringType })
            ) { backStackEntry ->
                val userName = backStackEntry.arguments?.getString("userName") ?: "Unknown"
                UserSettingsScreen(navController = navController, userName = userName)
            }

            composable(Screen.TrainerScreen.route) {
                TrainerScreenScreen(navController = navController, appTheme = themeState)
            }

            composable(
                route = Screen.ExportSettingsScreen.route,
                arguments = listOf(navArgument("userName") { type = NavType.StringType })
            ) { backStackEntry ->
                val userName = backStackEntry.arguments?.getString("userName") ?: "Unknown"
                ExportSettingsScreen(navController = navController, userName = userName)
            }
        }
    }


    /**
     * Starts the background service, which plays the audio and vibration
     */
    @OptIn(ExperimentalSerializationApi::class)
    val startService: (String, List<ConfigComponent>, Boolean) -> Unit =
        fun(patternName: String, config: List<ConfigComponent>, randomOrderPlayback: Boolean) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            val intent = Intent(this, SimulationService::class.java).apply {
                action = "START"
                putExtra(
                    ServiceIntentKeys.CONFIG_JSON_STRING,
                    ConfigurationComponentRoomConverter().componentListToString(config)
                )
                putExtra(ServiceIntentKeys.PATTERN_NAME_STRING, patternName)
                putExtra(ServiceIntentKeys.SOUNDS_DIR_STRING, "$filesDir/Sounds/")
                putExtra(
                    ServiceIntentKeys.RANDOM_ORDER_PLAYBACK_BOOLEAN,
                    randomOrderPlayback.toString()
                )
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }

    /**
     * Stops the background service
     */
    private fun stopService() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        Intent(this, SimulationService::class.java).also {
            it.action = "STOP"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it)
            } else {
                startService(it)
            }
        }
    }

    @SuppressLint("BatteryLife") // We need to have the Service run in the background as long as the user wants. The app only runs, when the user explicitly hits start.
    private fun disableBatteryOptimization(powerManager: PowerManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        if (powerManager.isIgnoringBatteryOptimizations(packageName)) return
        val intent = Intent()
        intent.action = android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    /**
     * Creates an intent to open the default recording app
     */
    private fun recordAudio() {
        recordAudioIntent.launch(Unit)
    }

    private class RecordAudioContract : ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
            val uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            return Intent.createChooser(
                intent, context.getString(R.string.choose_an_audio_recorder_app)
            )
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode != RESULT_OK) {
                return null
            }
            return intent?.data
        }

    }

    /**
     * Saves the recorded audio to the internal filesystem of the app
     */
    private fun saveAudioFile(fileName: String) {
        val inputStream = recordedAudioUri?.let { contentResolver.openInputStream(it) }
        val file = soundStorageManager.getFileInSoundsDir(fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()
    }

    /**
     * This function installs the audio files that come with the app.
     */
    private suspend fun installFilesOnFirstStartup() {
        val preferences: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstStart: Boolean = preferences.getBoolean("firstStart", true)
        if (!firstStart) return
        assets.list("sounds")?.forEach { soundName ->
            val extension = MimeTypeMap.getFileExtensionFromUrl(soundName)
                ?: return@forEach // Those ifs shall catch files that we didn't put into assets ourself. Will fail, if there somehow are audio files not from us.
            val type =
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: return@forEach
            val isTypeAudio = type.startsWith("audio")
            if (isTypeAudio) {
                soundStorageManager.addSoundFile(soundName, assets)
            }
        }
        configurationStorageManager.addDefaultConfiguration(
            context = this,
            defaultSettings = getDefaultValues()
        )
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean("firstStart", false)
        editor.apply()
    }

    /**
     * function which saves the set default values for Config Components
     * (in Main Activity because it needs the context)
     */
    private val saveDefaultValues: (state: State<SettingsState>) -> Unit =
        fun(state: State<SettingsState>) {
            val preferences: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putInt(PreferencesKeys.MIN_PAUSE_SOUND.name, state.value.minPauseSound)
            editor.putInt(PreferencesKeys.MAX_PAUSE_SOUND.name, state.value.maxPauseSound)
            editor.putFloat(PreferencesKeys.MIN_VOL_SOUND.name, state.value.minVolumeSound)
            editor.putFloat(PreferencesKeys.MAX_VOL_SOUND.name, state.value.maxVolumeSound)
            editor.putInt(PreferencesKeys.MIN_PAUSE_VIB.name, state.value.minPauseVibration)
            editor.putInt(PreferencesKeys.MAX_PAUSE_VIB.name, state.value.maxPauseVibration)
            editor.putInt(PreferencesKeys.MIN_STRENGTH_VIB.name, state.value.minStrengthVibration)
            editor.putInt(PreferencesKeys.MAX_STRENGTH_VIB.name, state.value.maxStrengthVibration)
            editor.putInt(PreferencesKeys.MIN_DURATION_VIB.name, state.value.minDurationVibration)
            editor.putInt(PreferencesKeys.MAX_DURATION_VIB.name, state.value.maxDurationVibration)
            editor.putString(
                PreferencesKeys.DEFAULT_NAME_VIB.name, state.value.defaultNameVibration
            )
            editor.apply()
        }

    /**
     * function which returns the set default values for Config Componments
     * (in Main Activity because it needs the context)
     */
    private val getDefaultValues: () -> SettingsState = fun(): SettingsState {
        val preferences: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val startDefaultName = "Vibration"
        return SettingsState(
            minPauseSound = preferences.getInt(PreferencesKeys.MIN_PAUSE_SOUND.name, 0),
            maxPauseSound = preferences.getInt(PreferencesKeys.MAX_PAUSE_SOUND.name, 5000),
            minVolumeSound = preferences.getFloat(PreferencesKeys.MIN_VOL_SOUND.name, 0f),
            maxVolumeSound = preferences.getFloat(PreferencesKeys.MAX_VOL_SOUND.name, 1f),

            minPauseVibration = preferences.getInt(PreferencesKeys.MIN_PAUSE_VIB.name, 0),
            maxPauseVibration = preferences.getInt(PreferencesKeys.MAX_PAUSE_VIB.name, 5000),
            minStrengthVibration = preferences.getInt(PreferencesKeys.MIN_STRENGTH_VIB.name, 3),
            maxStrengthVibration = preferences.getInt(
                PreferencesKeys.MAX_STRENGTH_VIB.name, 255
            ),
            minDurationVibration = preferences.getInt(
                PreferencesKeys.MIN_DURATION_VIB.name, 100
            ),
            maxDurationVibration = preferences.getInt(
                PreferencesKeys.MAX_DURATION_VIB.name, 1000
            ),
            defaultNameVibration = preferences.getString(
                PreferencesKeys.DEFAULT_NAME_VIB.name, startDefaultName
            ).toString()
        )
    }
}

enum class PreferencesKeys {
    MIN_PAUSE_SOUND, MAX_PAUSE_SOUND, MIN_VOL_SOUND, MAX_VOL_SOUND, MIN_PAUSE_VIB, MAX_PAUSE_VIB, MIN_STRENGTH_VIB, MAX_STRENGTH_VIB, MIN_DURATION_VIB, MAX_DURATION_VIB, DEFAULT_NAME_VIB,
}

object NavigationArguments {
    val configurationId = navArgument(
        name = "configurationId"
    ) {
        type = NavType.IntType
        defaultValue = -1
    }

    private val soundNameToAdd = navArgument(name = "soundNameToAdd") {
        type = NavType.StringType
        defaultValue = ""
    }

    private val minVolume = navArgument(name = "minVolume") {
        type = NavType.FloatType
        defaultValue = 0f
    }

    private val maxVolume = navArgument(name = "maxVolume") {
        type = NavType.FloatType
        defaultValue = 1f
    }

    val minPause = navArgument(name = "minPause") {
        type = NavType.IntType
        defaultValue = 0
    }

    val maxPause = navArgument(name = "maxPause") {
        type = NavType.IntType
        defaultValue = 1000
    }

    val allNavArguments = listOf(
        configurationId,
        soundNameToAdd,
        minVolume,
        maxVolume,
        minPause,
        maxPause
    )
}