package com.ispgr5.locationsimulator.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.ispgr5.locationsimulator.presentation.add.AddScreen
import com.ispgr5.locationsimulator.presentation.add.AddViewModel
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

// TODO: Add KDoc to this class and methods.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // With this soundStorageManager we can access the filesystem wherever we want
    private lateinit var soundStorageManager: SoundStorageManager
    private lateinit var configurationStorageManager: ConfigurationStorageManager
    private var popUpState = mutableStateOf(false)
    private var recordedAudioUri: Uri? = null

    private val snackbarContent: MutableState<SnackbarContent?> = mutableStateOf(null)

    private val recordAudioIntent = registerForActivityResult(RecordAudioContract()) {
        popUpState.value = true
        recordedAudioUri = it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        soundStorageManager = SoundStorageManager(this@MainActivity)
        installFilesOnFirstStartup()
        configurationStorageManager = ConfigurationStorageManager(
            mainActivity = this,
            soundStorageManager = soundStorageManager,
            context = this,
            snackbarContent = snackbarContent
        )
        val themeState = mutableStateOf(
            ThemeState(
                getSharedPreferences(
                    "prefs", MODE_PRIVATE
                ).getBoolean("isDarkTheme", false)
            )
        )

        setContent {
            LocationSimulatorTheme(themeState) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    val scaffoldState = rememberScaffoldState()
                    HandleIncomingIntent(intent)
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

    @Composable
    private fun HandleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        if (intent.action in listOf(Intent.ACTION_SEND, Intent.ACTION_VIEW)) {
            val viewModel: AddViewModel = hiltViewModel()
            LaunchedEffect(key1 = intent) {
                val newConfigurationName = configurationStorageManager.handleImportFromIntent(
                    intent, viewModel.configurationUseCases
                )
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
                    navController = navController, scaffoldState = scaffoldState
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
            composable(
                route = Screen.DelayScreen.route, arguments = listOf(navArgument(
                    name = "configurationId"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                })
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
                arguments = listOf(navArgument(name = "configurationId") {
                    type = NavType.IntType
                    defaultValue = -1
                }, navArgument(name = "soundNameToAdd") {
                    type = NavType.StringType
                    defaultValue = ""
                }, navArgument(name = "minVolume") {
                    type = NavType.FloatType
                    defaultValue = 0f
                }, navArgument(name = "maxVolume") {
                    type = NavType.FloatType
                    defaultValue = 1f
                }, navArgument(name = "minPause") {
                    type = NavType.IntType
                    defaultValue = 0
                }, navArgument(name = "maxPause") {
                    type = NavType.IntType
                    defaultValue = 1000
                })
            ) {
                EditTimelineScreen(
                    navController = navController,
                    getDefaultValuesFunction = getDefaultValues,
                    scaffoldState = scaffoldState
                )
            }
            composable(
                Screen.SoundScreen.route, arguments = listOf(navArgument(
                    name = "configurationId"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                })
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
                    popUpState = popUpState
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
                    "config", ConfigurationComponentRoomConverter().componentListToString(config)
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
            if (resultCode != Activity.RESULT_OK) {
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
    private fun installFilesOnFirstStartup() {
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
     * function which returns the setted default values for Config Componments
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