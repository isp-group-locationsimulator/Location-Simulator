package com.ispgr5.locationsimulator.presentation.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.network.ClientSingleton
import com.ispgr5.locationsimulator.presentation.universalComponents.LocationSimulatorTopBar
import com.ispgr5.locationsimulator.presentation.util.Screen
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme

/**
 * The Connection Screen that shows appears while waiting for a client to find a server
 */
@Composable
fun ConnectionScreen(
    navController: NavController,
    viewModel: ConnectionViewModel = hiltViewModel(),
    userName: String
) {
    val connectionState: ConnectionScreenState? by viewModel.state.observeAsState()
    val connectionStatus = connectionState!!.connectionStatus

    var statusString = ""
    when(connectionStatus) {
        ConnectionStatus.CONNECTING -> statusString = stringResource(id = R.string.searching_for_trainer)
        ConnectionStatus.SUCCESS -> navController.navigate(Screen.SelectScreen.route)
        ConnectionStatus.FAILED -> statusString = stringResource(id = R.string.search_failed)
    }

    ConnectionScreenScaffold(
        userName = userName,
        statusString = statusString,
        onRetry = { viewModel.tryServerConnection() }) {
        ClientSingleton.close()
        navController.popBackStack()
    }
}

@Composable
fun ConnectionScreenScaffold(
    userName: String,
    statusString: String,
    onRetry: () -> Unit,
    onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            LocationSimulatorTopBar(
                onBackClick = onBackClick,
                title = stringResource(id = R.string.ScreenConnection)
            )
        },
        content = { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.weight(1.0f))
                Text(
                    text = statusString,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = typography.titleLarge
                )
                Text(
                    text = stringResource(id = R.string.Name) + ": " + userName,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = typography.titleLarge
                )
                Spacer(Modifier.weight(1.0f))
                Button(
                    onClick = onRetry,
                ) {
                    Text(text = stringResource(id = R.string.retry))
                }
                Button(
                    onClick = onBackClick,
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
                Spacer(Modifier.weight(1.0f))
            }
        }
    )
}

@Preview
@Composable
fun ConnectionScreenPreview() {
    LocationSimulatorTheme {
        ConnectionScreenScaffold(userName = "TestUser",
            statusString = stringResource(id = R.string.searching_for_trainer),
            onRetry = {},
            onBackClick = {})
    }
}