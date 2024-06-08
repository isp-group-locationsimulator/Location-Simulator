package com.ispgr5.locationsimulator.presentation.sound

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.presentation.settings.SettingsState
import com.ispgr5.locationsimulator.presentation.universalComponents.ConfirmDeleteDialog
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar
import kotlinx.coroutines.delay

/**
 * Shows a list of Audio Files to be selected.
 */
@Composable
fun SoundScreen(
    navController: NavController,
    viewModel: SoundViewModel = hiltViewModel(),
    soundStorageManager: SoundStorageManager,
    soundsDirUri: String,
    recordAudio: () -> Unit,
    getDefaultValuesFunction: () -> SettingsState,
    scaffoldState: ScaffoldState
) {
    val state = viewModel.state.value

    // Refresh screen every second so imported and recorded sound are being shown
    LaunchedEffect(true) {
        while (true) {
            viewModel.onEvent(SoundEvent.RefreshPage(soundStorageManager = soundStorageManager))
            delay(1000)
        }
    }

    var soundNameToDelete: String? by remember { mutableStateOf(null) }  //String to store which sound should be deleted

    SoundScreenScaffold(
        state = state,
        scaffoldState = scaffoldState,
        onBackClick = { navController.popBackStack() },
        currentPlayingSoundName = viewModel.isPlaying.value,
        soundNameToDelete = soundNameToDelete,
        onImportSoundClick = { viewModel.onEvent(SoundEvent.ImportSound(soundStorageManager = soundStorageManager)) },
        onRecordClick = {
            viewModel.onEvent(SoundEvent.StopPlayback)
            recordAudio()
        },
        onPlayClick = { soundName ->
            viewModel.onEvent(
                SoundEvent.TestPlaySound(
                    soundsDirUri,
                    soundName
                )
            )
        },
        onStopPlayingClick = {
            viewModel.onEvent(SoundEvent.StopPlayback)
        },
        onSelectSoundClick = { soundName ->
            viewModel.onEvent(
                SoundEvent.SelectSound(
                    soundName,
                    navController,
                    getDefaultValuesFunction
                )
            )
        },
        onDeleteSoundClick = { soundName ->
            soundNameToDelete = soundName
        },
        onDismissShowDeleteDialog = {
            soundNameToDelete = null
        },
        onConfirmDeletion = { soundName ->
            viewModel.onEvent(
                SoundEvent.DeleteSound(
                    soundName,
                    soundStorageManager
                )
            )
        }
    )
}

@Composable
fun SoundScreenScaffold(
    state: SoundState,
    scaffoldState: ScaffoldState,
    currentPlayingSoundName: String?,
    soundNameToDelete: String?,
    onBackClick: () -> Unit,
    onImportSoundClick: () -> Unit,
    onRecordClick: () -> Unit,
    onPlayClick: (String) -> Unit,
    onStopPlayingClick: () -> Unit,
    onSelectSoundClick: (String) -> Unit,
    onDeleteSoundClick: (String) -> Unit,
    onDismissShowDeleteDialog: () -> Unit,
    onConfirmDeletion: (String) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(
                onBackClick = onBackClick,
                title = stringResource(id = R.string.ScreenSound)
            )
        }
    ) {
        Spacer(modifier = Modifier.height(it.calculateTopPadding()))

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {

                /**
                 * Import Button
                 */
                Button(
                    onClick = onImportSoundClick,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Text(text = stringResource(id = R.string.soundscreen_import))
                }

                /**
                 * Record Button
                 */
                Button(
                    onClick = onRecordClick,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Text(text = stringResource(id = R.string.soundscreen_record))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            /**
             * List of all known Sounds
             */
            LazyColumn {
                items(items = state.soundNames) { soundName ->
                    SingleSound(
                        soundName = soundName,
                        currentPlayingSoundName = currentPlayingSoundName,
                        onStopClicked = onStopPlayingClick,
                        onPlayClicked = { onPlayClick(soundName) },
                        onSelectClicked = { onSelectSoundClick(soundName) },
                        onDeleteClicked = { onDeleteSoundClick(soundName) }
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }

        //Dialog to confirm the deleting of an config Component
        if (soundNameToDelete != null) {
            ConfirmDeleteDialog(
                itemToDeleteName = soundNameToDelete,
                onDismiss = onDismissShowDeleteDialog
            ) {
                onDismissShowDeleteDialog()
                onConfirmDeletion(soundNameToDelete)
            }
        }
    }
}

@Composable
fun SoundScreenScreenshotPreview(
    state: SoundState,
    currentPlayingSoundName: String? = null,
    selectedForDeletion: String? = null
) {
    SoundScreenScaffold(
        state = state,
        scaffoldState = rememberScaffoldState(),
        currentPlayingSoundName = currentPlayingSoundName,
        soundNameToDelete = selectedForDeletion,
        onBackClick = {},
        onImportSoundClick = {},
        onRecordClick = {},
        onPlayClick = {},
        onStopPlayingClick = {},
        onSelectSoundClick = {},
        onDeleteSoundClick = {},
        onDismissShowDeleteDialog = {},
        onConfirmDeletion = {}
    )
}