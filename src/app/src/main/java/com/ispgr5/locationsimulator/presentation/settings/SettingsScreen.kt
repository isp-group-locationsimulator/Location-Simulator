package com.ispgr5.locationsimulator.presentation.settings

import android.content.Context
import android.os.Build
import android.os.Vibrator
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.RangeConverter
import com.ispgr5.locationsimulator.presentation.editTimeline.components.SecText
import com.ispgr5.locationsimulator.presentation.editTimeline.components.SliderForRange
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar

/**
 * The Settings Screen.
 * Shows the default values for vibration and sound that can be edited
 */
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalAnimationApi
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState,
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

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(navController, stringResource(id = R.string.ScreenSettings)) },
        content = {
            Spacer(modifier = Modifier.height(it.calculateTopPadding()))

            BoxWithConstraints(
                Modifier
                    .fillMaxWidth(),
            ) {
                // TODO: Verify if this is the perfect value for small screens
                // Hint: It is used to limit the card to 90% screen size on small screens,
                // so that the page indicator remains visible and doesn't overlap
                val isSmallScreen = maxHeight < 600.dp

                // The Pager
                val pageCount = 2
                val pagerState = rememberPagerState {
                    pageCount
                }

                HorizontalPager(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = pagerState,
                ) { page ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        // Content for the first page
                        if (page == 0) {
                            // Card for Vibration
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .border(
                                        1.dp,
                                        MaterialTheme.colors.onSurface,
                                        RoundedCornerShape(5)
                                    )
                                    .let { it ->
                                        if (isSmallScreen) it.fillMaxHeight(0.9f) else it
                                    }
                                    .padding(20.dp)
                                    .verticalScroll(rememberScrollState()),

                                ) {
                                /**
                                 * The Vibration Heading
                                 */
                                Text(
                                    text = stringResource(id = R.string.DefaultVibration),
                                    style = MaterialTheme.typography.h5,
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
                                        viewModel.onEvent(
                                            SettingsEvent.EnteredName(
                                                newText,
                                                saveDefaultValuesFunction
                                            )
                                        )
                                    },
                                    label = { Text("Default Name") },
                                    placeholder = { Text(text = stringResource(id = R.string.PlaceholderDefaultName)) },
                                    singleLine = true,
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = MaterialTheme.colors.primary,
                                        unfocusedBorderColor = MaterialTheme.colors.onSurface
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                                /**
                                 * The Vibration Strength
                                 */
                                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    // Use the recommended method to get the Vibrator service
                                    LocalContext.current.getSystemService(Vibrator::class.java)
                                } else {
                                    // Use the deprecated method to get the Vibrator service
                                    @Suppress("DEPRECATION")
                                    LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                }
                                if (Build.VERSION.SDK_INT >= 26 && vibrator.hasAmplitudeControl()) {
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
                                        value = RangeConverter.eightBitIntToPercentageFloat(state.minStrengthVibration)..RangeConverter.eightBitIntToPercentageFloat(
                                            state.maxStrengthVibration
                                        ),
                                        func = { value: ClosedFloatingPointRange<Float> ->
                                            viewModel.onEvent(
                                                SettingsEvent.ChangedVibStrength(
                                                    value,
                                                    saveDefaultValuesFunction
                                                )
                                            )
                                        },
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
                                    value = RangeConverter.msToS(state.minDurationVibration)..RangeConverter.msToS(
                                        state.maxDurationVibration
                                    ),
                                    func = { value: ClosedFloatingPointRange<Float> ->
                                        viewModel.onEvent(
                                            SettingsEvent.ChangedVibDuration(
                                                value,
                                                saveDefaultValuesFunction
                                            )
                                        )
                                    },
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
                                    value = RangeConverter.msToS(state.minPauseVibration)..RangeConverter.msToS(
                                        state.maxPauseVibration
                                    ),
                                    func = { value: ClosedFloatingPointRange<Float> ->
                                        viewModel.onEvent(
                                            SettingsEvent.ChangedVibPause(
                                                value,
                                                saveDefaultValuesFunction
                                            )
                                        )
                                    },
                                    range = 0f..60f
                                )

                            }
                        }

                        // Content for the second page
                        else if (page == 1) {
                            // Card for Sound
                            Column(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .border(
                                        1.dp,
                                        MaterialTheme.colors.onSurface,
                                        RoundedCornerShape(5)
                                    )
                                    .let { it ->
                                        if (isSmallScreen) it.fillMaxHeight(0.9f) else it
                                    }
                                    .padding(20.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                /**
                                 * The Sound Heading
                                 */
                                Text(
                                    text = stringResource(id = R.string.DefaultSound),
                                    style = MaterialTheme.typography.h5,
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
                                    value = RangeConverter.transformFactorToPercentage(state.minVolumeSound)..
                                            RangeConverter.transformFactorToPercentage(state.maxVolumeSound),
                                    func = { value: ClosedFloatingPointRange<Float> ->
                                        viewModel.onEvent(
                                            SettingsEvent.ChangedSoundVolume(
                                                value,
                                                saveDefaultValuesFunction
                                            )
                                        )
                                    },
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
                                    value = RangeConverter.msToS(state.minPauseSound)..RangeConverter.msToS(
                                        state.maxPauseSound
                                    ),
                                    func = { value: ClosedFloatingPointRange<Float> ->
                                        viewModel.onEvent(
                                            SettingsEvent.ChangedSoundPause(
                                                value,
                                                saveDefaultValuesFunction
                                            ),
                                        )
                                    },
                                    range = 0f..60f
                                )
                            }

                        }
                    }
                }


                // Page indicator
                Row(
                    Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pageCount) { iteration ->
                        val color =
                            if (pagerState.currentPage == iteration) MaterialTheme.colors.primary else MaterialTheme.colors.surface
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
