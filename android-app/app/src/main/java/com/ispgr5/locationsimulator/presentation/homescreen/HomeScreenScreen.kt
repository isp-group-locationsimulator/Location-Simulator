package com.ispgr5.locationsimulator.presentation.homescreen

import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gigamole.composescrollbars.Scrollbars
import com.gigamole.composescrollbars.config.ScrollbarsConfig
import com.gigamole.composescrollbars.config.ScrollbarsOrientation
import com.gigamole.composescrollbars.rememberScrollbarsState
import com.gigamole.composescrollbars.scrolltype.ScrollbarsScrollType
import com.ispgr5.locationsimulator.BuildConfig
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.presentation.previewData.AppPreview
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData
import com.ispgr5.locationsimulator.presentation.universalComponents.LocationSimulatorTopBar
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent
import com.ispgr5.locationsimulator.presentation.util.AppSnackbarHost
import com.ispgr5.locationsimulator.presentation.util.RenderSnackbarOnChange
import com.ispgr5.locationsimulator.presentation.util.Screen
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType
import kotlinx.coroutines.delay

/**
 * The Home Screen.
 *
 */
@ExperimentalAnimationApi
@Composable
fun HomeScreenScreen(

    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    checkBatteryOptimizationStatus: () -> Boolean,
    batteryOptDisableFunction: () -> Unit,
    soundStorageManager: SoundStorageManager,
    activity: MainActivity,
    appTheme: MutableState<ThemeState>,
    snackbarHostState: SnackbarHostState,
    snackbarContent: MutableState<SnackbarContent?>,
) {
    viewModel.updateConfigurationWithErrorsState(soundStorageManager = soundStorageManager)
    val state = viewModel.state.value
    val context = LocalContext.current
    val selectedRole = remember { mutableStateOf("Standalone") }
    RenderSnackbarOnChange(snackbarHostState = snackbarHostState, snackbarContent = snackbarContent)

    HomeScreenScaffold(
        homeScreenState = state,
        appTheme = appTheme,
        snackbarHostState = snackbarHostState,
        selectedRole = selectedRole,
        onInfoClick = {
            navController.navigate(Screen.InfoScreen.route)
        },
        onHelpClick = {

            navController.navigate(Screen.HelpScreen.route)
        },
        onSelectProfile = {
            viewModel.onEvent(HomeScreenEvent.SelectConfiguration)
            when (selectedRole.value) {
                "Trainer" -> {
                    navController.navigate(Screen.TrainerScreen.route)
                }
                "Standalone" -> {
                    navController.navigate(Screen.SelectScreen.route)
                }
                else -> {
                    navController.navigate(Screen.SelectScreen.route)
                }
            }
        },
        onSelectFavourite = { configuration ->
            when {
                state.configurationsWithErrors.find { conf -> conf.id == configuration.id } == null -> {
                    navController.navigate(
                        Screen.DelayScreen.createRoute(
                            configuration.id!!
                        )
                    )
                }

                else -> {
                    val errorStrings = viewModel.whatIsHisErrors(
                        configuration, soundStorageManager
                    )
                    val snackbarMessage = when (errorStrings.size) {
                        1 -> context.getString(
                            R.string.error_single_sound_not_found, errorStrings.first()
                        )

                        else -> {
                            val errorText = errorStrings.joinToString(", ") {
                                "'${it}'"
                            }
                            context.getString(
                                R.string.error_multiple_sounds_not_found, errorText
                            )
                        }
                    }
                    snackbarContent.value = SnackbarContent(
                        text = snackbarMessage,
                        snackbarDuration = SnackbarDuration.Indefinite,
                        actionLabel = context.getString(android.R.string.ok),
                    )
                }

            }
        },
        onSelectTheme = { newTheme ->
            appTheme.value = newTheme
            viewModel.onEvent(
                HomeScreenEvent.ChangedAppTheme(
                    activity = activity, themeState = newTheme
                )
            )
        },
        checkBatteryOptimizationStatus = checkBatteryOptimizationStatus,
        onLaunchBatteryOptimizerDisable = {
            viewModel.onEvent(HomeScreenEvent.DisableBatteryOptimization {
                batteryOptDisableFunction()
            })


        }


    )

}

@Composable
fun HomeScreenScaffold(
    homeScreenState: HomeScreenState,
    appTheme: MutableState<ThemeState>,
    snackbarHostState: SnackbarHostState,
    selectedRole: MutableState<String>,
    onInfoClick: () -> Unit,
    onHelpClick:()->Unit,
    onSelectProfile: () -> Unit,
    onSelectFavourite: (Configuration) -> Unit,
    onSelectTheme: (ThemeState) -> Unit,
    checkBatteryOptimizationStatus: () -> Boolean,
    onLaunchBatteryOptimizerDisable: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(onInfoClick,onHelpClick)
        },
        snackbarHost = {
            AppSnackbarHost(snackbarHostState)
        },
        content = { appPadding ->
            HomeScreenContent(
                appPadding = appPadding,
                homeScreenState = homeScreenState,
                appTheme = appTheme,
                selectedRole = selectedRole,
                onSelectProfile = onSelectProfile,
                onSelectFavourite = onSelectFavourite,
                onSelectTheme = onSelectTheme,
                checkBatteryOptimizationStatus = checkBatteryOptimizationStatus,
                onLaunchBatteryOptimizerDisable = onLaunchBatteryOptimizerDisable
            )
        })
}

@Composable
fun HomeScreenContent(
    appPadding: PaddingValues,
    homeScreenState: HomeScreenState,
    appTheme: MutableState<ThemeState>,
    selectedRole: MutableState<String>,
    onSelectProfile: () -> Unit,
    onSelectFavourite: (Configuration) -> Unit,
    onSelectTheme: (ThemeState) -> Unit,
    checkBatteryOptimizationStatus: () -> Boolean,
    onLaunchBatteryOptimizerDisable: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(appPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(), verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.quick_start),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = typography.titleLarge
            )
            FavouriteList(
                homeScreenState, onSelectFavourite
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            if (homeScreenState.showInputFields) {
                NameInputField()
                RoleSelectionField(selectedRole = selectedRole)
            }
        }

        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            SelectProfileButton(onSelectProfile)
        }

        Column(
            modifier = Modifier
                .padding(top = 8.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ThemeToggle(appTheme.value, onSetTheme = onSelectTheme)
            Spacer(modifier = Modifier.height(4.dp))
            BatteryOptimizationHint(
                checkBatteryOptimizationStatus = checkBatteryOptimizationStatus,
                onLaunchBatteryOptimizerDisable = onLaunchBatteryOptimizerDisable
            )
        }
    }
}

@Composable
private fun FavouriteList(
    state: HomeScreenState,
    onSelectFavourite: (Configuration) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val lazyListState = rememberLazyListState()
        val scrollbarsState = rememberScrollbarsState(
            config = ScrollbarsConfig(orientation = ScrollbarsOrientation.Vertical),
            scrollType = ScrollbarsScrollType.Lazy.List.Static(state = lazyListState)
        )
        LazyColumn(
            Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth(),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.favoriteConfigurations.sortedBy { it.name }) { configuration ->
                FavouriteConfigurationCard(
                    configuration = configuration,
                    onSelectFavourite = onSelectFavourite
                )
            }
        }
        Scrollbars(state = scrollbarsState)
    }
}

@Composable
fun FavouriteConfigurationCard(
    configuration: Configuration,
    onSelectFavourite: (Configuration) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = colorScheme.surfaceContainerHigh,
            contentColor = colorScheme.onSurface
        ),
        shape = shapes.small,
        onClick = {
            onSelectFavourite(configuration)
        }) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                text = configuration.name, style = typography.bodyLarge
            )
            if (configuration.description.isNotBlank()) {
                Text(
                    text = configuration.description,
                    style = typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun BatteryOptimizationHint(
    checkBatteryOptimizationStatus: () -> Boolean,
    onLaunchBatteryOptimizerDisable: () -> Unit
) {
    var isIgnoringOptimization by remember {
        mutableStateOf(checkBatteryOptimizationStatus())
    }
    LaunchedEffect(Unit) {
        while (true) {
            isIgnoringOptimization = checkBatteryOptimizationStatus()
            delay(5000L)
        }
    }
    Crossfade(
        targetState = isIgnoringOptimization,
        label = "battery optimization"
    ) { crossfadedIsIgnoringOptimization ->
        when (crossfadedIsIgnoringOptimization) {
            true -> {
                Spacer(Modifier.height(20.dp))
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.battery_opt_recommendation),
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onLaunchBatteryOptimizerDisable,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.secondaryContainer,
                            contentColor = colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(text = stringResource(id = R.string.battery_opt_button))
                    }
                    Spacer(Modifier.height(8.dp))
                }

            }
        }
    }
}

@Composable
fun ThemeToggle(
    selectedTheme: ThemeState, onSetTheme: (ThemeState) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.homescreen_app_theme),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onBackground
        )
        MultiStateToggle(stateKeyLabelMap = ThemeType.entries.associateWith { theme -> theme.labelStringRes },
            selectedOption = selectedTheme.themeType,
            onSelectionChange = { newTheme ->
                onSetTheme(selectedTheme.copy(themeType = newTheme))
            })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColorSchemeToggle(
                useDynamicColors = selectedTheme.useDynamicColor,
                onSelectionChange = { useDynamicColor ->
                    onSetTheme(selectedTheme.copy(useDynamicColor = useDynamicColor))
                }
            )
        }
    }
}

@Composable
fun DynamicColorSchemeToggle(
    useDynamicColors: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.dynamic_colors),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onBackground
        )
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.normal))
            Switch(checked = useDynamicColors, onCheckedChange = onSelectionChange)
            Text(stringResource(R.string.dynamic))
        }
    }
}

@Composable
private fun SelectProfileButton(onButtonClick: () -> Unit) {
    Button(
        onClick = onButtonClick,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .testTag(TestTags.HOME_SELECT_CONFIG_BUTTON)
    ) {
        Text(
            text = stringResource(id = R.string.homescreen_btn_select_profile),
            style = typography.headlineMedium
        )
    }
}

val customRedColor = Color(0xFFFEE3D9)

@Composable
private fun NameInputField() {
    var name by remember { mutableStateOf("") }

    TextField(
        value = name,
        onValueChange = { name = it },
        label = {
            Text(
                text = stringResource(id = R.string.input_label),
                style = typography.bodyMedium,
            )
        },
        placeholder = {
            Text(
                text = stringResource(id = R.string.enter_name),
                style = typography.bodySmall
            )
        },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .border(1.dp, customRedColor, RoundedCornerShape(4.dp))
            .padding(2.dp),
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
    )
}

@Composable
private fun RoleSelectionField(selectedRole: MutableState<String>) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.Absolute.Left
        ) {
            Text(
                text = stringResource(id = R.string.select_role),
                style = typography.bodyMedium
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clickable { expanded = !expanded }
                .border(width = 1.dp, color = customRedColor, shape = RoundedCornerShape(4.dp))
                .padding(16.dp)
                .height(20.dp)
        ) {
            Text(text = selectedRole.value)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(customRedColor)
            ) {
                DropdownMenuItem(
                    text = { Text("Trainer", color = Color.Black) },
                    onClick = {
                        selectedRole.value = "Trainer"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Remote", color = Color.Black) },
                    onClick = {
                        selectedRole.value = "Remote"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Standalone", color = Color.Black) },
                    onClick = {
                        selectedRole.value = "Standalone"
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
private fun AppTopBar(onInfoClick: () -> Unit, onHelpClick: () -> Unit) {
    LocationSimulatorTopBar(
        onBackClick = null,
        title = buildAnnotatedString {
            val appName = stringResource(R.string.app_name)
            val appVersion = stringResource(R.string.app_version, BuildConfig.VERSION_NAME)
            withStyle(ParagraphStyle(textAlign = TextAlign.Center, lineHeight = 20.sp)) {
                withStyle(SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)) {
                    appendLine(appName)
                }
                withStyle(SpanStyle(fontStyle = FontStyle.Italic, fontSize = 14.sp)) {
                    append(appVersion)
                }
            }
        },
        backPossible = false
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.Left
        )
        {
            IconButton(onClick = onInfoClick, modifier = Modifier.padding(5.dp),) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_info_24),
                    contentDescription = stringResource(
                        id = R.string.about
                    )
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onHelpClick, modifier = Modifier.padding(5.dp) ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_help_24),
                    contentDescription = stringResource(R.string.help),
                )
            }
        }
    }
}




@Composable
fun <K> MultiStateToggle(
    stateKeyLabelMap: Map<K, Int>, selectedOption: K, onSelectionChange: (K) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp,
        modifier = Modifier.wrapContentSize(),
        color = colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(24.dp))
                .background(colorScheme.surfaceContainer)
        ) {
            stateKeyLabelMap.entries.forEach { (key, labelStringRes) ->
                Text(text = stringResource(id = labelStringRes),
                    color = when (key == selectedOption) {
                        true -> colorScheme.onPrimary
                        else -> colorScheme.onSurface
                    },
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(24.dp))
                        .clickable {
                            onSelectionChange(key)
                        }
                        .background(
                            when (key) {
                                selectedOption -> {
                                    colorScheme.primary
                                }

                                else -> {
                                    colorScheme.surfaceContainer
                                }
                            }
                        )
                        .padding(
                            vertical = 8.dp,
                            horizontal = 16.dp,
                        ))
            }
        }
    }
}


@Composable
@AppPreview
fun HomeScreenPreview() {
    val state by remember {
        mutableStateOf(
            HomeScreenState(
                favoriteConfigurations = PreviewData.previewConfigurations.filter { it.isFavorite },
                configurationsWithErrors = emptyList()
            )
        )
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val themeState = remember {
        mutableStateOf(PreviewData.themePreviewState)
    }

    val selectedRole = remember { mutableStateOf("Standalone") }

        LocationSimulatorTheme {
        HomeScreenScaffold(
            homeScreenState = state,
            appTheme = themeState,
            snackbarHostState = snackbarHostState,
            selectedRole = selectedRole,
            onInfoClick = {},
            onHelpClick = {},
            onSelectProfile = {},
            onSelectFavourite = {},
            onSelectTheme = {},
            checkBatteryOptimizationStatus = { false },
            onLaunchBatteryOptimizerDisable = {},
        )
    }
    NameInputField()
    RoleSelectionField(selectedRole = selectedRole)


}
