package com.ispgr5.locationsimulator.presentation.select

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.presentation.select.components.OneConfigurationListMember

/**
 * The Select Screen.
 * Shows A list of all Configuration from state
 */
@ExperimentalAnimationApi
@Composable
fun SelectScreen(
    navController: NavController,
    viewModel: SelectViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            //Add and Delete Buttons should be on the right
            horizontalArrangement = Arrangement.End
        ) {
            //The Delete Button
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                onClick = {
                    viewModel.onEvent(SelectEvent.SelectDeleteMode)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_delete_outline_24),
                    contentDescription = null
                )
            }
            //The Add Button
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                onClick = {
                    navController.navigate(route = "editScreen")
                }
            ) {
                Icon(Icons.Outlined.Add, "")
            }
        }
        //The whole Column where all Configurations are in
        LazyColumn(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxSize()
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
                                Text(text = "Delete")
                            }
                        } else {
                            Button(
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                                onClick = {
                                    viewModel.onEvent(
                                        SelectEvent.SelectConfigurationForDeletion(
                                            configuration = configuration
                                        )
                                    )
                                }
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
                        onEditClicked = { navController.navigate("editScreen?configurationId=${configuration.id}") },
                        onSelectClicked = { navController.navigate("delayScreen?configurationId=${configuration.id}") },
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
    Button(onClick = {
        viewModel.onEvent(SelectEvent.TestSound)
    }) {
        Text(text = "Sound Test")
    }
}