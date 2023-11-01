package com.ispgr5.locationsimulator.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ispgr5.locationsimulator.data.storageManager.ConfigurationStorageManager
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.ConfigurationComponentRoomConverter
import com.ispgr5.locationsimulator.presentation.add.AddScreen
import com.ispgr5.locationsimulator.presentation.delay.DelayScreen
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineScreen
import com.ispgr5.locationsimulator.presentation.homescreen.HomeScreenScreen
import com.ispgr5.locationsimulator.presentation.homescreen.InfoScreen
import com.ispgr5.locationsimulator.presentation.run.InfinityService
import com.ispgr5.locationsimulator.presentation.run.RunScreen
import com.ispgr5.locationsimulator.presentation.select.SelectScreen
import com.ispgr5.locationsimulator.presentation.settings.SettingsScreen
import com.ispgr5.locationsimulator.presentation.settings.SettingsState
import com.ispgr5.locationsimulator.presentation.sound.SoundDialog
import com.ispgr5.locationsimulator.presentation.sound.SoundScreen
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent
import com.ispgr5.locationsimulator.presentation.util.Screen
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.FileOutputStream
import java.io.InputStream

// TODO: Add KDoc to this class and methods.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // With this soundStorageManager we can access the filesystem wherever we want
    private lateinit var soundStorageManager: SoundStorageManager
    private lateinit var configurationStorageManager: ConfigurationStorageManager
    private var popUpState = mutableStateOf(false)
    private var recordedAudioUri: Uri? = null

    private val snackbarContent: MutableState<SnackbarContent?> = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        soundStorageManager = SoundStorageManager(this@MainActivity)
        installFilesOnFirstStartup()
        configurationStorageManager =
            ConfigurationStorageManager(
                mainActivity = this,
                soundStorageManager = soundStorageManager,
                context = this,
                snackbarContent = snackbarContent
            )
        super.onCreate(savedInstanceState)
        val themeState = mutableStateOf(
            ThemeState(
                getSharedPreferences(
                    "prefs",
                    MODE_PRIVATE
                ).getBoolean("isDarkTheme", false)
            )
        )
        setContent {
            LocationSimulatorTheme(themeState) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    val scaffoldState = rememberScaffoldState()
                    NavigationAppHost(
                        navController = navController,
                        themeState = themeState,
                        scaffoldState = scaffoldState,
                        snackbarContent = snackbarContent
                    )
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
        scaffoldState: ScaffoldState,
        snackbarContent: MutableState<SnackbarContent?>,
    ) {
        NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
            composable(Screen.HomeScreen.route) {
                HomeScreenScreen(
                    navController = navController,
                    batteryOptDisableFunction = { disableBatteryOptimization() },
                    soundStorageManager = soundStorageManager,
                    activity = this@MainActivity,
                    darkTheme = themeState,
                    scaffoldState = scaffoldState,
                    snackbarContent = snackbarContent
                )
            }
            composable(Screen.InfoScreen.route) {
                InfoScreen(
                    navController = navController,
                    scaffoldState = scaffoldState
                )
            }
            composable(route = Screen.SelectScreen.route) {
                SelectScreen(
                    navController = navController,
                    configurationStorageManager = configurationStorageManager,
                    soundStorageManager = soundStorageManager,
                    scaffoldState = scaffoldState,
                    snackbarContent = snackbarContent
                )
            }
            composable(Screen.AddScreen.route) {
                AddScreen(
                    navController = navController,
                    configurationStorageManager = configurationStorageManager,
                    scaffoldState = scaffoldState,
                    getDefaultValuesFunction = getDefaultValues
                )
            }
            composable(Screen.SettingsScreen.route) {
                SettingsScreen(
                    navController = navController,
                    scaffoldState = scaffoldState,
                    saveDefaultValuesFunction = saveDefaultValues,
                    getDefaultValuesFunction = getDefaultValues
                )
            }
            composable(route = Screen.DelayScreen.route,
                arguments = listOf(navArgument(
                    name = "configurationId"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                }
                )
            ) {
                DelayScreen(
                    navController = navController,
                    startServiceFunction = startService,
                    context = this@MainActivity,
                    scaffoldState = scaffoldState,
                    soundsDirUri = this@MainActivity.filesDir.toString() + "/Sounds/",
                )
            }
            composable(Screen.RunScreen.route) {
                RunScreen(
                    navController = navController,
                    stopServiceFunction = { stopService() },
                    scaffoldState = scaffoldState
                )
            }
            composable(Screen.StopService.route) {
                navController.navigateUp()    //Todo not needed
            }
            composable(Screen.EditTimelineScreen.route,
                arguments = listOf(
                    navArgument(name = "configurationId") {
                        type = NavType.IntType
                        defaultValue = -1
                    },
                    navArgument(name = "soundNameToAdd") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument(name = "minVolume") {
                        type = NavType.FloatType
                        defaultValue = 0f
                    },
                    navArgument(name = "maxVolume") {
                        type = NavType.FloatType
                        defaultValue = 1f
                    },
                    navArgument(name = "minPause") {
                        type = NavType.IntType
                        defaultValue = 0
                    },
                    navArgument(name = "maxPause") {
                        type = NavType.IntType
                        defaultValue = 1000
                    }
                )
            ) {
                EditTimelineScreen(
                    navController = navController,
                    getDefaultValuesFunction = getDefaultValues,
                    scaffoldState = scaffoldState
                )
            }
            composable(Screen.SoundScreen.route,
                arguments = listOf(navArgument(
                    name = "configurationId"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                }
                )
            ) {
                SoundScreen(
                    navController = navController,
                    soundStorageManager = soundStorageManager,
                    soundsDirUri = this@MainActivity.filesDir.toString() + "/Sounds/",
                    recordAudio = { recordAudio() },
                    getDefaultValuesFunction = getDefaultValues,
                    scaffoldState = scaffoldState
                )
                SoundDialog(
                    popUpState = popUpState,
                    scaffoldState = scaffoldState
                ) { fileName ->
                    saveAudioFile(fileName)
                    popUpState.value = false
                }
            }
        }
    }


    /**
     * Starts the background service, which plays the audio and vibration
     */
    @OptIn(ExperimentalSerializationApi::class)
    val startService: (List<ConfigComponent>, Boolean) -> Unit =
        fun(config: List<ConfigComponent>, randomOrderPlayback: Boolean) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            Intent(this, InfinityService::class.java).also {
                it.action = "START"
                it.putExtra(
                    "config",
                    ConfigurationComponentRoomConverter().componentListToString(config)
                )
                it.putExtra("soundsDir", "$filesDir/Sounds/")
                it.putExtra("randomOrderPlayback", randomOrderPlayback.toString())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(it)
                } else {
                    startService(it)
                }
            }
        }

    /**
     * Stops the background service
     */
    private fun stopService() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        Intent(this, InfinityService::class.java).also {
            Log.d("debug", "itAction: ${it.action}")
            it.action = "STOP"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it)
            } else {
                startService(it)
            }
        }
    }

    @SuppressLint("BatteryLife") // We need to have the Service run in the background as long as the user wants. The app only runs, when the user explicitly hits start.
    private fun disableBatteryOptimization() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (pm.isIgnoringBatteryOptimizations(packageName)) return
        val intent = Intent()
        intent.action = android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    /**
     * Creates an intent to open the default recording app
     */
    private fun recordAudio() {
        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        val uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        //TODO translate
        val chooserIntent = Intent.createChooser(intent, "Choose an audio recorder app")
        startActivityForResult(chooserIntent, 0)
    }

    @Deprecated("Deprecated in Java")
    /**
     * Sets the Uri of the recorded audio file
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != 0 || resultCode != Activity.RESULT_OK) return
        popUpState.value = true
        recordedAudioUri = data?.data
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
    private fun installFilesOnFirstStartup() {
        val preferences: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstStart: Boolean = preferences.getBoolean("firstStart", true)
        if (!firstStart) return
        assets.list("sounds")?.forEach {
            val extension = MimeTypeMap.getFileExtensionFromUrl(it)
                ?: return@forEach // Those ifs shall catch files that we didn't put into assets ourself. Will fail, if there somehow are audio files not from us.
            val type =
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: return@forEach
            val isTypeAudio = type.startsWith("audio")
            if (isTypeAudio) {
                val inputStream: InputStream = assets.open("sounds/$it")
                soundStorageManager.addSoundFile(it, inputStream)
            }
        }
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean("firstStart", false)
        editor.apply()
    }

    /**
     * function which saves the setted default values for Config Components
     * (in Main Activity because it needs the context)
     */
    private val saveDefaultValues: (state: State<SettingsState>) -> Unit =
        fun(state: State<SettingsState>) {
            val preferences: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putInt("minPauseSound", state.value.minPauseSound)
            editor.putInt("maxPauseSound", state.value.maxPauseSound)
            editor.putFloat("minVolumeSound", state.value.minVolumeSound)
            editor.putFloat("maxVolumeSound", state.value.maxVolumeSound)
            editor.putInt("minPauseVibration", state.value.minPauseVibration)
            editor.putInt("maxPauseVibration", state.value.maxPauseVibration)
            editor.putInt("minStrengthVibration", state.value.minStrengthVibration)
            editor.putInt("maxStrengthVibration", state.value.maxStrengthVibration)
            editor.putInt("minDurationVibration", state.value.minDurationVibration)
            editor.putInt("maxDurationVibration", state.value.maxDurationVibration)
            editor.putString("defaultNameVibration", state.value.defaultNameVibration)
            editor.apply()
        }

    /**
     * function which returns the setted default values for Config Componments
     * (in Main Activity because it needs the context)
     */
    private val getDefaultValues: () -> SettingsState =
        fun(): SettingsState {
            val preferences: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
            val startDefaultName = "Vibration"
            return SettingsState(
                minPauseSound = preferences.getInt("minPauseSound", 0),
                maxPauseSound = preferences.getInt("maxPauseSound", 5000),
                minVolumeSound = preferences.getFloat("minVolumeSound", 0f),
                maxVolumeSound = preferences.getFloat("maxVolumeSound", 1f),

                minPauseVibration = preferences.getInt("minPauseVibration", 0),
                maxPauseVibration = preferences.getInt("maxPauseVibration", 5000),
                minStrengthVibration = preferences.getInt("minStrengthVibration", 3),
                maxStrengthVibration = preferences.getInt("maxStrengthVibration", 255),
                minDurationVibration = preferences.getInt("minDurationVibration", 100),
                maxDurationVibration = preferences.getInt("maxDurationVibration", 1000),
                defaultNameVibration = preferences.getString(
                    "defaultNameVibration",
                    startDefaultName
                ).toString()
            )
        }
}