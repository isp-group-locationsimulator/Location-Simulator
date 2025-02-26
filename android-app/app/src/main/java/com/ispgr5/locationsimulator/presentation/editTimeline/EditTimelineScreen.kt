package com.ispgr5.locationsimulator.presentation.editTimeline

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.presentation.editTimeline.components.AddConfigComponentDialog
import com.ispgr5.locationsimulator.presentation.editTimeline.components.EditConfigComponent
import com.ispgr5.locationsimulator.presentation.editTimeline.components.Timeline
import com.ispgr5.locationsimulator.presentation.editTimeline.components.VibrationSupportHintMode
import com.ispgr5.locationsimulator.presentation.previewData.AppPreview
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData.editTimelineState
import com.ispgr5.locationsimulator.presentation.settings.SettingsState
import com.ispgr5.locationsimulator.presentation.universalComponents.LocationSimulatorTopBar
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent
import com.ispgr5.locationsimulator.presentation.util.AppSnackbarHost
import com.ispgr5.locationsimulator.presentation.util.BackPressGestureDisabler
import com.ispgr5.locationsimulator.presentation.util.RenderSnackbarOnChange
import com.ispgr5.locationsimulator.presentation.util.Screen
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme


/**
 * The Edit Screen.
 * Screen to Edit the Configuration
 */
@Composable
fun EditTimelineScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: EditTimelineViewModel = hiltViewModel(),
    getDefaultValuesFunction: () -> SettingsState
) {
    val state = viewModel.state.value
    var showCustomDialogWithResult by remember { mutableStateOf(false) }

    val snackbarContentState = remember {
        mutableStateOf<SnackbarContent?>(null)
    }

    RenderSnackbarOnChange(snackbarHostState, snackbarContentState)

    BackPressGestureDisabler(snackbarContentState)

    val editTimelineEventHandlers by remember {
        mutableStateOf(
            EditTimelineEventHandlers(
                onSoundValueChanged = {
                    viewModel.onEvent(EditTimelineEvent.ChangedSoundVolume(it))
                },
                onPauseValueChanged = {
                    viewModel.onEvent(EditTimelineEvent.ChangedPause(it))
                },
                onVibStrengthChanged = {
                    viewModel.onEvent(EditTimelineEvent.ChangedVibStrength(it))
                },
                onVibDurationChanged = {
                    viewModel.onEvent(EditTimelineEvent.ChangedVibDuration(it))
                },
                onDeleteClicked = {
                    viewModel.onEvent(EditTimelineEvent.DeleteConfigurationComponent(it))
                },
                onMoveLeftClicked = {
                    viewModel.onEvent(EditTimelineEvent.MoveConfCompLeft(it))
                },
                onMoveRightClicked = {
                    viewModel.onEvent(EditTimelineEvent.MoveConfCompRight(it))
                },
                onConfigComponentNameChanged = {
                    viewModel.onEvent(EditTimelineEvent.ChangeConfigComponentName(it))
                },
                onCopyConfigComponent = {
                    viewModel.onEvent(EditTimelineEvent.CopyConfigComponent(it))
                }
            )
        )
    }

    EditTimelineScaffold(
        state = state,
        isDialogShown = showCustomDialogWithResult,
        snackbarHostState = snackbarHostState,
        onBackClick = {
            navController.popBackStack()
        },
        onSettingsClick = {
            navController.navigate(route = Screen.SettingsScreen.route)
        },
        onToggleShowDialog = {
            showCustomDialogWithResult = !showCustomDialogWithResult
        },
        onChangeName = { name ->
            viewModel.onEvent(EditTimelineEvent.ChangedName(name))
        },
        onChangeDescription = {
            viewModel.onEvent(EditTimelineEvent.ChangedDescription(it))
        },
        onCheckRandomOrder = {
            viewModel.onEvent(EditTimelineEvent.ChangedRandomOrderPlayback(it))
        },
        onAddSoundClicked = {
            viewModel.onEvent(
                EditTimelineEvent.AddSound(navController)
            )
        },
        onAddVibrationClicked = {
            viewModel.onEvent(
                EditTimelineEvent.AddVibration(getDefaultValuesFunction)
            )
        },
        onSelectAComponent = { configItem ->
            viewModel.onEvent(EditTimelineEvent.SelectedTimelineItem(configItem))
        },
        editTimelineEventHandlers = editTimelineEventHandlers
    )

}

@Composable
fun EditTimelineScaffold(
    state: EditTimelineState,
    snackbarHostState: SnackbarHostState,
    isDialogShown: Boolean,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onToggleShowDialog: (Boolean) -> Unit,
    onChangeName: (String) -> Unit,
    onChangeDescription: (String) -> Unit,
    onCheckRandomOrder: (Boolean) -> Unit,
    onAddSoundClicked: () -> Unit,
    onAddVibrationClicked: () -> Unit,
    onSelectAComponent: (ConfigComponent) -> Unit,
    editTimelineEventHandlers: EditTimelineEventHandlers?,
    vibrationSupportHintMode: VibrationSupportHintMode = VibrationSupportHintMode.AUTOMATIC
) {
    val focusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            EditTimelineTopBar(onBackClick = onBackClick, onSettingsClick = onSettingsClick)
        },
        snackbarHost = {
            AppSnackbarHost(snackbarHostState)
        },
        modifier = Modifier.clickable(interactionSource = null, indication = null, onClick = {focusManager.clearFocus()})
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            ConfigMetadata(state, onChangeName, onChangeDescription, onCheckRandomOrder)
            HorizontalDivider(color = colorScheme.onBackground, thickness = 1.dp)

            /**
             * The TimeLine
             */
            Timeline(components = state.components,
                selectedComponent = state.current,
                onSelectAComponent = onSelectAComponent,
                onAddClicked = { onToggleShowDialog(true) })
            HorizontalDivider(color = colorScheme.onBackground, thickness = 1.dp)

            /**
             * The Edit Options, like Slider
             */
            if (state.current != null) {
                EditConfigComponent(
                    configComponent = state.current,
                    editTimelineEventHandlers = editTimelineEventHandlers,
                    vibrationSupportHintMode = vibrationSupportHintMode,
                    isFirstComponent = state.current.let {
                        state.components.first() == it
                    },
                    isLastComponent = state.current.let {
                        state.components.last() == it
                    }
                )
            }

        }

        /**
         * The Select Vibration or Sound Dialog
         */
        if (isDialogShown) {
            AddConfigComponentDialog(onDismiss = { onToggleShowDialog(false) },
                onNegativeClick = { onToggleShowDialog(false) },
                onSoundClicked = {
                    onToggleShowDialog(false)
                    onAddSoundClicked()
                },
                onVibrationClicked = {
                    onToggleShowDialog(false)
                    onAddVibrationClicked()
                })
        }
    }
}


@Composable
private fun ConfigMetadata(
    state: EditTimelineState,
    onChangeName: (String) -> Unit,
    onChangeDescription: (String) -> Unit,
    onCheckRandomOrder: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        OutlinedTextField(
            value = state.name,
            modifier = Modifier
                .testTag(TestTags.EDIT_CONFIG_NAME_TEXTINPUT)
                .fillMaxWidth(),
            onValueChange = onChangeName,
            label = {
                Text(stringResource(id = R.string.editTimeline_name))
            },
            singleLine = true
        )

        OutlinedTextField(
            value = state.description,
            modifier = Modifier
                .testTag(TestTags.EDIT_CONFIG_DESCRIPTION_TEXTINPUT)
                .fillMaxWidth(),
            maxLines = 2,
            singleLine = false,
            label = {
                Text(stringResource(id = R.string.editTimeline_description))
            },
            onValueChange = onChangeDescription,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(id = R.string.editTimeline_randomOrderPlayback),
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .weight(1.0f)
                    .padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(stringResource(id = R.string.playback_in_order))
                Switch(
                    checked = state.randomOrderPlayback,
                    onCheckedChange = onCheckRandomOrder,
                )
                Text(stringResource(id = R.string.playback_random_order))
            }
        }
    }
}

@Composable
fun EditTimelineTopBar(onBackClick: () -> Unit, onSettingsClick: () -> Unit) {
    LocationSimulatorTopBar(
        onBackClick = onBackClick,
        title = stringResource(id = R.string.ScreenEdit),
        extraActions = {
            //The Add Button
            IconButton(
                onClick = onSettingsClick,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_settings_24),
                    contentDescription = null,
                )

            }
        })
}

class EditTimelineEventHandlers(
    val onSoundValueChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    val onPauseValueChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    val onVibStrengthChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    val onVibDurationChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    val onDeleteClicked: (configComponent: ConfigComponent) -> Unit,
    val onMoveLeftClicked: (configComponent: ConfigComponent) -> Unit,
    val onMoveRightClicked: (configComponent: ConfigComponent) -> Unit,
    val onConfigComponentNameChanged: (name: String) -> Unit,
    val onCopyConfigComponent: (configComponent: ConfigComponent) -> Unit,
)

@Composable
fun EditTimelinePreviewScaffold(
    isDialogShown: Boolean,
    state: EditTimelineState,
    vibrationSupportHintMode: VibrationSupportHintMode
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    LocationSimulatorTheme {
        EditTimelineScaffold(
            state = state,
            snackbarHostState = snackbarHostState,
            isDialogShown = isDialogShown,
            onBackClick = {},
            onSettingsClick = {},
            onToggleShowDialog = {},
            onChangeName = {},
            onChangeDescription = {},
            onCheckRandomOrder = {},
            onAddSoundClicked = {},
            onAddVibrationClicked = {},
            onSelectAComponent = {},
            editTimelineEventHandlers = null,
            vibrationSupportHintMode = vibrationSupportHintMode
        )
    }
}


@Composable
@AppPreview
fun EditTimelineNormalPreview() {
    EditTimelinePreviewScaffold(
        isDialogShown = false,
        state = editTimelineState,
        vibrationSupportHintMode = VibrationSupportHintMode.SUPPRESSED
    )
}

@Composable
@AppPreview
fun EditTimelineDialogShownPreview() {
    EditTimelinePreviewScaffold(
        isDialogShown = true,
        state = editTimelineState,
        vibrationSupportHintMode = VibrationSupportHintMode.SUPPRESSED
    )
}

@Composable
@AppPreview
fun EditTimelineUnsupportedIntensityPreview() {
    EditTimelinePreviewScaffold(
        isDialogShown = false,
        state = editTimelineState,
        vibrationSupportHintMode = VibrationSupportHintMode.ENFORCED
    )
}

