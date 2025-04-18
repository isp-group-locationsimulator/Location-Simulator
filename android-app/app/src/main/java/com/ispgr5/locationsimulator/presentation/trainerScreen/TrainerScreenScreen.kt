package com.ispgr5.locationsimulator.presentation.trainerScreen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.network.ClientSingleton
import com.ispgr5.locationsimulator.network.ServerSingleton
import com.ispgr5.locationsimulator.presentation.ChosenRole
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData
import com.ispgr5.locationsimulator.presentation.universalComponents.LocationSimulatorTopBar
import com.ispgr5.locationsimulator.presentation.util.Screen
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "TrainerScreen"

@Composable
fun TrainerScreenScreen(
    navController: NavController,
    viewModel: TrainerScreenViewModel = hiltViewModel(),
    appTheme: MutableState<ThemeState>
) {
    val deviceState: List<Device>? by ClientSingleton.deviceList.observeAsState()
    val devices = deviceState ?: emptyList()
    val state = viewModel.state.value
    val isRefreshing = viewModel.isRestartClientThreadAlive.observeAsState().value == true
    val isTrainingActive = remember { mutableStateOf(false) }
    val isTimerActive = remember { mutableStateOf(false) }

    val onBack = {
        if(!isRefreshing) {
            ClientSingleton.close()
            navController.popBackStack()
        }
    }

    BackHandler {
        onBack()
    }

    TrainerScreenScaffold(
        trainerScreenState = state,
        deviceList = devices,
        isTrainingActive = isTrainingActive.value,
        isTimerActive = isTimerActive.value,
        onEvent = { ev: TrainerScreenEvent -> viewModel.onEvent(ev) },
        onGoBack = onBack,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.onEvent(TrainerScreenEvent.Refresh) },
        navController = navController,
        appTheme = appTheme
    )
}

@Composable
fun TrainerScreenScaffold(
    trainerScreenState: TrainerScreenState,
    deviceList: List<Device>,
    isTrainingActive: Boolean,
    isTimerActive: Boolean,
    onEvent: (TrainerScreenEvent) -> Unit,
    onGoBack: () -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    navController: NavController,
    appTheme: MutableState<ThemeState>
) {
    Scaffold(
        topBar = {
            TrainerScreenTopBar(onGoBack = onGoBack)
        },
        content = { paddingValues ->
            TrainerScreenContent(
                paddingValues = paddingValues,
                trainerScreenState = trainerScreenState,
                deviceList = deviceList,
                isTrainingActive = isTrainingActive,
                isTimerActive = isTimerActive,
                onEvent = onEvent,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                navController = navController,
                appTheme = appTheme
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerScreenContent(
    paddingValues: PaddingValues,
    trainerScreenState: TrainerScreenState,
    deviceList: List<Device>,
    isTrainingActive: Boolean,
    isTimerActive: Boolean,
    onEvent: (TrainerScreenEvent) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    navController: NavController,
    appTheme: MutableState<ThemeState>
) {
    val trainingActive = remember { mutableStateOf(isTrainingActive) }
    val timerActive = remember { mutableStateOf(isTimerActive) }

    trainingActive.value = deviceList.isNotEmpty() && deviceList.all { it.isPlaying }
    timerActive.value = deviceList.isNotEmpty() && deviceList.all { it.timerState != null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.already_connected),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
                .background(
                    if (isSystemInDarkTheme()) Color(0xFF80FFD1)
                    else Color.LightGray
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(deviceList) { device ->
                    if(device.selectedConfig == null) {
                        device.selectedConfig = trainerScreenState.defaultConfig
                    }

                    val vibrationInteractionSource = remember {
                        MutableInteractionSource()
                    }
                    val soundInteractionSource = remember {
                        MutableInteractionSource()
                    }

                    LaunchedEffect(vibrationInteractionSource) {
                        vibrationInteractionSource.interactions.collectLatest { interaction ->
                            when (interaction) {
                                is PressInteraction.Press -> {
                                    onEvent(TrainerScreenEvent.TestVibrationPress(device))
                                }
                                is PressInteraction.Release -> {
                                    onEvent(TrainerScreenEvent.StopDeviceTraining(device))
                                }
                            }
                        }
                    }
                    LaunchedEffect(soundInteractionSource) {
                        soundInteractionSource.interactions.collectLatest { interaction ->
                            when (interaction) {
                                is PressInteraction.Press -> {
                                    onEvent(TrainerScreenEvent.TestSoundPress(device))
                                }
                                is PressInteraction.Release -> {
                                    onEvent(TrainerScreenEvent.StopDeviceTraining(device))
                                }
                            }
                        }
                    }

                    DeviceCard(
                        userName = device.user,
                        deviceIpAddress = device.ipAddress,
                        activity = device.selectedConfig?.name ?: "Default",
                        isOnline = device.isConnected,
                        isPlaying = device.isPlaying,
                        isTimerActive = device.timerState != null,
                        onPlayClick = {
                            if(device.isPlaying) {
                                onEvent(TrainerScreenEvent.StopDeviceTraining(device))
                            } else {
                                onEvent(TrainerScreenEvent.StartDeviceTraining(device))
                            }
                        },
                        vibrationInteractionSource = vibrationInteractionSource,
                        soundInteractionSource = soundInteractionSource,
                        onSettingsClick = {
                            navController.navigate(Screen.UserSettingsScreen.createRoute(device.user, device.ipAddress))
                        },
                        onTimerClick = {
                            if(device.timerState == null) {
                                val id = device.selectedConfig?.id
                                if (id != null) { // should always be true but checking won't hurt
                                    navController.navigate(
                                        Screen.DelayScreen.createRoute(
                                            id,
                                            ChosenRole.TRAINER.value,
                                            device.ipAddress
                                        )
                                    )
                                } else {
                                    Log.w(TAG, "selectedConfig was null")
                                }
                            } else {
                                onEvent(TrainerScreenEvent.StopDeviceTraining(device))
                            }
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            // Starten/Stoppen-Button
            Button(
                onClick = {
                    if (trainingActive.value) {
                        // Stoppe alle Geräte
                        onEvent(TrainerScreenEvent.StopTraining)
                    } else {
                        // Starte nur Geräte, die noch nicht laufen
                        onEvent(TrainerScreenEvent.StartTraining)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (trainingActive.value) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    if (trainingActive.value) {
                        stringResource(id = R.string.stop)
                    } else {
                        stringResource(id = R.string.start)
                    },
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            IconButton(
                onClick = {
                    if(timerActive.value) {
                        onEvent(TrainerScreenEvent.StopTraining)
                    } else {
                        navController.navigate(Screen.DelayScreen.createRoute(-1, ChosenRole.TRAINER.value))
                    }
                }
            ) {
                Icon(
                    imageVector = if (timerActive.value) Icons.Default.TimerOff else Icons.Default.Timer,
                    contentDescription = "Countdown until start",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerScreenTopBar(onGoBack: () -> Unit) {
    LocationSimulatorTopBar(
        onBackClick = onGoBack,
        title = stringResource(id = R.string.trainer_screen_title)
    )
}


@Preview(showBackground = true)
@Composable
fun TrainerScreenPreview() {
    val state = TrainerScreenState()
    val deviceList =  listOf(
        Device(ipAddress = "127.0.0.1", user = "User1", isPlaying = true, isConnected = true),
        Device(ipAddress = "127.0.0.1", user = "User2", isPlaying = false, isConnected = false)
    )

    val navController = rememberNavController()
    val themeState = remember {
        mutableStateOf(PreviewData.themePreviewState)
    }

    LocationSimulatorTheme {
        TrainerScreenScaffold(
            trainerScreenState = state,
            deviceList = deviceList,
            isTrainingActive = false,
            isTimerActive = false,
            onEvent = {},
            onGoBack = {},
            isRefreshing = false,
            onRefresh = {},
            navController = navController,
            appTheme = themeState
        )
    }
}
