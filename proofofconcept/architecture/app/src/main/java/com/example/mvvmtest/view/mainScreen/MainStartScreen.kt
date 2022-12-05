package com.example.mvvmtest.view.mainScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mvvmtest.model.Configuration


/**
 * This composable shows the Start Screen
 * The Numbers are from the selectScreen
 */
@Composable
fun StartScreen(
    currentConfiguration: Configuration
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Duration Vibration")
        Text(text = currentConfiguration.durationVibrateInSec.value.toString())
        Text(text = "Duration Pause Vibration")
        Text(text = currentConfiguration.durationPauseVibrateInSec.value.toString())
    }
}