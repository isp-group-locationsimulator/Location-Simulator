package com.ispgr5.locationsimulator.presentation.homescreen

import android.os.Build
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.presentation.select.components.OneConfigurationListMember
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar
import com.ispgr5.locationsimulator.presentation.util.MakeSnackbar
import com.ispgr5.locationsimulator.presentation.util.Screen
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType

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
    val notFound: String = stringResource(id = R.string.not_found)
    val ok = stringResource(id = android.R.string.ok)

    MakeSnackbar(scaffoldState = scaffoldState, snackbarContent = snackbarContent)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(navController, "", false,
                extraActions = {
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
        },
        content = {
            Spacer(modifier = Modifier.height(it.calculateTopPadding()))
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                /**
                 * Header
                 */
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground,
                        modifier = Modifier.testTag(TestTags.HOME_APPNAME)
                    )
                }

                /**
                 * Select Button
                 */
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
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
                            text = stringResource(id = R.string.homescreen_btn_select_profile),
                            fontSize = 30.sp
                        )
                    }
                }

                /**
                 * The Favorite Configurations
                 */
                if (state.favoriteConfigurations.isNotEmpty()) {
                    Column(
                        modifier = Modifier.weight(2f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        LazyColumn(
                            Modifier
                                .padding(25.dp)
                                .fillMaxWidth()
                        ) {
                            items(state.favoriteConfigurations) { configuration ->
                                OneConfigurationListMember(
                                    configuration = configuration,
                                    onToggleClicked = {
                                        if (state.configurationsWithErrors.find { conf -> conf.id == configuration.id } == null) {
                                            navController.navigate(
                                                Screen.DelayScreen.createRoute(
                                                    configuration.id!!
                                                )
                                            )
                                        } else {
                                            for (error in viewModel.whatIsHisErrors(
                                                configuration = configuration,
                                                soundStorageManager = soundStorageManager
                                            )) {
                                                snackbarContent.value = SnackbarContent(
                                                    text = "$error $notFound",
                                                    snackbarDuration = SnackbarDuration.Indefinite,
                                                    actionLabel = ok,
                                                )

                                            }
                                        }
                                    },
                                    isToggled = false,
                                    onEditClicked = {},
                                    onSelectClicked = {},
                                    onExportClicked = {},
                                    onDuplicateClicked = {},
                                    hasErrors = state.configurationsWithErrors.find { conf -> conf.id == configuration.id } != null,
                                    onErrorInfoClicked = {
                                        for (error in viewModel.whatIsHisErrors(
                                            configuration = configuration,
                                            soundStorageManager = soundStorageManager
                                        )) {
                                            "$error $notFound" to SnackbarDuration.Indefinite
                                        }
                                    },
                                    isFavorite = configuration.isFavorite,
                                    onFavoriteClicked = {}
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }


                /**
                 * Switch for Darkmode
                 */
                Column(
                    modifier = Modifier.weight(0.5f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(1.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(id = R.string.homescreen_darkmode),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground
                        )
                        TriStateToggle(
                            stateKeyLabelMap = ThemeType.entries.associateWith { theme -> theme.labelStringRes },
                            selectedOption = appTheme.value.themeType,
                            onSelectionChange = { newTheme ->
                                val newAppTheme = appTheme.value.copy(themeType = newTheme)
                                appTheme.value = newAppTheme
                                viewModel.onEvent(
                                    HomeScreenEvent.ChangedAppTheme(
                                        activity = activity,
                                        themeState = newAppTheme
                                    )
                                )
                            }
                        )
                    }
                }


                /**
                 * battery optimization hint
                 */
                val pm =
                    LocalContext.current.getSystemService(ComponentActivity.POWER_SERVICE) as PowerManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !pm.isIgnoringBatteryOptimizations(
                        LocalContext.current.packageName
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1.5f),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.battery_opt_recommendation),
                            textAlign = TextAlign.Center
                        )
                        //var forceUpdate:Boolean by remember { mutableStateOf(true) }
                        Button(onClick = {
                            viewModel.onEvent(HomeScreenEvent.DisableBatteryOptimization { batteryOptDisableFunction() })
                        }) {
                            Text(text = stringResource(id = R.string.battery_opt_button))
                        }
                    }
                }
            }
        })
}

@Composable
fun <K> TriStateToggle(
    stateKeyLabelMap: Map<K, Int>,
    selectedOption: K,
    onSelectionChange: (K) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        elevation = 4.dp,
        modifier = Modifier.wrapContentSize(),
        color = colors.surface
    ) {
        Row(
            modifier = Modifier.clip(shape = RoundedCornerShape(24.dp)).background(colors.surface)
        ) {
            stateKeyLabelMap.entries.forEach { (key, labelStringRes) ->
                Text(
                    text = stringResource(id = labelStringRes),
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
                        )
                )
            }
        }
    }
}
