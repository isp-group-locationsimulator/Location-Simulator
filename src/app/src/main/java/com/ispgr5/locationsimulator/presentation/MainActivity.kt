package com.ispgr5.locationsimulator.presentation

import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineScreen
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ispgr5.locationsimulator.FilePicker
import com.ispgr5.locationsimulator.StorageConfigInterface
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.ConfigurationComponentConverter
import com.ispgr5.locationsimulator.presentation.delay.DelayScreen
import com.ispgr5.locationsimulator.presentation.edit.EditScreen
import com.ispgr5.locationsimulator.presentation.homescreen.HomeScreenScreen
import com.ispgr5.locationsimulator.presentation.homescreen.InfoScreen
import com.ispgr5.locationsimulator.presentation.run.InfinityService
import com.ispgr5.locationsimulator.presentation.run.RunScreen
import com.ispgr5.locationsimulator.presentation.select.SelectScreen
import com.ispgr5.locationsimulator.presentation.sound.SoundScreen
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.ExperimentalSerializationApi

// TODO: Add KDoc to this class and methods.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // With this filePicker we can access the filesystem wherever we want
    private lateinit var filePicker: FilePicker
    private lateinit var storageConfigInterface: StorageConfigInterface

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        filePicker = FilePicker(this)
        storageConfigInterface = StorageConfigInterface(this, filePicker = filePicker)
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
                            HomeScreenScreen(navController = navController)
                        }
                        composable("infoScreen") {
                            InfoScreen()
                        }
                        composable(route = "selectScreen") {
                            SelectScreen(
                                navController = navController,
                                storageConfigInterface = storageConfigInterface,
                                filePicker = filePicker
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
                                storageConfigInterface = storageConfigInterface
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
                        // TODO: Is this the correct way to setup the navigation?
                        composable("sound?configurationId={configurationId}",
                            arguments = listOf(navArgument(
                                name = "configurationId"
                            ) {
                                type = NavType.IntType
                                defaultValue = -1
                            }
                            )
                        ) {
                            SoundScreen(filePicker = filePicker, mainActivity = this@MainActivity)
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    val startService: (List<ConfigComponent>) -> Unit = fun(config: List<ConfigComponent>) {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        Intent(this, InfinityService::class.java).also {
            it.action = "START"
            it.putExtra("config", ConfigurationComponentConverter().componentListToString(config))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it)
            } else {
                startService(it)
            }
        }
    }

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
}