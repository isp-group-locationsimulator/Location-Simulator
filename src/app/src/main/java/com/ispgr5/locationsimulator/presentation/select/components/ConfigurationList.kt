package com.ispgr5.locationsimulator.presentation.select.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.R

/**
 * Shows one Configuration as Button in max width
 */
@Composable
fun SelectConfigurationButton(
    configuration: Configuration,
    toggledConfiguration: Configuration?,
    onToggleClicked: () -> Unit
) {
    Box(
        Modifier
            .background(Color.LightGray)
            .padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onToggleClicked,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
            ) {
                Icon(
                    painter = if (toggledConfiguration?.id == configuration.id) {
                        painterResource(id = R.drawable.ic_baseline_keyboard_arrow_up_24)
                    } else {
                        painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24)
                    },
                    contentDescription = null
                )
            }
            ConfigurationBox(
                name = configuration.name,
                description = configuration.description,
                isToggled = toggledConfiguration?.id == configuration.id
            )
        }
    }
}

/**
 * Helper component for the Configuration Box
 * Shows the inside of the Configuration Button
 */
@Composable
fun ConfigurationBox(
    name: String,
    description: String,
    isToggled: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = name)
        if (isToggled) {
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = description)
        }
    }
}