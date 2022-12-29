package com.ispgr5.locationsimulator.presentation.select.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ispgr5.locationsimulator.domain.model.Configuration

/**
 * Shows one Configuration as Button in max width
 */
@Composable
fun SelectConfigurationButton(
    configuration: Configuration,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick
    ) {
        ConfigurationBox(
            duration = configuration.duration.toString(),
            pause = configuration.pause.toString(),
            name = configuration.name
        )
    }
}

/**
 * Helper component for the Configuration Box
 * Shows the inside of the Configuration Button
 */
@Composable
fun ConfigurationBox(
    duration: String,
    pause: String,
    name: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth()) {
            Text(text = name)
            Row {
                Text(text = "duration:")
                Text(text = duration)
            }
            Row {
                Text(text = "pause:")
                Text(text = pause)
            }
        }
    }
}