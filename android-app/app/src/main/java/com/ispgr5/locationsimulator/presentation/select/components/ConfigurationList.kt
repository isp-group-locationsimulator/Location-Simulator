package com.ispgr5.locationsimulator.presentation.select.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData.previewConfigurations
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData.themePreviewState
import com.ispgr5.locationsimulator.ui.theme.DISABLED_ALPHA
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme

/**
 * Shows one Configuration as Button in max width
 */
@Composable
fun RowScope.OneConfigurationListMember(
    configuration: Configuration,
    isToggled: Boolean,
    onToggleClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onSelectClicked: () -> Unit,
    onExportClicked: () -> Unit,
    onDuplicateClicked: () -> Unit,
    hasErrors: Boolean,
    onErrorInfoClicked: () -> Unit,
    isFavorite: Boolean,
    onFavoriteClicked: () -> Unit
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.elevatedCardColors(
            containerColor = colorScheme.surfaceContainerHigh,
            contentColor = colorScheme.onSurface
        ),
        modifier = Modifier
            .weight(1f)
            .heightIn(min = 55.dp)
            .testTag(TestTags.SELECT_CONFIG_BUTTON_PREFIX + configuration.name)
            .clickable(true, onClick = onToggleClicked)

    ) {
        //Column is needed for toggling so the Toggled Information is shown under the Configuration name
        Column(modifier = Modifier.padding(4.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                //new row so the Configuration name is centered
                Column(
                    Modifier
                        .weight(8f)
                        .padding(start = 5.dp, end = 5.dp, top = 0.dp, bottom = 0.dp)

                ) {
                    Column {
                        Text(
                            text = configuration.name,
                            fontSize = 18.sp,
                        )
                        if (isToggled) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = configuration.description,
                                fontSize = 14.sp,
                            )
                        }
                    }
                }

                Column(Modifier.weight(1f)) {
                    //showing errors
                    if (hasErrors) {
                        Button(
                            onClick = onErrorInfoClicked,
                            contentPadding = PaddingValues(0.dp),
                            enabled = true,
                            shape = MaterialTheme.shapes.small,
                            border = null,
                            elevation = null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = colorScheme.primary
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
                            containerColor = Color.Transparent,
                            contentColor = colorScheme.primary
                        )
                    ) {
                        //favorite
                        if (isFavorite) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_star_24),
                                contentDescription = null,
                                tint = colorScheme.secondary,
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_star_outline_24),
                                contentDescription = null,
                                tint = colorScheme.onSurface,
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
                    //The Select Button
                    Button(
                        onClick = onSelectClicked,
                        contentPadding = PaddingValues(0.dp),
                        enabled = !hasErrors,
                        shape = MaterialTheme.shapes.small,
                        border = null,
                        elevation = null,
                        modifier = Modifier.testTag(TestTags.SELECT_CONFIG_BUTTON_SELECT_PREFIX + configuration.name),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = colorScheme.primary,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = colorScheme.error.copy(alpha = DISABLED_ALPHA),
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_play_arrow_24),
                            contentDescription = null,
                            tint = colorScheme.onSurface
                        )
                    }
                    //The Export Button
                    Button(
                        onClick = onExportClicked,
                        contentPadding = PaddingValues(0.dp),
                        enabled = !hasErrors,
                        shape = MaterialTheme.shapes.small,
                        border = null,
                        elevation = null,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = colorScheme.primary,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = colorScheme.error.copy(alpha = DISABLED_ALPHA),
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_ios_share_24),
                            contentDescription = null,
                            tint = colorScheme.onSurface
                        )
                    }
                    //The Edit Button
                    Button(
                        onClick = onEditClicked,
                        contentPadding = PaddingValues(0.dp),
                        enabled = true,
                        shape = MaterialTheme.shapes.small,
                        border = null,
                        elevation = null,
                        modifier = Modifier.testTag(TestTags.SELECT_CONFIG_BUTTON_EDIT_PREFIX + configuration.name),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = colorScheme.primary
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_edit_24),
                            contentDescription = null,
                            tint = colorScheme.onSurface
                        )
                    }
                    //The Duplicate Button
                    Button(
                        onClick = onDuplicateClicked,
                        contentPadding = PaddingValues(0.dp),
                        enabled = !hasErrors,
                        shape = MaterialTheme.shapes.small,
                        border = null,
                        elevation = null,
                        modifier = Modifier.testTag(TestTags.SELECT_CONFIG_BUTTON_DUPLICTAE_PREFIX + configuration.name),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = colorScheme.onSurface,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = colorScheme.error.copy(alpha = DISABLED_ALPHA),
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.duplicate_icon_24),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ConfigurationListMemberCollapsedPreview() {
    LocationSimulatorTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OneConfigurationListMember(
                configuration = previewConfigurations.first(),
                isToggled = false,
                onToggleClicked = { },
                onEditClicked = { },
                onSelectClicked = { },
                onExportClicked = { },
                onDuplicateClicked = { },
                hasErrors = false,
                onErrorInfoClicked = { },
                isFavorite = true,
                onFavoriteClicked = {}
            )
        }
    }
}


@Preview
@Composable
fun ConfigurationListMemberExpandedPreview() {
    LocationSimulatorTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OneConfigurationListMember(
                configuration = previewConfigurations.first(),
                isToggled = true,
                onToggleClicked = { },
                onEditClicked = { },
                onSelectClicked = { },
                onExportClicked = { },
                onDuplicateClicked = { },
                hasErrors = false,
                onErrorInfoClicked = { },
                isFavorite = true,
                onFavoriteClicked = {}
            )
        }
    }
}