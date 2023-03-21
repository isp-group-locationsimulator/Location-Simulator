package com.ispgr5.locationsimulator.presentation.Add

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.data.storageManager.ConfigurationStorageManager

/**
 * The Edit Screen.
 * Shows a Configuration that can be edited
 */
@ExperimentalAnimationApi
@Composable
fun EditScreen(
	navController: NavController,
	viewModel: AddViewModel = hiltViewModel(),
	configurationStorageManager: ConfigurationStorageManager
) {
	//The state from viewmodel
	val state = viewModel.state.value

	Column(
		Modifier
			.padding(15.dp)
			.fillMaxSize()
	) {
		//The name Input Field
		Text(text = stringResource(id = R.string.edit_name))
		TextField(
			value = state.name,
			onValueChange = { viewModel.onEvent(AddEvent.EnteredName(it)) },
			modifier = Modifier.fillMaxWidth()
		)
		//The description Input Field
		Text(text = stringResource(id = R.string.edit_Description))
		TextField(
			value = state.description,
			onValueChange = { viewModel.onEvent(AddEvent.EnteredDescription(it)) },
			modifier = Modifier.fillMaxWidth()
		)
		//The save Configuration Button
		Button(onClick = {
			viewModel.onEvent(AddEvent.SaveConfiguration)
			//navigate back to the Select Screen
			navController.navigate("selectScreen")
		}) {
			Text(text = stringResource(id = R.string.edit_Save))
		}
		Button(onClick = {
			viewModel.onEvent(AddEvent.SelectedImportConfiguration(configurationStorageManager = configurationStorageManager))
			navController.navigateUp()
		}) {
			Text(text = stringResource(id = R.string.edit_Import))
		}
	}
}