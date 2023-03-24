package com.ispgr5.locationsimulator.presentation.select

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.data.storageManager.ConfigurationStorageManager
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.presentation.select.components.OneConfigurationListMember

/**
 * The Select Screen.
 * Shows A list of all Configuration from state
 */
@SuppressLint("MutableCollectionMutableState")
@ExperimentalAnimationApi
@Composable
fun SelectScreen(
	navController: NavController,
	viewModel: SelectViewModel = hiltViewModel(),
	configurationStorageManager: ConfigurationStorageManager,
	soundStorageManager: SoundStorageManager,
	toaster: (String) -> Unit
) {
	viewModel.updateConfigurationWithErrorsState(soundStorageManager = soundStorageManager)
	val state = viewModel.state.value
	var sizeOfDeletionConfiguration by remember { mutableStateOf(IntSize.Zero) }
	val notFound: String = stringResource(id = R.string.not_found)

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
				Modifier
					.fillMaxSize()
					.padding(end = 15.dp, top = 15.dp, start = 0.dp, bottom = 15.dp)
			} else {
				Modifier
					.padding(15.dp)
					.fillMaxSize()
			}
		) {

			//for all configurations in state we create a Row
			items(state.configurations) { configuration ->
				Row(
					if (configuration.id == state.selectedConfigurationForDeletion?.id) {
						Modifier
							.fillMaxWidth()
							.padding(start = 15.dp)
					} else {
						Modifier.fillMaxWidth()
					},

					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center
				) {
					if (state.isInDeleteMode && configuration.id != state.selectedConfigurationForDeletion?.id) {
						Column(Modifier.weight(1f)) {
							Button(
								onClick = {
									viewModel.onEvent(
										SelectEvent.SelectConfigurationForDeletion(
											configuration = configuration
										)
									)
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
									painter = painterResource(id = R.drawable.ic_baseline_cancel_24),
									contentDescription = null,
									tint = Color.Red,
								)
							}
						}
					}
					Column(
						Modifier
							.weight(5f)
							.onSizeChanged {
								if (configuration.id == state.selectedConfigurationForDeletion?.id) {
									sizeOfDeletionConfiguration = it
								}
							}) {
						val toastString = stringResource(id = R.string.export_toast_message)
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
										configurationStorageManager = configurationStorageManager
									)
								)
								toaster(toastString)
							},
							onDuplicateClicked = {
								viewModel.onEvent(
									SelectEvent.Duplicate(
										id = configuration.id
									)
								)
							},
							hasErrors = state.configurationsWithErrors.find { conf -> conf.id == configuration.id } != null,
							onErrorInfoClicked = {
								for (error in viewModel.whatIsHisErrors(
									configuration = configuration,
									soundStorageManager = soundStorageManager
								)) {
									toaster("$error $notFound")
								}
							},
							isFavorite = configuration.isFavorite,
							onFavoriteClicked = {
								viewModel.onEvent(
									SelectEvent.FavoriteClicked(
										configuration,
										toaster
									)
								)
							}
						)
					}
					if (state.selectedConfigurationForDeletion?.id == configuration.id) {
						Column(Modifier.weight(1.5f)) {
							Button(
								modifier = Modifier
									.then(
										with(LocalDensity.current) {
											Modifier.size(
												width = 9999.dp,
												height = sizeOfDeletionConfiguration.height.toDp(),
											)
										}
									),
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
						}
					}
				}
				Spacer(modifier = Modifier.height(6.dp))
			}
		}
	}
}