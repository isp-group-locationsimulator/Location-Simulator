package com.ispgr5.locationsimulator.presentation.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.RangeConverter
import com.ispgr5.locationsimulator.presentation.editTimeline.components.SecText
import com.ispgr5.locationsimulator.presentation.editTimeline.components.SliderForRange
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData.settingsScreenPreviewState
import com.ispgr5.locationsimulator.presentation.universalComponents.LocationSimulatorTopBar
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent
import com.ispgr5.locationsimulator.presentation.util.AppSnackbarHost
import com.ispgr5.locationsimulator.presentation.util.BackPressGestureDisabler
import com.ispgr5.locationsimulator.presentation.util.RenderSnackbarOnChange
import com.ispgr5.locationsimulator.presentation.util.vibratorHasAmplitudeControlAndReason

/**
 * The Settings Screen.
 * Shows the default values for vibration and sound that can be edited
 */
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalAnimationApi
@Composable
fun SettingsScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: SettingsViewModel = hiltViewModel(),
    saveDefaultValuesFunction: (state: State<SettingsState>) -> Unit,
    getDefaultValuesFunction: () -> SettingsState
) {
    //The state from viewmodel
    val state = viewModel.state.value

    // Initialising the state with saved default values
    //TODO: Is there a cleaner way?
    state.minPauseSound = getDefaultValuesFunction().minPauseSound
    state.maxPauseSound = getDefaultValuesFunction().maxPauseSound
    state.minVolumeSound = getDefaultValuesFunction().minVolumeSound
    state.maxVolumeSound = getDefaultValuesFunction().maxVolumeSound

    state.minPauseVibration = getDefaultValuesFunction().minPauseVibration
    state.maxPauseVibration = getDefaultValuesFunction().maxPauseVibration
    state.minStrengthVibration = getDefaultValuesFunction().minStrengthVibration
    state.maxStrengthVibration = getDefaultValuesFunction().maxStrengthVibration
    state.minDurationVibration = getDefaultValuesFunction().minDurationVibration
    state.maxDurationVibration = getDefaultValuesFunction().maxDurationVibration
    state.defaultNameVibration = getDefaultValuesFunction().defaultNameVibration

    val snackbarContentState = remember {
        mutableStateOf<SnackbarContent?>(null)
    }

    RenderSnackbarOnChange(snackbarHostState, snackbarContentState)

    BackPressGestureDisabler(snackbarContentState)

    SettingsScreenScaffold(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = {
            navController.popBackStack()
        },
        pagerState = rememberPagerState {
            SettingsPages.entries.size
        },
        onSaveDefaultValues = saveDefaultValuesFunction,
        onChangeEvent = {
            viewModel.onEvent(it)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsScreenScaffold(
    state: SettingsState,
    snackbarHostState: SnackbarHostState,
    pagerState: PagerState,
    onBackClick: () -> Unit,
    onSaveDefaultValues: (State<SettingsState>) -> Unit,
    onChangeEvent: (SettingsEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            LocationSimulatorTopBar(
                onBackClick = onBackClick,
                title = stringResource(id = R.string.ScreenSettings)
            )
        },
        snackbarHost = {
            AppSnackbarHost(snackbarHostState)
        },
        content = { paddingValues ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                HorizontalPager(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.9f),
                    state = pagerState,
                ) { page ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        // Content for the first page
                        when (page) {
                            0 -> {
                                VibrationCard(
                                    state,
                                    onSaveDefaultValues,
                                    onChangeEvent
                                )
                            }

                            // Content for the second page
                            1 -> {
                                SoundCard(
                                    state,
                                    onSaveDefaultValues,
                                    onChangeEvent
                                )
                            }
                        }
                    }
                }


                // Page indicator
                Row(
                    Modifier
                        .height(IntrinsicSize.Min)
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color =
                            when (pagerState.currentPage) {
                                iteration -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            }
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(20.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun ColumnScope.SoundCard(
    state: SettingsState,
    saveDefaultValuesFunction: (state: State<SettingsState>) -> Unit,
    onChangeEvent: (SettingsEvent) -> Unit

) {
    Card(
        modifier = Modifier
            .padding(20.dp)
            .weight(1f)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            /**
             * The Sound Heading
             */
            Text(
                text = stringResource(id = R.string.DefaultSound),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
            )
            /**
             * The Sound Volume
             */
            Text(text = stringResource(id = R.string.editTimeline_SoundVolume))
            Text(
                RangeConverter.transformFactorToPercentage(state.minVolumeSound)
                    .toInt().toString() + "% "
                        + stringResource(id = R.string.editTimeline_range) + RangeConverter.transformFactorToPercentage(
                    state.maxVolumeSound
                )
                    .toInt()
                    .toString() + "% "
            )
            SliderForRange(
                onValueChange = { value: ClosedFloatingPointRange<Float> ->
                    onChangeEvent(
                        SettingsEvent.ChangedSoundVolume(
                            value,
                            saveDefaultValuesFunction
                        )
                    )
                },
                value = RangeConverter.transformFactorToPercentage(state.minVolumeSound)..
                        RangeConverter.transformFactorToPercentage(state.maxVolumeSound),
                range = 0f..100f
            )

            /**
             * The Sound Pause
             */
            Text(text = stringResource(id = R.string.editTimeline_Pause))
            SecText(
                min = RangeConverter.msToS(state.minPauseSound),
                max = RangeConverter.msToS(state.maxPauseSound)
            )
            SliderForRange(
                onValueChange = { value: ClosedFloatingPointRange<Float> ->
                    onChangeEvent(
                        SettingsEvent.ChangedSoundPause(
                            value,
                            saveDefaultValuesFunction
                        ),
                    )
                },
                value = RangeConverter.msToS(state.minPauseSound)..RangeConverter.msToS(
                    state.maxPauseSound
                ),
                range = 0f..60f
            )
        }
    }
}

@Composable
private fun ColumnScope.VibrationCard(
    state: SettingsState,
    saveDefaultValuesFunction: (state: State<SettingsState>) -> Unit,
    onChangeEvent: (SettingsEvent) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .weight(1f),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            /**
             * The Vibration Heading
             */
            Text(
                text = stringResource(id = R.string.DefaultVibration),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
            )
            /**
             * The Vibration Default Name
             */
            OutlinedTextField(
                value = state.defaultNameVibration,
                onValueChange = { newText ->
                    onChangeEvent(
                        SettingsEvent.EnteredName(
                            newText,
                            saveDefaultValuesFunction
                        )
                    )
                },
                label = { Text("Default Name") },
                placeholder = { Text(text = stringResource(id = R.string.PlaceholderDefaultName)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            /**
             * The Vibration Strength
             */
            val (amplitudeControlSupported, _) = LocalContext.current.vibratorHasAmplitudeControlAndReason
            if (amplitudeControlSupported) {
                Text(text = stringResource(id = R.string.editTimeline_Vibration_Strength))
                Text(
                    RangeConverter.eightBitIntToPercentageFloat(state.minStrengthVibration)
                        .toInt().toString() + "% "
                            + stringResource(id = R.string.editTimeline_range) + RangeConverter.eightBitIntToPercentageFloat(
                        state.maxStrengthVibration
                    )
                        .toInt()
                        .toString() + "%"
                )
                SliderForRange(
                    onValueChange = { value: ClosedFloatingPointRange<Float> ->
                        onChangeEvent(
                            SettingsEvent.ChangedVibStrength(
                                value,
                                saveDefaultValuesFunction
                            )
                        )
                    },
                    value = RangeConverter.eightBitIntToPercentageFloat(state.minStrengthVibration)..RangeConverter.eightBitIntToPercentageFloat(
                        state.maxStrengthVibration
                    ),
                    range = 0f..100f
                )
            }
            /**
             * The Vibration Duration
             */
            Text(text = stringResource(id = R.string.editTimeline_Vibration_duration))
            SecText(
                min = RangeConverter.msToS(state.minDurationVibration),
                max = RangeConverter.msToS(state.maxDurationVibration)
            )
            SliderForRange(
                onValueChange = { value: ClosedFloatingPointRange<Float> ->
                    onChangeEvent(
                        SettingsEvent.ChangedVibDuration(
                            value,
                            saveDefaultValuesFunction
                        )
                    )
                },
                value = RangeConverter.msToS(state.minDurationVibration)..RangeConverter.msToS(
                    state.maxDurationVibration
                ),
                range = 0f..30f
            )

            /**
             * The Vibration Pause
             */
            Text(text = stringResource(id = R.string.editTimeline_Pause))
            SecText(
                min = RangeConverter.msToS(state.minPauseVibration),
                max = RangeConverter.msToS(state.maxPauseVibration)
            )
            SliderForRange(
                onValueChange = { value: ClosedFloatingPointRange<Float> ->
                    onChangeEvent(
                        SettingsEvent.ChangedVibPause(
                            value,
                            saveDefaultValuesFunction
                        )
                    )
                },
                value = RangeConverter.msToS(state.minPauseVibration)..RangeConverter.msToS(
                    state.maxPauseVibration
                ),
                range = 0f..60f
            )
        }
    }
}

enum class SettingsPages {
    Vibration, Sound
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsScreenScreenshotPreview(state: SettingsState, pagerState: PagerState) {
    val snackbarHostState = remember { SnackbarHostState() }
    SettingsScreenScaffold(
        state = state,
        snackbarHostState = snackbarHostState,
        pagerState = pagerState,
        onBackClick = { },
        onSaveDefaultValues = {},
        onChangeEvent = {}
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun SettingsScreenVibrationPreview() {
    SettingsScreenScreenshotPreview(
        state = settingsScreenPreviewState,
        pagerState = rememberPagerState(initialPage = SettingsPages.Vibration.ordinal) {
            SettingsPages.entries.size
        })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun SettingsScreenSoundPreview() {
    SettingsScreenScreenshotPreview(
        state = settingsScreenPreviewState,
        pagerState = rememberPagerState(initialPage = SettingsPages.Sound.ordinal) {
            SettingsPages.entries.size
        })
}
