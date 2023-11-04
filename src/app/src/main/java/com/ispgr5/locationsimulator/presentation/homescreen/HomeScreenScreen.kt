package com.ispgr5.locationsimulator.presentation.homescreen

import android.os.Build
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    darkTheme: MutableState<ThemeState>,
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
                        text = stringResource(id = R.string.homescreen_appname),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onBackground,
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
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(1.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.homescreen_darkmode),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colors.onBackground
                        )
                        Switch(
                            checked = darkTheme.value.isDarkTheme,
                            onCheckedChange = { isChecked ->
                                viewModel.onEvent(
                                    HomeScreenEvent.ChangedAppTheme(
                                        isChecked,
                                        activity,
                                        darkTheme
                                    )
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colors.primary,
                                uncheckedThumbColor = MaterialTheme.colors.primary,
                                checkedTrackColor = MaterialTheme.colors.secondary,
                                uncheckedTrackColor = MaterialTheme.colors.secondary,
                            ),
                            modifier = Modifier.testTag(TestTags.HOME_DARKMODE_SLIDER)
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