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
    hasErrors: Boolean,
    onErrorInfoClicked: () -> Unit,
    isFavorite: Boolean,
    onFavoriteClicked: () -> Unit
) {
    val rowBackgroundColor: Color = Color.LightGray

    Button(
        onClick = onToggleClicked,
        contentPadding = PaddingValues(0.dp),
        enabled = true,
        shape = MaterialTheme.shapes.small,
        border = null,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
        ),
        modifier = Modifier
            //.border(width = 1.dp, color = MaterialTheme.colors.primary, shape = RoundedCornerShape(6.dp))
            .background(rowBackgroundColor, shape = RoundedCornerShape(6.dp))
            .fillMaxWidth()
            .heightIn(min = 55.dp)

    ) {
        //Column is needed for toggling so the Toggled Information is shown under the Configuration name
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (!hasErrors) {
                    Arrangement.Start
                } else {
                    Arrangement.Start
                },
            ) {
                //new row so the Configuration name is centered
                Column(
                    Modifier
                        .weight(8f)
                        .padding(start = 5.dp, end = 5.dp, top = 0.dp, bottom = 0.dp)

                ) {
                    Column {
                        Text(text = configuration.name)
                        if (isToggled) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(text = configuration.description)
                        }
                    }
                }

                Column(Modifier.weight(1f)) {
                    if (hasErrors) {
                        Button(
                            onClick = onErrorInfoClicked,
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
                Column(Modifier.weight(1f)) {
                    Button(
                        onClick = onFavoriteClicked,
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
                        if (isFavorite) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_star_24),
                                contentDescription = null,
                                tint = Color.Yellow,
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_star_outline_24),
                                contentDescription = null,
                                tint = Color.Black,
                            )
                        }
                    }
                }

                Column(Modifier.weight(1f)) {
                    Icon(
                        painter = if (isToggled) {
                            painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24)
                        } else {
                            painterResource(id = R.drawable.ic_baseline_keyboard_arrow_right_24)
                        },
                        contentDescription = null
                    )
                }
            }
            //The Information which is shown when toggle is active
            if (isToggled) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(onClick = onExportClicked, enabled = !hasErrors) {
                        Text(text = stringResource(id = R.string.select_btn_profile_export))
                    }
                    //The Select Button
                    Button(onClick = onSelectClicked, enabled = !hasErrors) {
                        Text(text = stringResource(id = R.string.select_btn_profile_select))
                    }
                    //The Edit Button
                    Button(
                        onClick = onEditClicked,
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
                            painter = painterResource(id = R.drawable.ic_baseline_edit_24),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}