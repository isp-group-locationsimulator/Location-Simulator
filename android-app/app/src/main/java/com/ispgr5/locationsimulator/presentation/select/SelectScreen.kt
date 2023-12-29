package com.ispgr5.locationsimulator.presentation.select

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
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
import com.ispgr5.locationsimulator.presentation.select.components.OneConfigurationListMember
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar
import com.ispgr5.locationsimulator.presentation.util.MakeSnackbar
import com.ispgr5.locationsimulator.presentation.util.Screen

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
    scaffoldState: ScaffoldState,
    snackbarContent: MutableState<SnackbarContent?>
) {
    viewModel.updateConfigurationWithErrorsState(soundStorageManager = soundStorageManager)
    val selectScreenState = viewModel.state.value
    val context = LocalContext.current
    MakeSnackbar(scaffoldState, snackbarContent)

    SelectScreenScaffold(
        scaffoldState = rememberScaffoldState(),
        selectScreenState = selectScreenState,
        onBackClick = {
            navController.popBackStack()
        },
        onSelectDeleteModeClick = { viewModel.onEvent(SelectEvent.SelectDeleteMode) },
        onClickAddScreenButton = { navController.navigate(route = Screen.AddScreen.route) },
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
                configuration = configuration,
                soundStorageManager = soundStorageManager
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
                    configuration = configuration,
                    snackbarContent = snackbarContent
                )
            )
        }
    ) { configuration ->
        viewModel.onEvent(
            SelectEvent.DeleteConfiguration(
                configuration = configuration
            )
        )
    }
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
    var sizeOfDeletionConfiguration by remember {
        mutableStateOf(IntSize.Zero)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        val lazyListState = rememberLazyListState()
        val scrollbarsState = rememberScrollbarsState(
            config = ScrollbarsConfig(orientation = ScrollbarsOrientation.Vertical),
            scrollType = ScrollbarsScrollType.Lazy.List.Static(
                state = lazyListState
            )
        )
        val lazyColumnModifier = when {
            selectScreenState.isInDeleteMode -> {
                Modifier
                    .fillMaxSize()
                    .padding(end = 15.dp, top = 6.dp, start = 0.dp, bottom = 15.dp)
            }

            else -> {
                Modifier
                    .padding(end = 15.dp, top = 6.dp, start = 15.dp, bottom = 15.dp)
                    .fillMaxSize()
            }
        }
        LazyColumn(
            modifier = lazyColumnModifier, state = lazyListState
        ) {
            //for all configurations in state we create a Row
            items(selectScreenState.configurations) { configuration ->
                Row(
                    if (configuration.id == selectScreenState.selectedConfigurationForDeletion?.id) {
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 15.dp)
                    } else {
                        Modifier.fillMaxWidth()
                    },

                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (selectScreenState.isInDeleteMode && configuration.id != selectScreenState.selectedConfigurationForDeletion?.id) {
                        Column(Modifier.weight(1f)) {
                            Button(
                                onClick = { onSelectForDeletion(configuration) },
                                contentPadding = PaddingValues(0.dp),
                                enabled = true,
                                shape = MaterialTheme.shapes.small,
                                border = null,
                                elevation = null,
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Transparent,
                                    contentColor = MaterialTheme.colors.primary,
                                    disabledBackgroundColor = Color.Transparent,
                                    disabledContentColor = MaterialTheme.colors.primary.copy(
                                        alpha = ContentAlpha.disabled
                                    ),
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
                                if (configuration.id == selectScreenState.selectedConfigurationForDeletion?.id) {
                                    sizeOfDeletionConfiguration = it
                                }
                            }) {
                        OneConfigurationListMember(configuration = configuration,
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
                    }
                    if (selectScreenState.selectedConfigurationForDeletion?.id == configuration.id) {
                        Column(Modifier.weight(1.5f)) {
                            Button(modifier = Modifier.then(with(LocalDensity.current) {
                                Modifier.size(
                                    width = 9999.dp,
                                    height = sizeOfDeletionConfiguration.height.toDp(),
                                )
                            }),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                                onClick = {
                                    onDeleteConfiguration(configuration)
                                }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_delete_outline_24),
                                    contentDescription = null,
                                    tint = Color.Black,
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
        Scrollbars(state = scrollbarsState)
    }
}

@Composable
private fun AddButton(onClickAddScreenButton: () -> Unit) {
    Button(
        onClick = onClickAddScreenButton,
        contentPadding = PaddingValues(0.dp),
        enabled = true,
        shape = MaterialTheme.shapes.small,
        border = null,
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
        ),
        modifier = Modifier
            .padding(15.dp, 15.dp, 15.dp, 0.dp)
            .border(1.dp, MaterialTheme.colors.onSurface, RoundedCornerShape(6.dp))
            .fillMaxWidth()
            .heightIn(min = 55.dp)
            .testTag(TestTags.SELECT_ADD_BUTTON)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_add_24), contentDescription = null
        )
    }
}

@Composable
private fun SelectScreenTopBar(
    onBackClick: () -> Unit,
    onSelectDeleteModeClick: () -> Unit,
    isInDeleteMode: Boolean,
) {
    TopBar(onBackClick = onBackClick,
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
fun SelectScreenScreenshotPreview(selectScreenState: SelectScreenState) {
    SelectScreenScaffold(
        scaffoldState = rememberScaffoldState(),
        selectScreenState = selectScreenState,
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
        onSelectForDeletion = {}
    )
}

@Composable
fun SelectScreenScaffold(
    scaffoldState: ScaffoldState,
    selectScreenState: SelectScreenState,
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
    Scaffold(scaffoldState = scaffoldState, topBar = {
        SelectScreenTopBar(
            onBackClick = onBackClick,
            onSelectDeleteModeClick = onSelectDeleteModeClick,
            isInDeleteMode = selectScreenState.isInDeleteMode
        )
    }, content = { padding ->
        Spacer(modifier = Modifier.height(padding.calculateTopPadding()))

        Column(
            modifier = Modifier.fillMaxSize()
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