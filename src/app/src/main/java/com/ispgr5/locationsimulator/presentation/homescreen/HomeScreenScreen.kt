package com.ispgr5.locationsimulator.presentation.homescreen

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar
import com.ispgr5.locationsimulator.presentation.util.MakeSnackbar
import com.ispgr5.locationsimulator.presentation.util.Screen
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
    batteryOptDisableFunction: () -> Unit,
    soundStorageManager: SoundStorageManager,
    activity: MainActivity,
    appTheme: MutableState<ThemeState>,
    scaffoldState: ScaffoldState,
    snackbarContent: MutableState<SnackbarContent?>,
) {
    viewModel.updateConfigurationWithErrorsState(soundStorageManager = soundStorageManager)
    val state = viewModel.state.value

    MakeSnackbar(scaffoldState = scaffoldState, snackbarContent = snackbarContent)

    Scaffold(scaffoldState = scaffoldState, topBar = {
        AppTopBar(navController)
    }, content = { appPadding ->
        HomeScreenContent(
            appPadding,
            viewModel,
            navController,
            state,
            soundStorageManager,
            snackbarContent,
            appTheme,
            activity,
            batteryOptDisableFunction
        )
    })
}

@Composable
fun HomeScreenContent(
    appPadding: PaddingValues,
    viewModel: HomeScreenViewModel,
    navController: NavController,
    state: HomeScreenState,
    soundStorageManager: SoundStorageManager,
    snackbarContent: MutableState<SnackbarContent?>,
    appTheme: MutableState<ThemeState>,
    activity: MainActivity,
    batteryOptDisableFunction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(appPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        /**
         * Header
         */
        Column(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {
            AppName()
        }

        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SelectProfileButton(viewModel, navController)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            FavouriteList(
                state, navController, viewModel, soundStorageManager, snackbarContent
            )
        }
        Column(
            modifier = Modifier
                .padding(top = 8.dp)
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ThemeToggle(appTheme.value) { newTheme ->
                appTheme.value = newTheme
                viewModel.onEvent(
                    HomeScreenEvent.ChangedAppTheme(
                        activity = activity, themeState = newTheme
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            BatteryOptimizationHint(viewModel, batteryOptDisableFunction)
        }
    }
}

@Composable
private fun FavouriteList(
    state: HomeScreenState,
    navController: NavController,
    viewModel: HomeScreenViewModel,
    soundStorageManager: SoundStorageManager,
    snackbarContent: MutableState<SnackbarContent?>,
) {
    val ok = stringResource(id = android.R.string.ok)
    val soundNotFoundSingle = stringResource(id = R.string.error_single_sound_not_found)
    val soundNotFoundMultiple = stringResource(id = R.string.error_multiple_sounds_not_found)
    Box(
        modifier = Modifier
            .fillMaxSize()
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
                    state = state,
                    configuration = configuration,
                    navController = navController,
                    viewModel = viewModel,
                    soundStorageManager = soundStorageManager,
                    soundNotFoundSingle = soundNotFoundSingle,
                    soundNotFoundMultiple = soundNotFoundMultiple,
                    ok = ok,
                    snackbarContent = snackbarContent,
                )
            }
        }
        Scrollbars(state = scrollbarsState)
    }
}

@Composable
fun FavouriteConfigurationCard(
    state: HomeScreenState,
    configuration: Configuration,
    navController: NavController,
    viewModel: HomeScreenViewModel,
    soundStorageManager: SoundStorageManager,
    soundNotFoundSingle: String,
    soundNotFoundMultiple: String,
    ok: String,
    snackbarContent: MutableState<SnackbarContent?>,
) {
    Button(modifier = Modifier
        .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colors.surface,
        ),
        border = null,
        elevation = null,
        shape = shapes.small,
        onClick = {
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
                        1 -> soundNotFoundSingle.format(errorStrings.first())
                        else -> soundNotFoundMultiple.format(errorStrings.joinToString(
                            ", "
                        ) { "'${it}'" })
                    }
                    snackbarContent.value = SnackbarContent(
                        text = snackbarMessage,
                        snackbarDuration = SnackbarDuration.Indefinite,
                        actionLabel = ok,
                    )
                }

            }
        }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = configuration.name, style = typography.body1
            )
            if (configuration.description.isNotBlank()) {
                Text(
                    text = configuration.description,
                    style = typography.subtitle2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AppName() {
    Text(
        text = stringResource(id = R.string.app_name),
        style = typography.h3,
        color = colors.onBackground,
        modifier = Modifier.testTag(TestTags.HOME_APPNAME)
    )
}

private fun appIsIgnoringPowerOptimization(context: Context, powerManager: PowerManager): Boolean {
    return when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> true
        else -> powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }
}

@Composable
private fun BatteryOptimizationHint(
    viewModel: HomeScreenViewModel, batteryOptDisableFunction: () -> Unit
) {
    val context = LocalContext.current
    val powerManager = remember {
        context.getSystemService(ComponentActivity.POWER_SERVICE) as PowerManager
    }
    var isIgnoringOptimization by remember {
        mutableStateOf(appIsIgnoringPowerOptimization(context, powerManager))
    }
    LaunchedEffect(viewModel) {
        while (true) {
            isIgnoringOptimization = appIsIgnoringPowerOptimization(context, powerManager)
            delay(100)
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
                    Button(onClick = {
                        viewModel.onEvent(HomeScreenEvent.DisableBatteryOptimization {
                            batteryOptDisableFunction()
                            isIgnoringOptimization =
                                appIsIgnoringPowerOptimization(context, powerManager)
                        })
                    }) {
                        Text(text = stringResource(id = R.string.battery_opt_button))
                    }
                }

            }
        }
    }


}

@Composable
fun ThemeToggle(
    selectedTheme: ThemeState,
    onSetTheme: (ThemeState) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.homescreen_app_theme),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.onBackground
        )
        TriStateToggle(stateKeyLabelMap = ThemeType.entries.associateWith { theme -> theme.labelStringRes },
            selectedOption = selectedTheme.themeType,
            onSelectionChange = { newTheme ->
                onSetTheme(ThemeState(newTheme))
            })
    }
}

@Composable
private fun SelectProfileButton(
    viewModel: HomeScreenViewModel, navController: NavController
) {
    Button(
        onClick = {
            viewModel.onEvent(HomeScreenEvent.SelectConfiguration)
            navController.navigate(Screen.SelectScreen.route)
        },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .testTag(TestTags.HOME_SELECT_CONFIG_BUTTON)
    ) {
        Text(
            text = stringResource(id = R.string.homescreen_btn_select_profile),
            style = typography.h4
        )
    }
}

@Composable
private fun AppTopBar(navController: NavController) {
    TopBar(navController, stringResource(R.string.app_name), false, extraActions = {
        IconButton(onClick = {
            navController.navigate(Screen.InfoScreen.route)
        }, modifier = Modifier.padding(5.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_info_24),
                contentDescription = ""
            )
        }
    }

    )
}

@Composable
fun <K> TriStateToggle(
    stateKeyLabelMap: Map<K, Int>, selectedOption: K, onSelectionChange: (K) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        elevation = 4.dp,
        modifier = Modifier.wrapContentSize(),
        color = colors.surface
    ) {
        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(24.dp))
                .background(colors.surface)
        ) {
            stateKeyLabelMap.entries.forEach { (key, labelStringRes) ->
                Text(text = stringResource(id = labelStringRes),
                    color = when (key == selectedOption) {
                        true -> colors.onPrimary
                        else -> colors.onSurface
                    },
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(24.dp))
                        .clickable {
                            onSelectionChange(key)
                        }
                        .background(
                            if (key == selectedOption) {
                                colors.primary
                            } else {
                                colors.surface
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
