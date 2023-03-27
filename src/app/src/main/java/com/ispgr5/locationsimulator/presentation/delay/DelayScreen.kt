package com.ispgr5.locationsimulator.presentation.delay

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.presentation.editTimeline.components.Timeline
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar

/**
 * The Delay Screen.
 * Here you can check you have Select the right Configuration
 * and set a timer
 */
@ExperimentalAnimationApi
@Composable
fun DelayScreen(
	navController: NavController,
	viewModel: DelayViewModel = hiltViewModel(),
	startServiceFunction: (List<ConfigComponent>, Boolean) -> Unit,
) {
	//The state from viewmodel
	val state = viewModel.state.value

	Scaffold(
		topBar = { TopBar(navController, stringResource(id = R.string.ScreenDelay)) },
		content = {
			Spacer(modifier = Modifier.height(it.calculateTopPadding()))
			Column(
				Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				if (state.configuration == null) {
					Text(text = "Configuration is null")
				} else {
					Text(text = state.configuration.name)
					Text(text = state.configuration.description)
				}
				Button(
					onClick = {
						viewModel.onEvent(DelayEvent.StartClicked(startServiceFunction))
						navController.navigate("runScreen")
					},
					enabled = state.configuration != null
				) {
					Text(text = stringResource(id = R.string.delay_btn_start))
				}

				/**
				 * The Timeline
				 */
				Spacer(modifier = Modifier.size(8.dp))
				Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
				LazyColumn(
					modifier = Modifier.fillMaxSize()
				) {
					items(1) {
						state.configuration?.components?.let {
							Timeline(
								components = it,
								selectedComponent = null,
								onSelectAComponent = fun(_: ConfigComponent) {},
								onAddClicked = fun() {},
								showAddButton = false
							)
						}
					}
				}
			}
		})
}