package com.ispgr5.locationsimulator.presentation.select

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.FilePicker
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.StorageConfigInterface
import com.ispgr5.locationsimulator.presentation.select.components.OneConfigurationListMember

/**
 * The Select Screen.
 * Shows A list of all Configuration from state
 */
@ExperimentalAnimationApi
@Composable
fun SelectScreen(
    navController: NavController,
    viewModel: SelectViewModel = hiltViewModel(),
    storageConfigInterface: StorageConfigInterface,
    filePicker: FilePicker
) {
    viewModel.updateConfigurationWithErrorsState(filePicker = filePicker)
    val state = viewModel.state.value

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            //Add and Delete Buttons should be on the right
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //The Delete Button
            Button(
                onClick = { viewModel.onEvent(SelectEvent.SelectDeleteMode) },
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
                if (state.isInDeleteMode) {
                    Text(stringResource(id = R.string.finish_deletion))
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_delete_outline_24),
                        contentDescription = null
                    )
                }
            }
            //The Add Button
            Button(
                onClick = { navController.navigate(route = "editScreen") },
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
                    painter = painterResource(id = R.drawable.ic_baseline_add_24),
                    contentDescription = null
                )
            }
        }
        //The whole Column where all Configurations are in
        LazyColumn(
            modifier = if (state.isInDeleteMode) {
                Modifier.fillMaxSize().padding(end = 15.dp, top = 15.dp, start = 0.dp, bottom = 15.dp)
            } else {
                Modifier
                    .padding(15.dp)
                    .fillMaxSize()
            }
        ) {
            //for all configurations in state we create a Row
            items(state.configurations) { configuration ->
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (state.isInDeleteMode) {
                        if (configuration.id == state.selectedConfigurationForDeletion?.id) {
                            Button(
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                                onClick = {
                                    viewModel.onEvent(
                                        SelectEvent.DeleteConfiguration(
                                            configuration = configuration
                                        )
                                    )
                                }
                            ) {
                                Text(text = stringResource(id = R.string.select_btn_profile_delete))
                            }
                        } else {
                            Button(
                                onClick = { viewModel.onEvent(SelectEvent.SelectConfigurationForDeletion(configuration = configuration)) },
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
                                    painter = painterResource(id = R.drawable.ic_baseline_cancel_24),
                                    contentDescription = null,
                                    tint = Color.Red,
                                )
                            }
                        }
                    }
                    OneConfigurationListMember(
                        configuration = configuration,
                        isToggled = configuration.id == state.toggledConfiguration?.id,
                        onToggleClicked = {
                            viewModel.onEvent(
                                SelectEvent.ToggledConfiguration(
                                    configuration
                                )
                            )
                        },
                        onEditClicked = { navController.navigate("editTimeline?configurationId=${configuration.id}") },
                        onSelectClicked = { navController.navigate("delayScreen?configurationId=${configuration.id}") },
                        onExportClicked = {
                            viewModel.onEvent(
                                SelectEvent.SelectedExportConfiguration(
                                    configuration = configuration,
                                    storageConfigInterface = storageConfigInterface
                                )
                            )
                        },
                        hasErrors = state.configurationsWithErrors.find { conf -> conf.id == configuration.id } != null
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}