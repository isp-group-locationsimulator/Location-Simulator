package com.ispgr5.locationsimulator.presentation.sound

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.presentation.universalComponents.ConfirmDeleteDialog
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar

/**
 * Shows a list of Audio Files to be selected.
 */
@Composable
fun SoundScreen(
	navController: NavController,
	viewModel: SoundViewModel = hiltViewModel(),
	soundStorageManager: SoundStorageManager,
	privateDirUri: String,
	recordAudio: () -> Unit,
) {
	val state = viewModel.state.value
	viewModel.onEvent(SoundEvent.RefreshPage(soundStorageManager = soundStorageManager))

	//Delte Confirmation
	var showDeleteConfirmDialog by remember { mutableStateOf(false) }
	var soundNameToDelete  by remember { mutableStateOf("")}  //String to store which sound should be deleted

	Scaffold(
		topBar = { TopBar(navController, stringResource(id = R.string.ScreenSound)) },
		content = {
			Spacer(modifier = Modifier.height(it.calculateTopPadding()))

			Column(
				modifier = Modifier.fillMaxSize(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				/**
				 * The refresh Button
				 */
				/**
				 * The refresh Button
				 */
				IconButton(
					onClick = {
						viewModel.onEvent(SoundEvent.RefreshPage(soundStorageManager = soundStorageManager))
					}
				) {
					Icon(
						painter = painterResource(id = R.drawable.baseline_refresh_24),
						contentDescription = null
					)
				}

				/**
				 * Header Text
				 */

				/**
				 * Header Text
				 */
				Text(
					text = stringResource(id = R.string.soundscreen_soundselection),
					fontSize = 30.sp
				)

				Row(
					horizontalArrangement = Arrangement.Center
				) {

					/**
					 * Import Button
					 */
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


					/**
					 * Record Button
					 */
					Button(
						colors = ButtonDefaults.buttonColors(),
						onClick = { recordAudio() },
						modifier = Modifier.padding(5.dp)
					) {
						Text(text = stringResource(id = R.string.soundscreen_record))
					}

					/**
					 * The Stop Playback button
					 */

					/**
					 * The Stop Playback button
					 */
					Button(
						colors = ButtonDefaults.buttonColors(),
						onClick = {
							viewModel.onEvent(SoundEvent.StopPlayback)
						},
						modifier = Modifier.padding(5.dp)
					) {
						Text(text = stringResource(id = R.string.soundscreen_stopplayback))
					}

				}

				Spacer(modifier = Modifier.height(20.dp))

				/**
				 * List of all known Sounds
				 */

				/**
				 * List of all known Sounds
				 */
				LazyColumn {
					items(state.soundNames) { soundName ->
						SingleSound(
							soundName,
							onPlayClicked = {
								viewModel.onEvent(
									SoundEvent.TestPlaySound(
										privateDirUri,
										soundName
									)
								)
							},
							onSelectClicked = {
								viewModel.onEvent(
									SoundEvent.SelectSound(
										soundName,
										navController
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