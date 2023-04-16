package com.ispgr5.locationsimulator.presentation.editTimeline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
import com.ispgr5.locationsimulator.presentation.settings.SettingsState
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar
import com.ispgr5.locationsimulator.presentation.util.Screen

/**
 * The Edit Screen.
 * Screen to Edit the Configuration
 */
@Composable
fun EditTimelineScreen(
	navController: NavController,
	viewModel: EditTimelineViewModel = hiltViewModel() ,
	getDefaultValuesFunction : () -> SettingsState
) {
	val state = viewModel.state.value
	var showCustomDialogWithResult by remember { mutableStateOf(false) }

	Scaffold(
		topBar = { TopBar(navController, stringResource(id = R.string.ScreenEdit),
			extraActions = {
				//The Add Button
				IconButton(
					onClick = { navController.navigate(route = Screen.SettingsScreen.route) },
				) {
					Icon(
						painter = painterResource(id = R.drawable.ic_baseline_settings_24),
						contentDescription = null,
					)

				}
			}
		)},
		content = {
			Spacer(modifier = Modifier.height(it.calculateTopPadding()))

			Column(
				modifier = Modifier.fillMaxSize()
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {

					/**
					 * Name, Description and Random order
					 */
					Column(
						modifier = Modifier
							.padding(12.dp)
							.fillMaxWidth()
							.weight(1f),
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Column {
							Row(
								modifier = Modifier.fillMaxWidth(),
								verticalAlignment = Alignment.CenterVertically
							) {
								Text(
									text = stringResource(id = R.string.editTimeline_name) + ":",
									fontWeight = FontWeight.Bold,
									modifier = Modifier.width(125.dp),
									color = MaterialTheme.colors.onBackground
								)
								Divider(
									color = MaterialTheme.colors.background,
									modifier = Modifier
										.width(10.dp)
								)
								Column(
									modifier = Modifier.fillMaxWidth(),
									horizontalAlignment = Alignment.CenterHorizontally,
									verticalArrangement = Arrangement.Center
								) {
									BasicTextField(
										value = state.name,
										modifier = Modifier.testTag(TestTags.EDIT_CONFIG_NAME_TEXTINPUT),
										onValueChange = { name ->
											viewModel.onEvent(
												EditTimelineEvent.ChangedName(
													name
												)
											)
										},
										textStyle = TextStyle(
											color = MaterialTheme.colors.onBackground
										)
									)
								}
							}
						}
						Spacer(modifier = Modifier.size(4.dp))
						Column {
							Row(
								modifier = Modifier.fillMaxWidth(),
								verticalAlignment = Alignment.CenterVertically
							) {
								Text(
									text = stringResource(id = R.string.editTimeline_description) + ":",
									fontWeight = FontWeight.Bold,
									modifier = Modifier.width(125.dp)
								)
								Divider(
									color = MaterialTheme.colors.background,
									modifier = Modifier
										.width(10.dp)
								)
								Column(
									modifier = Modifier.fillMaxWidth(),
									horizontalAlignment = Alignment.CenterHorizontally,
									verticalArrangement = Arrangement.Center
								) {
									BasicTextField(
										value = state.description,
										modifier = Modifier.testTag(TestTags.EDIT_CONFIG_DESCRIPTION_TEXTINPUT),
										onValueChange = { description ->
											viewModel.onEvent(
												EditTimelineEvent.ChangedDescription(
													description
												)
											)
										},
										textStyle = TextStyle(
											color = MaterialTheme.colors.onBackground
										)
									)
								}
							}
						}
						Spacer(modifier = Modifier.size(4.dp))
						Column {
							Row(
								modifier = Modifier.fillMaxWidth(),
								verticalAlignment = Alignment.CenterVertically
							) {
								Text(
									text = stringResource(id = R.string.editTimeline_randomOrderPlayback) + ":",
									fontWeight = FontWeight.Bold,
									modifier = Modifier.width(125.dp)
								)
								Column(
									modifier = Modifier.fillMaxWidth(),
									horizontalAlignment = Alignment.CenterHorizontally,
									verticalArrangement = Arrangement.Center
								) {
									Switch(
										checked = state.randomOrderPlayback,
										onCheckedChange = { randomOrderPlayback ->
											viewModel.onEvent(
												EditTimelineEvent.ChangedRandomOrderPlayback(
													randomOrderPlayback
												)
											)
										}
									)
								}
							}
						}
					}
				}

				Spacer(modifier = Modifier.size(4.dp))
				Divider(color = MaterialTheme.colors.onBackground, thickness = 1.dp)
				Spacer(modifier = Modifier.size(7.dp))

				/**
				 * The TimeLine
				 */
				Timeline(
					components = state.components,
					selectedComponent = state.current,
					onSelectAComponent = fun(configItem: ConfigComponent) {
						viewModel.onEvent(
							EditTimelineEvent.SelectedTimelineItem(configItem)
						)
					},
					onAddClicked = fun() { showCustomDialogWithResult = true }
				)
				Spacer(modifier = Modifier.size(7.dp))
				Divider(color = MaterialTheme.colors.onBackground, thickness = 1.dp)

				/**
				 * The Edit Options, like Slider
				 */
				if (state.current != null) {
					EditConfigComponent(
						state.current,
						onSoundValueChanged = fun(range: ClosedFloatingPointRange<Float>) {
							viewModel.onEvent(
								EditTimelineEvent.ChangedSoundVolume(range)
							)
						},
						onPauseValueChanged = fun(range: ClosedFloatingPointRange<Float>) {
							viewModel.onEvent(
								EditTimelineEvent.ChangedPause(range)
							)
						},
						onVibStrengthChanged = fun(range: ClosedFloatingPointRange<Float>) {
							viewModel.onEvent(
								EditTimelineEvent.ChangedVibStrength(range)
							)
						},
						onVibDurationChanged = fun(range: ClosedFloatingPointRange<Float>) {
							viewModel.onEvent(
								EditTimelineEvent.ChangedVibDuration(range)
							)
						},
						onDeleteClicked = fun(configComponent: ConfigComponent) {
							viewModel.onEvent(
								EditTimelineEvent.DeleteConfigurationComponent(
									configComponent
								)
							)
						},
						onMoveLeftClicked = fun(configComponent: ConfigComponent) {
							viewModel.onEvent(
								EditTimelineEvent.MoveConfCompLeft(configComponent)
							)
						},
						onMoveRightClicked = fun(configComponent: ConfigComponent) {
							viewModel.onEvent(
								EditTimelineEvent.MoveConfCompRight(configComponent)
							)
						},
						onConfigComponentNameChanged = { name ->
							viewModel.onEvent(
								EditTimelineEvent.ChangeConfigComponentName(name)
							)
						},
						onCopyConfigComponent = { configComponent ->
							viewModel.onEvent(
								EditTimelineEvent.CopyConfigComponent(configComponent)
							)
						}
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
						viewModel.onEvent(
							EditTimelineEvent.AddSound(navController)
						)
					},
					onVibrationClicked = {
						revShowDialog()
						viewModel.onEvent(
							EditTimelineEvent.AddVibration(getDefaultValuesFunction)
						)
					}
				)
			}
		})

}