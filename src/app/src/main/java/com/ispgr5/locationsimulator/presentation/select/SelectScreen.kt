package com.ispgr5.locationsimulator.presentation.select

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.presentation.select.components.SelectConfigurationButton

/**
 * The Select Screen.
 * Shows A list of all Configuration from state
 */
@ExperimentalAnimationApi
@Composable
fun SelectScreen(
    navController: NavController,
    viewModel: SelectViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
     Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Button(
                onClick = {
                    navController.navigate(route = "editScreen")
                }
            ) {
                Icon(Icons.Filled.Add,"")
            }
        }
        Column(
            Modifier
                .padding(15.dp)
                .fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                //for all configurations in state we create a SelectConfigurationButton
                items(state.configurations) { configuration ->
                    SelectConfigurationButton(
                        configuration = configuration
                    ) {
                        viewModel.onEvent(SelectEvent.SelectedConfiguration(configuration))
                        //TODO navController.navigate("startScreen?configurationId=${configuration.id}") navigate to StartScreen with the Selected Configuration
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                //TODO add Button to delete Configurations from list
            }
        }
    }
}