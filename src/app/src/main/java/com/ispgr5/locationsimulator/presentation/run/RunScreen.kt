package com.ispgr5.locationsimulator.presentation.run

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R

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
        Text(text = stringResource(id = R.string.run_stop))
    }
}