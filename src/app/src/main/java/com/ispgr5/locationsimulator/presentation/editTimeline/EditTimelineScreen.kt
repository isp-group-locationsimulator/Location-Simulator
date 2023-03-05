package com.ispgr5.locationsimulator.presentation.editTimeline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.presentation.editTimeline.components.AddConfigComponentDialog
import com.ispgr5.locationsimulator.presentation.editTimeline.components.EditConfigComponent
import com.ispgr5.locationsimulator.presentation.editTimeline.components.Timeline


@Composable
fun EditTimelineScreen(
    navController: NavController,
    viewModel: EditTimelineViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    var showCustomDialogWithResult by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        /**
         * Name and Description
         */
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.editTimeline_name) + ":",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(100.dp)
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        BasicTextField(
                            value = state.name,
                            onValueChange = { name -> viewModel.onEvent(EditTimelineEvent.ChangedName(name)) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(4.dp))
            Column {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(id = R.string.editTimeline_description) + ":",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(100.dp)
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        BasicTextField(
                            value = state.description,
                            onValueChange = { description -> viewModel.onEvent(EditTimelineEvent.ChangedDescription(description)) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(4.dp))
        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
        Spacer(modifier = Modifier.size(7.dp))

        /**
         * The TimeLine
         */
        Timeline(
            components = state.components,
            selectedComponent = state.current,
            onSelectAComponent = fun(configItem: ConfigComponent) { viewModel.onEvent(EditTimelineEvent.SelectedTimelineItem(configItem)) },
            onAddClicked = fun() { showCustomDialogWithResult = true }
        )
        Spacer(modifier = Modifier.size(7.dp))
        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)

        /**
         * The Edit Options, like Slider
         */
        if (viewModel.state.value.currentTimelineIndex != -1) {
            EditConfigComponent(
                state.current,
                onSoundValueChanged = fun(range: ClosedFloatingPointRange<Float>) { viewModel.onEvent(EditTimelineEvent.ChangedSoundVolume(range)) },
                onPauseValueChanged = fun(range: ClosedFloatingPointRange<Float>) { viewModel.onEvent(EditTimelineEvent.ChangedPause(range)) },
                onVibStrengthChanged = fun(range: ClosedFloatingPointRange<Float>) { viewModel.onEvent(EditTimelineEvent.ChangedVibStrength(range)) },
                onVibDurationChanged = fun(range: ClosedFloatingPointRange<Float>) { viewModel.onEvent(EditTimelineEvent.ChangedVibDuration(range)) },
                onDeleteClicked = fun(configComponent: ConfigComponent) {
                    viewModel.onEvent(
                        EditTimelineEvent.DeleteConfigurationComponent(
                            configComponent
                        )
                    )
                },
                onMoveUpClicked = fun(configComponent: ConfigComponent) { viewModel.onEvent(EditTimelineEvent.MoveConfCompUp(configComponent)) },
                onMoveDownClicked = fun(configComponent: ConfigComponent) { viewModel.onEvent(EditTimelineEvent.MoveConfCompDown(configComponent)) }
            )
        }
    }

    /**
     * The Select Vibration or Sound Dialog
     */
    val revShowDialog = fun() { showCustomDialogWithResult = !showCustomDialogWithResult }
    if (showCustomDialogWithResult) {
        AddConfigComponentDialog(
            onDismiss = { revShowDialog() },
            onNegativeClick = { revShowDialog() },
            onSoundClicked = {
                revShowDialog()
                viewModel.onEvent(EditTimelineEvent.AddSound)
                //TODO let User pick a Sound from private dir
                //Button(onClick = { navController.navigate("sound") }) {
                //    Text(text = stringResource(id = R.string.editTimeline_addSound))
                //}
            },
            onVibrationClicked = {
                revShowDialog()
                viewModel.onEvent(EditTimelineEvent.AddVibration)
            }
        )
    }

}