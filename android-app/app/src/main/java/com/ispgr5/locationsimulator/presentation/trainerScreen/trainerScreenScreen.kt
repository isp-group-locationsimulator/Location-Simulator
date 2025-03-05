package com.ispgr5.locationsimulator.presentation.trainerScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.ispgr5.locationsimulator.network.ClientHandler
import com.ispgr5.locationsimulator.network.ServerSingleton
import com.ispgr5.locationsimulator.presentation.util.Screen


@Composable
fun TrainerScreenScreen(
    navController: NavController,
    viewModel: TrainerScreenViewModel = hiltViewModel()
) {
    val deviceState: ArrayList<Device>? by ClientHandler.deviceList.observeAsState()
    val devices = deviceState ?: emptyList()
    val state = viewModel.state.value
    val isTrainingActive = remember { mutableStateOf(false) }

    TrainerScreenScaffold(
        trainerScreenState = state,
        deviceList = devices,
        isTrainingActive = isTrainingActive.value,
        onEvent = { ev: TrainerScreenEvent -> viewModel.onEvent(ev) },
        onGoBack = { ServerSingleton.close(); navController.navigateUp() },
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
                    DeviceCard(
                        userName = device.user,
                        deviceName = device.name,
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
                            ClientHandler.deviceList.updateDevice(modifiedDevice)
                        },
                        onSettingsClick = {
                            navController.navigate(Screen.UserSettingsScreen.createRoute(device.user))
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
        Device(user = "User1", name = "Samsung Galaxy S9+", isPlaying = true, isConnected = true),
        Device(user = "User2", name = "Huawei P30 Pro", isPlaying = false, isConnected = false)
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
