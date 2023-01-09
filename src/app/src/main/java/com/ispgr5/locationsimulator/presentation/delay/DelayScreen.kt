package com.ispgr5.locationsimulator.presentation.delay

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

/**
 * The Delay Screen.
 * Here you can check you have Select the right Configuration
 * and set a timer
 */
@ExperimentalAnimationApi
@Composable
fun DelayScreen(
    navController: NavController,
    viewModel: DelayViewModel = hiltViewModel(),
    startServiceFunction : () -> Unit
) {
    //The state from viewmodel
    val state = viewModel.state.value

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.configuration == null) {
            Text(text = "Configuration is null")
            //TODO should not happen can you guarantee that?
        } else {
            Text(text = state.configuration.name)
            Text(text = state.configuration.description)
        }
        Button(onClick = {
            if (state.configuration == null){
                //Don't start
            }else{
                viewModel.onEvent(DelayEvent.StartClicked(startServiceFunction))
                navController.navigate("runScreen")
            }
        }) {
            Text(text = "START")
        }
    }
}