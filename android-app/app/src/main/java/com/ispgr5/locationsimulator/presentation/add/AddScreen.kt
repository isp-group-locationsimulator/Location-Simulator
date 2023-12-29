package com.ispgr5.locationsimulator.presentation.add

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.data.storageManager.ConfigurationStorageManager
import com.ispgr5.locationsimulator.presentation.settings.SettingsState
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar

/**
 * The Edit Screen.
 * Shows a Configuration that can be edited
 */
@ExperimentalAnimationApi
@Composable
fun AddScreen(
    navController: NavController,
    viewModel: AddViewModel = hiltViewModel(),
    configurationStorageManager: ConfigurationStorageManager,
    getDefaultValuesFunction: () -> SettingsState,
    scaffoldState: ScaffoldState
) {
    //The state from viewmodel
    val addScreenState = viewModel.state.value

    AddScreenScaffold(scaffoldState = scaffoldState,
        addScreenState = addScreenState,
        onBackClick = {
            navController.popBackStack()
        },
        onNameChange = {
            viewModel.onEvent(
                event = AddEvent.EnteredName(it)
            )
        },
        onDescriptionChange = { viewModel.onEvent(event = AddEvent.EnteredDescription(it)) },
        onSave = {
            viewModel.onEvent(
                event = AddEvent.SaveConfiguration(getDefaultValuesFunction)
            )
            //navigate back to the Select Screen
            navController.popBackStack()
        },
        onImportClick = {
            viewModel.onEvent(
                event = AddEvent.SelectedImportConfiguration(
                    configurationStorageManager = configurationStorageManager,
                )
            )
            navController.navigateUp()
        })
}

@Composable
fun AddScreenScreenshotPreview(addScreenState: AddScreenState) {
    val scaffoldState = rememberScaffoldState()
    AddScreenScaffold(scaffoldState = scaffoldState,
        addScreenState = addScreenState,
        onBackClick = {},
        onNameChange = {},
        onDescriptionChange = {},
        onSave = {},
        onImportClick = {})
}

@Composable
private fun AddScreenScaffold(
    scaffoldState: ScaffoldState,
    addScreenState: AddScreenState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    onImportClick: () -> Unit
) {
    Scaffold(scaffoldState = scaffoldState, topBar = {
        TopBar(onBackClick = onBackClick, stringResource(id = R.string.ScreenAdd))
    }, content = { paddingValues ->
        Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding()))
        Column(
            Modifier
                .padding(15.dp)
                .fillMaxSize()
        ) {
            //The name Input Field
            Text(text = stringResource(id = R.string.edit_name))
            TextField(
                value = addScreenState.name,
                onValueChange = onNameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.ADD_NAME_TEXTINPUT)
            )
            //The description Input Field
            Text(text = stringResource(id = R.string.edit_Description))
            TextField(
                value = addScreenState.description,
                onValueChange = onDescriptionChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.ADD_DESCRIPTION_TEXTINPUT)
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                //The save Configuration Button
                Button(
                    onClick = onSave,
                    modifier = Modifier.testTag(TestTags.ADD_SAVE_BUTTON),
                    enabled = addScreenState.name.isNotBlank()
                ) {
                    Icon(Icons.Default.Save, stringResource(id = R.string.edit_Save))
                    Text(text = stringResource(id = R.string.edit_Save))
                }
                Button(
                    onClick = onImportClick,
                ) {
                    Icon(Icons.Default.ImportExport, stringResource(id = R.string.edit_Import))
                    Text(text = stringResource(id = R.string.edit_Import))
                }
            }

        }
    })
}