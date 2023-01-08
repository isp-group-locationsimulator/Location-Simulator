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
fun OneConfigurationListMember(
    configuration: Configuration,
    toggledConfiguration: Configuration?,
    onToggleClicked: () -> Unit,
    onEditClicked: () -> Unit
) {
    val rowBackgroundColor: Color = Color.LightGray
    val isToggled: Boolean = toggledConfiguration?.id == configuration.id

    Box(
        Modifier
            .background(rowBackgroundColor)
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onToggleClicked,
                colors = ButtonDefaults.buttonColors(backgroundColor = rowBackgroundColor)
            ) {
                Icon(
                    painter = if (isToggled) {
                        painterResource(id = R.drawable.ic_baseline_keyboard_arrow_up_24)
                    } else {
                        painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24)
                    },
                    contentDescription = null
                )
            }
            ConfigurationBody(
                name = configuration.name,
                description = configuration.description,
                isToggled = isToggled
            )
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = rowBackgroundColor),
                onClick = onEditClicked
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_edit_24),
                    contentDescription = null
                )
            }
        }
    }
}

/**
 * Helper component for the Configuration Box
 * Shows the inside of the Configuration Button
 */
@Composable
fun ConfigurationBody(
    name: String,
    description: String,
    isToggled: Boolean
) {
    Column {
        Text(text = name)
        if (isToggled) {
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = description)
        }
    }
}