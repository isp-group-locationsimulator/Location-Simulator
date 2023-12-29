package com.ispgr5.locationsimulator.presentation.sound

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
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
	LaunchedEffect(true){
		while (true){
			viewModel.onEvent(SoundEvent.RefreshPage(soundStorageManager = soundStorageManager))
			delay(1000)
		}
	}

	//Delete Confirmation
	var showDeleteConfirmDialog by remember { mutableStateOf(false) }
	var soundNameToDelete  by remember { mutableStateOf("")}  //String to store which sound should be deleted

	Scaffold(
		scaffoldState = scaffoldState,
		topBar = { TopBar(navController, stringResource(id = R.string.ScreenSound)) },
		content = {
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
						colors = ButtonDefaults.buttonColors(),
						onClick = {
							viewModel.onEvent(SoundEvent.ImportSound(soundStorageManager = soundStorageManager))
						},
						modifier = Modifier.padding(5.dp)
					) {
						Text(text = stringResource(id = R.string.soundscreen_import))
					}

					/**
					 * Record Button
					 */
					Button(
						colors = ButtonDefaults.buttonColors(),
						onClick = {
							viewModel.onEvent(SoundEvent.StopPlayback)
							recordAudio() },
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
					items(state.soundNames) { soundName ->
						SingleSound(
							soundName,
							viewModel = viewModel,
							onStopClicked = {
								viewModel.onEvent(SoundEvent.StopPlayback)
							},
							onPlayClicked = {
								viewModel.onEvent(
									SoundEvent.TestPlaySound(
										soundsDirUri,
										soundName
									)
								)
							},
							onSelectClicked = {
								viewModel.onEvent(
									SoundEvent.SelectSound(
										soundName,
										navController,
										getDefaultValuesFunction
									)
								)
							},
							onDeleteClicked = {
								soundNameToDelete = soundName
								showDeleteConfirmDialog = true
							}
						)
						Spacer(modifier = Modifier.height(5.dp))
					}
				}
			}

			//Dialog to confirm the deleting of an config Component
			val revShowDialog = fun() { showDeleteConfirmDialog = !showDeleteConfirmDialog }
			if (showDeleteConfirmDialog) {
				ConfirmDeleteDialog(onDismiss = revShowDialog, onConfirm = {
					showDeleteConfirmDialog = false
					viewModel.onEvent(
						SoundEvent.DeleteSound(
							soundNameToDelete,
							soundStorageManager
						)
					)
				})
			}
		})
}