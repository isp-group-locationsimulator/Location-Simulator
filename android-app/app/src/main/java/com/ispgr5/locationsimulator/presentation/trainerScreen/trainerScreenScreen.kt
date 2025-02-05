package com.ispgr5.locationsimulator.presentation.trainerScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import androidx.navigation.compose.rememberNavController





@Composable
fun TrainerScreenScreen(
    navController: NavController,
    viewModel: TrainerScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    TrainerScreenScaffold(
        trainerScreenState = state,
        onStartTraining = { /* TODO: Action for starting training */ },
        onGoBack = { navController.navigateUp() },
        onOptionSelected = { option -> viewModel.onEvent(TrainerScreenEvent.OptionSelected(option)) },
        navController = navController

    )
}

@Composable
fun TrainerScreenScaffold(
    trainerScreenState: TrainerScreenState,
    onStartTraining: () -> Unit,
    onGoBack: () -> Unit,
    onOptionSelected: (String) -> Unit,
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
                onStartTraining = onStartTraining,
                onOptionSelected = onOptionSelected,
                navController = navController
            )
        }
    )
}

@Composable
fun TrainerScreenContent(
    paddingValues: PaddingValues,
    trainerScreenState: TrainerScreenState,
    onStartTraining: () -> Unit, // Später für Funktionalität nutzen
    onOptionSelected: (String) -> Unit, // Später für Gerätekontrolle nutzen
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titel ohne Funktion
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

        // Scrollbare Liste im grauen Bereich
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Begrenzte Höhe für Scrollbarkeit
                .padding(horizontal = 16.dp)
                .background(Color.LightGray) // Grauer Hintergrund für die Liste
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(trainerScreenState.devices) { device ->
                    DeviceCard(
                        userName = device.user,
                        deviceName = device.name,
                        activity = "Klopfen und Husten", // Beispieltext, später dynamisch anpassen
                        isOnline = device.isConnected,
                        onPlayClick = { /* TODO: Play-Button Logik einfügen */ },
                        onSettingsClick = {
                            navController.navigate("userSettingsScreen/${device.user}")
                        }
                    )
                }
            }

        }

        // Start-Button (noch ohne Logik)
        Button(
            onClick = onStartTraining,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8C3300)) // Braune Farbe
        ) {
            Text("Starten", fontSize = 18.sp)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerScreenTopBar(onGoBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.trainer_screen),
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
    val state = TrainerScreenState(
        devices = listOf(
            Device(user = "User1", name = "Samsung Galaxy S9+", isConnected = true),
            Device(user = "User2", name = "Huawei P30 Pro", isConnected = false)
        )
    )

    val navController = rememberNavController()

    LocationSimulatorTheme {
        TrainerScreenScaffold(
            trainerScreenState = state,
            onStartTraining = {},
            onGoBack = {},
            onOptionSelected = {},
            navController = navController
        )
    }
}
