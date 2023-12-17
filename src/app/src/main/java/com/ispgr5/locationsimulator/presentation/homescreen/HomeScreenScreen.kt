package com.ispgr5.locationsimulator.presentation.homescreen

import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
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

private const val TAG = "HomeScreenScreen"

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
                .fillMaxHeight(0.3f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {
            AppName(colors)
            SelectProfileButton(viewModel, navController)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(6f), verticalArrangement = Arrangement.Center
        ) {
            FavouriteList(
                state, navController, viewModel, soundStorageManager, snackbarContent
            )
        }
        Column(
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ThemeToggle(appTheme, viewModel, activity)
            Spacer(modifier = Modifier.height(16.dp))
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
                text = configuration.name, fontSize = 18.sp
            )
            if (configuration.description.isNotBlank()) {
                Text(
                    text = configuration.description,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AppName(colors: Colors) {
    Text(
        text = stringResource(id = R.string.app_name),
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        color = colors.onBackground,
        modifier = Modifier.testTag(TestTags.HOME_APPNAME)
    )
}

@Composable
private fun BatteryOptimizationHint(
    viewModel: HomeScreenViewModel, batteryOptDisableFunction: () -> Unit
) {
    val context = LocalContext.current
    val isIgnoringOptimization = remember {
        val pm = context.getSystemService(ComponentActivity.POWER_SERVICE) as PowerManager
        return@remember when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> {
                Log.d(TAG, "SDK < M -> ignoring power optimization")
                true
            }

            else -> pm.isIgnoringBatteryOptimizations(context.packageName).also {
                Log.d(TAG, "ignoring power optimization for ${context.packageName}: $it")
            }
        }
    }
    when(isIgnoringOptimization) {
        true -> {
            Spacer(Modifier.height(20.dp))
        }
        else -> {
            Text(
                text = stringResource(id = R.string.battery_opt_recommendation),
                textAlign = TextAlign.Center
            )

            Button(onClick = {
                viewModel.onEvent(HomeScreenEvent.DisableBatteryOptimization { batteryOptDisableFunction() })
            }) {
                Text(text = stringResource(id = R.string.battery_opt_button))
            }
        }
    }

}

@Composable
fun ThemeToggle(
    appTheme: MutableState<ThemeState>, viewModel: HomeScreenViewModel, activity: MainActivity
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.homescreen_darkmode),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.onBackground
        )
        TriStateToggle(stateKeyLabelMap = ThemeType.entries.associateWith { theme -> theme.labelStringRes },
            selectedOption = appTheme.value.themeType,
            onSelectionChange = { newTheme ->
                val newAppTheme = appTheme.value.copy(themeType = newTheme)
                appTheme.value = newAppTheme
                viewModel.onEvent(
                    HomeScreenEvent.ChangedAppTheme(
                        activity = activity, themeState = newAppTheme
                    )
                )
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
            .height(60.dp)
            .width(300.dp)
            .testTag(TestTags.HOME_SELECT_CONFIG_BUTTON)
    ) {
        Text(
            text = stringResource(id = R.string.homescreen_btn_select_profile), fontSize = 30.sp
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
                            vertical = 12.dp,
                            horizontal = 16.dp,
                        ))
            }
        }
    }
}
