package com.ispgr5.locationsimulator.presentation.userSettings

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun UserSettingsScreen(
    navController: NavController,
    viewModel: UserSettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    UserSettingsScaffold(
        userSettingsState = state,
        onGoBack = { navController.navigateUp() },
        onOptionSelected = { option -> viewModel.onEvent(UserSettingsEvent.SelectConfiguration(option)) },
        onExportConfig = { /* TODO: Implementieren */ },
        onSaveConfig = { /* TODO: Implementieren */ }
    )
}

@Composable
fun UserSettingsScaffold(
    userSettingsState: UserSettingsState,
    onGoBack: () -> Unit,
    onOptionSelected: (String) -> Unit,
    onExportConfig: () -> Unit,
    onSaveConfig: () -> Unit
) {
    Scaffold(
        topBar = { UserSettingsTopBar(onGoBack) },
        content = { paddingValues ->
            UserSettingsContent(
                paddingValues = paddingValues,
                state = userSettingsState,
                onOptionSelected = onOptionSelected,
                onExportConfig = onExportConfig,
                onSaveConfig = onSaveConfig
            )
        }
    )
}

@Composable
fun UserSettingsContent(
    paddingValues: PaddingValues,
    state: UserSettingsState,
    onOptionSelected: (String) -> Unit,
    onExportConfig: () -> Unit,
    onSaveConfig: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Überschrift
        Text(
            text = "User1",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Konfiguration auswählen",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Scrollbare Liste im beigen Bereich
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
                items(state.availableConfigurations) { option ->
                    ConfigOptionItem(option, onOptionSelected)
                }
            }
        }

        // Buttons unten
        Button(
            onClick = onExportConfig,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513))
        ) {
            Text("Konfiguration exportieren", fontSize = 16.sp)
        }

        Button(
            onClick = onSaveConfig,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513))
        ) {
            Text("Speichern", fontSize = 16.sp)
        }
    }
}

@Composable
fun ConfigOptionItem(option: String, onOptionSelected: (String) -> Unit) {
    Button(
        onClick = { onOptionSelected(option) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDAD5)) // Hellorange
    ) {
        Text(text = option, color = Color.Black,fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSettingsTopBar(onGoBack: () -> Unit) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f)) // Platzhalter für Zentrierung
                Text(
                    text = "Einstellungen",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f)) // Platzhalter für Zentrierung
            }
        },
        navigationIcon = {
            IconButton(onClick = onGoBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Zurück")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun UserSettingsPreview() {
    val state = UserSettingsState(
        selectedUser = "User1",
        availableConfigurations = listOf("Lautes Atmen", "Husten", "Kratzen")
    )
    LocationSimulatorTheme {
        UserSettingsScaffold(
            userSettingsState = state,
            onGoBack = {},
            onOptionSelected = {},
            onExportConfig = {},
            onSaveConfig = {}
        )
    }
}
