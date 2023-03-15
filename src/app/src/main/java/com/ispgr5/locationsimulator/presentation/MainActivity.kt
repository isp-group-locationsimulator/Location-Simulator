package com.ispgr5.locationsimulator.presentation

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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ispgr5.locationsimulator.data.storageManager.ConfigurationStorageManager
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.ConfigurationComponentConverter
import com.ispgr5.locationsimulator.presentation.delay.DelayScreen
import com.ispgr5.locationsimulator.presentation.edit.EditScreen
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineScreen
import com.ispgr5.locationsimulator.presentation.homescreen.HomeScreenScreen
import com.ispgr5.locationsimulator.presentation.homescreen.InfoScreen
import com.ispgr5.locationsimulator.presentation.run.InfinityService
import com.ispgr5.locationsimulator.presentation.run.RunScreen
import com.ispgr5.locationsimulator.presentation.select.SelectScreen
import com.ispgr5.locationsimulator.presentation.sound.SoundDialog
import com.ispgr5.locationsimulator.presentation.sound.SoundScreen
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
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

	@OptIn(ExperimentalAnimationApi::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		soundStorageManager = SoundStorageManager(this)
		installFilesOnFirstStartup()
		configurationStorageManager =
			ConfigurationStorageManager(this, soundStorageManager = soundStorageManager)
		super.onCreate(savedInstanceState)
		setContent {
			LocationSimulatorTheme {
				// A surface container using the 'background' color from the theme
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colors.background
				) {
					val navController = rememberNavController()
					NavHost(navController = navController, startDestination = "homeScreen") {
						composable("homeScreen") {
							HomeScreenScreen(
								navController = navController,
								batteryOptDisableFunction = { disableBatteryOptimization() },
								soundStorageManager = soundStorageManager,
								toaster = toastAMessage
							)
						}
						composable("infoScreen") {
							InfoScreen(navController = navController)
						}
						composable(route = "selectScreen") {
							SelectScreen(
								navController = navController,
								configurationStorageManager = configurationStorageManager,
								soundStorageManager = soundStorageManager,
								toaster = toastAMessage
							)
						}
						composable("editScreen?configurationId={configurationId}",
							arguments = listOf(navArgument(
								name = "configurationId"
							) {
								type = NavType.IntType
								defaultValue = -1
							}
							)
						) {
							EditScreen(
								navController = navController,
								configurationStorageManager = configurationStorageManager
							)
						}
						composable(route = "delayScreen?configurationId={configurationId}",
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
								startServiceFunction = startService
							)
						}
						composable("runScreen") {
							RunScreen(navController, stopServiceFunction = { stopService() })
						}
						composable("stopService") {
							navController.navigateUp()
						}
						composable("editTimeline?configurationId={configurationId}",
							arguments = listOf(navArgument(
								name = "configurationId"
							) {
								type = NavType.IntType
								defaultValue = -1
							}
							)
						) {
							EditTimelineScreen(navController = navController)
						}
						composable("sound?configurationId={configurationId}",
							arguments = listOf(navArgument(
								name = "configurationId"
							) {
								type = NavType.IntType
								defaultValue = -1
							}
							)
						) {
							SoundScreen(navController = navController,
								soundStorageManager = soundStorageManager,
								privateDirUri = this@MainActivity.filesDir.toString(),
								recordAudio = { recordAudio() }
							)
							SoundDialog(
								popUpState = popUpState,
								onDismiss = { fileName ->
									saveAudioFile(fileName)
									popUpState.value = false
								}
							)
						}
					}
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
					ConfigurationComponentConverter().componentListToString(config)
				)
				it.putExtra("filesDir", filesDir.toString())
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

	private fun disableBatteryOptimization() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val intent = Intent()
			val pm = getSystemService(POWER_SERVICE) as PowerManager
			if (!pm.isIgnoringBatteryOptimizations(packageName)) {
				intent.action =
					android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
				intent.data = Uri.parse("package:$packageName")
				startActivity(intent)
			}
		}
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
		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
			popUpState.value = true
			recordedAudioUri = data?.data
		}
	}

	/**
	 * Saves the recorded audio to the internal filesystem of the app
	 */
	private fun saveAudioFile(fileName: String) {
		val inputStream = recordedAudioUri?.let { contentResolver.openInputStream(it) }
		val file = soundStorageManager.getFileInPrivateDir(fileName)
		val outputStream = FileOutputStream(file)
		inputStream?.copyTo(outputStream)
		outputStream.close()
		inputStream?.close()
	}

	private val toastAMessage: (message: String) -> Unit = fun(message: String) {
		Toast.makeText(this.applicationContext, message, Toast.LENGTH_SHORT).show()
	}

	/**
	 * This function installs the audio files that come with the app.
	 */
	private fun installFilesOnFirstStartup() {
		val preferences: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
		val firstStart: Boolean = preferences.getBoolean("firstStart", true)
		if (firstStart) {

			assets.list("sounds")?.forEach {
				println("Warum? Sound: $it")
				val inputStream: InputStream = assets.open("sounds/$it")
				soundStorageManager.addSoundFile(it, inputStream)
			}
			val editor: SharedPreferences.Editor = preferences.edit()
			editor.putBoolean("firstStart", false)
			editor.apply()
		}
	}
}