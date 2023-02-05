package com.ispgr5.locationsimulator.presentation.select.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
    onExportClicked: () -> Unit,
    hasErrors: Boolean
) {
    val rowBackgroundColor: Color = Color.LightGray

    Box(
        Modifier
            .background(rowBackgroundColor, shape = RoundedCornerShape(6.dp))
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        //Column is needed for toggling so the Toggled Information is shown under the Configuration name
        Column {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (!hasErrors) {
                    Arrangement.Center
                } else {
                    Arrangement.SpaceBetween
                },
            ) {
                //The Toggle Button (Arrow up and down when toggled)
                Button(
                    onClick = onToggleClicked,
                    contentPadding = PaddingValues(0.dp),
                    enabled = true,
                    shape = MaterialTheme.shapes.small,
                    border = null,
                    elevation = null,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colors.primary,
                        disabledBackgroundColor = Color.Transparent,
                        disabledContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
                    )
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
                    if (!hasErrors) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column {
                        Text(text = configuration.name)
                        if (isToggled) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(text = configuration.description)
                        }
                    }
                }
                if (hasErrors) {
                    Button(
                        onClick = {
                                  //TODO tell the user what is the problem
                                  },
                        contentPadding = PaddingValues(0.dp),
                        enabled = true,
                        shape = MaterialTheme.shapes.small,
                        border = null,
                        elevation = null,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = MaterialTheme.colors.primary,
                            disabledBackgroundColor = Color.Transparent,
                            disabledContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_error_outline_24),
                            contentDescription = null,
                            tint = Color.Red,
                        )
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
                    Button(onClick = onExportClicked) {
                        Text(text = stringResource(id = R.string.select_btn_profile_export))
                    }
                    //The Select Button
                    Button(onClick = onSelectClicked) {
                        Text(text = stringResource(id = R.string.select_btn_profile_select))
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