package com.ispgr5.locationsimulator.presentation.trainerScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import androidx.navigation.compose.rememberNavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.network.ClientSingleton
import com.ispgr5.locationsimulator.presentation.util.Screen
import kotlinx.coroutines.flow.collectLatest


@Composable
fun TrainerScreenScreen(
    navController: NavController,
    viewModel: TrainerScreenViewModel = hiltViewModel()
) {
    val deviceState: ArrayList<Device>? by ClientSingleton.deviceList.observeAsState()
    val devices = deviceState ?: emptyList()
    val state = viewModel.state.value
    val isTrainingActive = remember { mutableStateOf(false) }

    TrainerScreenScaffold(
        trainerScreenState = state,
        deviceList = devices,
        isTrainingActive = isTrainingActive.value,
        onEvent = { ev: TrainerScreenEvent -> viewModel.onEvent(ev) },
        onGoBack = { ClientSingleton.close(); navController.navigateUp() },
        navController = navController
    )
}

@Composable
fun TrainerScreenScaffold(
    trainerScreenState: TrainerScreenState,
    deviceList: List<Device>,
    isTrainingActive: Boolean,
    onEvent: (TrainerScreenEvent) -> Unit,
    onGoBack: () -> Unit,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TrainerScreenTopBar(onGoBack)
        },
        content = { paddingValues ->
            TrainerScreenContent(
                paddingValues = paddingValues,
                trainerScreenState = trainerScreenState,
                deviceList = deviceList,
                isTrainingActive = isTrainingActive,
                onEvent = onEvent,
                navController = navController
            )
        }
    )
}

@Composable
fun TrainerScreenContent(
    paddingValues: PaddingValues,
    trainerScreenState: TrainerScreenState,
    deviceList: List<Device>,
    isTrainingActive: Boolean,
    onEvent: (TrainerScreenEvent) -> Unit,
    navController: NavController
) {
    val trainingActive = remember { mutableStateOf(isTrainingActive) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.trainer_screen_title),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Bereits verbunden",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
                .background(Color.LightGray)
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
                        onPlayClick = {
                            if(device.isPlaying) {
                                onEvent(TrainerScreenEvent.StopDeviceTraining(device))
                            } else {
                                onEvent(TrainerScreenEvent.StartDeviceTraining(device))
                            }
                            val modifiedDevice = device.copy()
                            modifiedDevice.isPlaying = !device.isPlaying
                            ClientSingleton.deviceList.updateDevice(modifiedDevice)
                        },
                        vibrationInteractionSource = vibrationInteractionSource,
                        soundInteractionSource = soundInteractionSource,
                        onSettingsClick = {
                            navController.navigate(Screen.UserSettingsScreen.createRoute(device.user, device.ipAddress))
                        }
                    )
                }
            }
        }

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

                // Zustand umschalten
                trainingActive.value = !trainingActive.value
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (trainingActive.value) Color.Red else Color(0xFF8C3300)
            )
        ) {
            Text(if (trainingActive.value) "Stoppen" else "Starten", fontSize = 18.sp)
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerScreenTopBar(onGoBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "",
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onGoBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, // Zurück-Pfeil
                    contentDescription = "Zurück"
                )
            }
        }
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

    LocationSimulatorTheme {
        TrainerScreenScaffold(
            trainerScreenState = state,
            deviceList = deviceList,
            isTrainingActive = false,
            onEvent = {},
            onGoBack = {},
            navController = navController
        )
    }
}
