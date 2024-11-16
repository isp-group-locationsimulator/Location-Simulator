package com.ispgr5.locationsimulator.presentation.select

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gigamole.composescrollbars.Scrollbars
import com.gigamole.composescrollbars.config.ScrollbarsConfig
import com.gigamole.composescrollbars.config.ScrollbarsOrientation
import com.gigamole.composescrollbars.rememberScrollbarsState
import com.gigamole.composescrollbars.scrolltype.ScrollbarsScrollType
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.data.storageManager.ConfigurationStorageManager
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData.selectScreenPreviewState
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData.selectScreenPreviewStateDelete
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData.themePreviewState
import com.ispgr5.locationsimulator.presentation.select.components.OneConfigurationListMember
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent
import com.ispgr5.locationsimulator.presentation.universalComponents.LocationSimulatorTopBar
import com.ispgr5.locationsimulator.presentation.util.AppSnackbarHost
import com.ispgr5.locationsimulator.presentation.util.RenderSnackbarOnChange
import com.ispgr5.locationsimulator.presentation.util.Screen
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme


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
    snackbarHostState: SnackbarHostState,
    snackbarContent: MutableState<SnackbarContent?>
) {
    viewModel.updateConfigurationWithErrorsState(soundStorageManager = soundStorageManager)
    val selectScreenState = viewModel.state.value
    val context = LocalContext.current
    RenderSnackbarOnChange(snackbarHostState = snackbarHostState, snackbarContent)

    SelectScreenScaffold(
        selectScreenState = selectScreenState,
        snackbarHostState = snackbarHostState,
        onBackClick = {
            navController.popBackStack()
        },
        onSelectDeleteModeClick = {
            when (selectScreenState.isInDeleteMode) {
                false -> viewModel.onEvent(SelectEvent.SelectDeleteMode)
                else -> viewModel.onEvent(SelectEvent.SelectNormalMode)
            }
        },
        onClickAddScreenButton = {
            viewModel.onEvent(SelectEvent.SelectNormalMode)
            navController.navigate(route = Screen.AddScreen.route)
        },
        onSelectForDeletion = { configuration ->
            viewModel.onEvent(
                SelectEvent.SelectConfigurationForDeletion(
                    configuration = configuration
                )
            )
        },
        onToggleConfiguration = { configuration ->
            viewModel.onEvent(SelectEvent.ToggledConfiguration(configuration))
        },
        onEditConfiguration = { configuration ->
            navController.navigate(
                Screen.EditTimelineScreen.createRoute(configuration.id!!)
            )
        },
        onSelectConfiguration = { configuration ->
            navController.navigate(
                Screen.DelayScreen.createRoute(
                    configuration.id!!
                )
            )
        },
        onExportConfiguration = { configuration ->
            viewModel.onEvent(
                SelectEvent.SelectedExportConfiguration(
                    configuration = configuration,
                    configurationStorageManager = configurationStorageManager,
                    context = context
                )
            )
        },
        onDuplicateClicked = { configuration ->
            viewModel.onEvent(SelectEvent.Duplicate(id = configuration.id))
        },
        onErrorInfoClicked = { configuration ->
            for (error in viewModel.whatIsHisErrors(
                configuration = configuration, soundStorageManager = soundStorageManager
            )) {
                snackbarContent.value = SnackbarContent(
                    text = "$error ${context.getString(R.string.not_found)}",
                    actionLabel = context.getString(android.R.string.ok),
                    snackbarDuration = SnackbarDuration.Indefinite
                )
            }
        },
        onFavouriteClicked = { configuration ->
            viewModel.onEvent(
                SelectEvent.FavoriteClicked(
                    configuration = configuration, snackbarContent = snackbarContent
                )
            )
        },
        onDeleteConfiguration = { configuration ->
            viewModel.onEvent(
                SelectEvent.DeleteConfiguration(
                    configuration = configuration
                )
            )
        })
}

@Composable
private fun ConfigurationList(
    selectScreenState: SelectScreenState,
    onSelectForDeletion: (Configuration) -> Unit,
    onToggleConfiguration: (Configuration) -> Unit,
    onEditConfiguration: (Configuration) -> Unit,
    onSelectConfiguration: (Configuration) -> Unit,
    onExportConfiguration: (Configuration) -> Unit,
    onDuplicateClicked: (Configuration) -> Unit,
    onErrorInfoClicked: (Configuration) -> Unit,
    onFavouriteClicked: (Configuration) -> Unit,
    onDeleteConfiguration: (Configuration) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val lazyListState = rememberLazyListState()
        val scrollbarsState = rememberScrollbarsState(
            config = ScrollbarsConfig(orientation = ScrollbarsOrientation.Vertical),
            scrollType = ScrollbarsScrollType.Lazy.List.Static(
                state = lazyListState
            )
        )
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //for all configurations in state we create a Row
                items(selectScreenState.configurations) { configuration ->
                    val deleteThis =
                        selectScreenState.selectedConfigurationForDeletion?.equals(configuration) == true
                    Row(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (selectScreenState.isInDeleteMode && !deleteThis) {
                            IconButton(
                                onClick = { onSelectForDeletion(configuration) },
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_delete_outline_24),
                                    contentDescription = null,
                                    tint = colorScheme.onBackground,
                                )
                            }
                        }

                        OneConfigurationListMember(
                            configuration = configuration,
                            isToggled = configuration.id == selectScreenState.toggledConfiguration?.id,
                            onToggleClicked = {
                                onToggleConfiguration(configuration)
                            },
                            onEditClicked = {
                                onEditConfiguration(configuration)
                            },
                            onSelectClicked = {
                                onSelectConfiguration(configuration)
                            },
                            onExportClicked = {
                                onExportConfiguration(configuration)
                            },
                            onDuplicateClicked = {
                                onDuplicateClicked(configuration)
                            },
                            hasErrors = selectScreenState.configurationsWithErrors.find { conf -> conf.id == configuration.id } != null,
                            onErrorInfoClicked = {
                                onErrorInfoClicked(configuration)
                            },
                            isFavorite = configuration.isFavorite,
                            onFavoriteClicked = {
                                onFavouriteClicked(configuration)
                            })

                        if (selectScreenState.isInDeleteMode && deleteThis) {
                            Spacer(modifier = Modifier.width(2.dp))
                            ElevatedCard(
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(horizontal = 2.dp)
                                    .clickable {
                                        onDeleteConfiguration(configuration)
                                    },
                                colors = CardDefaults.cardColors(containerColor = colorScheme.error)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .width(IntrinsicSize.Min)
                                        .fillMaxHeight(),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = stringResource(id = R.string.delete_configuration),
                                        tint = colorScheme.onError.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
        Scrollbars(state = scrollbarsState)
    }
}

@Composable
private fun AddButton(onClickAddScreenButton: () -> Unit) {
    OutlinedButton(
        onClick = onClickAddScreenButton,
        contentPadding = PaddingValues(0.dp),
        enabled = true,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(15.dp, 15.dp, 15.dp, 0.dp)
            .fillMaxWidth()
            .heightIn(min = 55.dp)
            .testTag(TestTags.SELECT_ADD_BUTTON)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_add_24), contentDescription = null
        )
        Text(stringResource(R.string.new_configuration), fontSize = 18.sp)
    }
}

@Composable
private fun SelectScreenTopBar(
    onBackClick: () -> Unit,
    onSelectDeleteModeClick: () -> Unit,
    isInDeleteMode: Boolean,
) {
    LocationSimulatorTopBar(onBackClick = onBackClick,
        title = stringResource(id = R.string.ScreenSelect),
        extraActions = {
            //The Delete Button
            IconButton(
                onClick = onSelectDeleteModeClick,
            ) {
                when {
                    isInDeleteMode -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_check_24),
                            contentDescription = null
                        )
                    }

                    else -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_delete_outline_24),
                            contentDescription = null
                        )
                    }
                }
            }
        })
}

@Composable
fun SelectScreenPreviewScaffold(selectScreenState: SelectScreenState) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    SelectScreenScaffold(
        selectScreenState = selectScreenState,
        snackbarHostState = snackbarHostState,
        onBackClick = {},
        onSelectDeleteModeClick = {},
        onClickAddScreenButton = {},
        onDeleteConfiguration = {},
        onFavouriteClicked = {},
        onErrorInfoClicked = {},
        onDuplicateClicked = {},
        onSelectConfiguration = {},
        onExportConfiguration = {},
        onEditConfiguration = {},
        onToggleConfiguration = {},
        onSelectForDeletion = {})
}

@Composable
fun SelectScreenScaffold(
    selectScreenState: SelectScreenState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onSelectDeleteModeClick: () -> Unit,
    onClickAddScreenButton: () -> Unit,
    onSelectForDeletion: (Configuration) -> Unit,
    onToggleConfiguration: (Configuration) -> Unit,
    onEditConfiguration: (Configuration) -> Unit,
    onSelectConfiguration: (Configuration) -> Unit,
    onExportConfiguration: (Configuration) -> Unit,
    onDuplicateClicked: (Configuration) -> Unit,
    onErrorInfoClicked: (Configuration) -> Unit,
    onFavouriteClicked: (Configuration) -> Unit,
    onDeleteConfiguration: (Configuration) -> Unit
) {
    Scaffold(
        snackbarHost = {
            AppSnackbarHost(snackbarHostState)
        },
        topBar = {
            SelectScreenTopBar(
                onBackClick = onBackClick,
                onSelectDeleteModeClick = onSelectDeleteModeClick,
                isInDeleteMode = selectScreenState.isInDeleteMode
            )
        }, content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                AddButton(onClickAddScreenButton)
                ConfigurationList(
                    selectScreenState = selectScreenState,
                    onSelectForDeletion = onSelectForDeletion,
                    onToggleConfiguration = onToggleConfiguration,
                    onEditConfiguration = onEditConfiguration,
                    onSelectConfiguration = onSelectConfiguration,
                    onExportConfiguration = onExportConfiguration,
                    onDuplicateClicked = onDuplicateClicked,
                    onErrorInfoClicked = onErrorInfoClicked,
                    onFavouriteClicked = onFavouriteClicked,
                    onDeleteConfiguration = onDeleteConfiguration
                )
            }
        })
}


@Composable
@Preview
fun SelectScreenNormalPreview() {
    LocationSimulatorTheme {
        SelectScreenPreviewScaffold(selectScreenState = selectScreenPreviewState)
    }
}


@Composable
@Preview
fun SelectScreenDeleteModePreview() {
    LocationSimulatorTheme {
        SelectScreenPreviewScaffold(selectScreenState = selectScreenPreviewStateDelete)
    }
}