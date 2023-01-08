package com.ispgr5.locationsimulator.presentation.run

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

/**
 * The Run Screen.
 */
@ExperimentalAnimationApi
@Composable
fun RunScreen(
    navController: NavController
) {
    Button(onClick = {navController.navigate("stopService")}) {
        Text(text = "STOP")
    }
}