package com.ispgr5.locationsimulator.presentation.exportSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import androidx.compose.ui.tooling.preview.Preview
import com.ispgr5.locationsimulator.presentation.universalComponents.LocationSimulatorTopBar

@Composable
fun ExportSettingsScreen(
    navController: NavController,
    userName: String,
    viewModel: ExportSettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    ExportSettingsScaffold(
        exportSettingsState = state,
        userName = userName,
        onGoBack = { navController.navigateUp() },
        onOptionSelected = { option -> viewModel.onEvent(ExportSettingsEvent.SelectConfiguration(option)) },
        onExportConfig = { /* TODO: Implementieren */ },
        onSaveConfig = { /* TODO: Implementieren */ }
    )
}

@Composable
fun ExportSettingsScaffold(
    exportSettingsState: ExportSettingsState,
    userName: String,
    onGoBack: () -> Unit,
    onOptionSelected: (String) -> Unit,
    onExportConfig: () -> Unit,
    onSaveConfig: () -> Unit
) {
    Scaffold(
        topBar = { ExportSettingsTopBar(onGoBack) },
        content = { paddingValues ->
            ExportSettingsContent(
                userName = userName,
                paddingValues = paddingValues,
                state = exportSettingsState,
                onOptionSelected = onOptionSelected,
                onExportConfig = onExportConfig,
                onSaveConfig = onSaveConfig
            )
        }
    )
}

@Composable
fun ExportSettingsContent(
    userName: String,
    paddingValues: PaddingValues,
    state: ExportSettingsState,
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
            text = userName,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Konfiguration auswählen",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Scrollbare Liste im beigen Bereich
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
                .background(colorScheme.surfaceContainerLow)
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
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
        ) {
            Text("Exportieren", fontSize = 16.sp, color = colorScheme.onPrimary)
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
        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.surfaceContainerHigh) // Hellorange
    ) {
        Text(text = option, color = colorScheme.onBackground,fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportSettingsTopBar(onGoBack: () -> Unit) {
    LocationSimulatorTopBar(
        onBackClick = onGoBack,
        title = "Exportieren",
    )
}

@Preview(showBackground = true)
@Composable
fun ExportSettingsPreview() {
    val state = ExportSettingsState(
        selectedUser = "User1",
        availableConfigurations = listOf("Lautes Atmen", "Husten", "Kratzen"),
        selectedConfiguration = "Husten"
    )

    val userName = "User1"
    LocationSimulatorTheme {
        ExportSettingsScaffold(
            exportSettingsState = state,
            userName = userName,
            onGoBack = {},
            onOptionSelected = {},
            onExportConfig = {},
            onSaveConfig = {}
        )
    }
}
