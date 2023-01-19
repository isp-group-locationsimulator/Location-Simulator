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
    isToggled: Boolean,
    onToggleClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onSelectClicked: () -> Unit,
) {
    val rowBackgroundColor: Color = Color.LightGray

    Box(
        Modifier
            .background(rowBackgroundColor)
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        //Column is needed for toggling so the Toggled Information is shown under the Configuration name
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                //The Toggle Button (Arrow up and down when toggled)
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
                //new row so the Configuration name is centered
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column {
                        Text(text = configuration.name)
                        if (isToggled) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(text = configuration.description)
                        }
                    }
                }
            }
            //The Information which is shown when toggle is active
            if (isToggled) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    //The Select Button
                    Button(onClick = onSelectClicked) {
                        Text(text = "SELECT")
                    }
                    //The Edit Button
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
    }
}