package com.ispgr5.locationsimulator.presentation.run

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

/**
 * The Run Screen.
 */
@ExperimentalAnimationApi
@Composable
fun RunScreen(
    navController: NavController,
    stopServiceFunction : () -> Unit,
    viewModel: RunViewModel = hiltViewModel()
) {
    Button(onClick = {
        viewModel.onEvent(RunEvent.StopClicked(stopServiceFunction))
        navController.navigateUp()
    }) {
        Text(text = "STOP")
    }
}