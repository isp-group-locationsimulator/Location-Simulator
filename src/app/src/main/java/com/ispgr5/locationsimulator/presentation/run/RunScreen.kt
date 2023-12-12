package com.ispgr5.locationsimulator.presentation.run

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar
import kotlinx.coroutines.delay
import org.joda.time.Instant

private const val TAG = "RunScreen"

/**
 * The Run Screen.
 * This screen is shown while the simulation is running
 */
@ExperimentalAnimationApi
@Composable
fun RunScreen(
    navController: NavController,
    stopServiceFunction: () -> Unit,
    viewModel: RunViewModel = hiltViewModel(),
    scaffoldState: ScaffoldState
) {
    BackHandler {
        viewModel.onEvent(RunEvent.StopClicked(stopServiceFunction))
        navController.popBackStack()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(navController, stringResource(id = R.string.ScreenRun), false) },
        content = {
            Spacer(modifier = Modifier.height(it.calculateTopPadding()))

            val effectState by SimulationService.EffectTimelineBus.observeAsState()
            LaunchedEffect(effectState) {
                Log.i(TAG, "RunScreen: $effectState")
                Log.i(TAG, "in pause: ${effectState?.playingEffect == null}")
            }

            /**
             * The stop button
             */
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.running_configuration),
                        style = MaterialTheme.typography.h5
                    )
                    Text(
                        text = viewModel.state.value.configuration?.name
                            ?: stringResource(R.string.unknown_configuration),
                        style = MaterialTheme.typography.h5.copy(fontFamily = FontFamily.Monospace)
                    )
                    effectState?.let { state -> PlayingStateUi(state) }
                }
                Button(
                    modifier = Modifier
                        .width(200.dp)
                        .height(120.dp)
                        .testTag(TestTags.RUN_END_BUTTON),
                    onClick = {
                        SimulationService.IsPlayingEventBus.postValue(false)
                        viewModel.onEvent(RunEvent.StopClicked(stopServiceFunction))
                        navController.popBackStack()
                    }) {
                    Text(text = stringResource(id = R.string.run_stop), fontSize = 30.sp)
                }
            }
        })
}

@Composable
fun PlayingStateUi(effectState: EffectTimeline) {
    val lastRefreshInstant = remember {
        mutableStateOf(Instant.now())
    }
    LaunchedEffect(effectState) {
        while (true) {
            lastRefreshInstant.value = Instant.now()
            delay(10)
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (effectState.playingEffect) {
            null -> PausedUi(effectState = effectState)
            is EffectParameters.Vibration -> VibrationUi(effectState)
            is EffectParameters.Sound -> SoundUi(effectState)
        }
        NextUi(nextEffect = effectState.nextEffect)
        EffectProgressBar(effectState, lastRefreshInstant.value)
    }
}

@Composable
fun EffectProgressBar(effectState: EffectTimeline, lastRefreshInstant: Instant) {
    Log.i(TAG, effectState.toString())
    Row(modifier = Modifier.wrapContentHeight(Alignment.Bottom)) {
        val (comparisonInstant, comparisonSource) = when (effectState.playingEffect) {
            null -> effectState.nextEffect.startAt to "pause"
            else -> effectState.playingEffect.endEffectAt to "playing"
        }
        Log.d(TAG, "$lastRefreshInstant - comparison $comparisonInstant $comparisonSource")
        val (denominatorDuration, denominatorSource) = remember {
            when (effectState.playingEffect) {
                null -> effectState.currentPauseDuration!! to "pause"
                else -> effectState.playingEffect.durationMillis to "playing"
            }
        }
        Log.d(TAG, "$lastRefreshInstant - denominator $denominatorDuration $denominatorSource")
        val numeratorDifference = remember {
            comparisonInstant.millis.minus(lastRefreshInstant.millis)
        }
        Log.d(TAG, "$lastRefreshInstant - numerator $numeratorDifference")
        val percentage = remember {
            numeratorDifference.toFloat().div(denominatorDuration)
        }

        Log.d(
            TAG,
            "$lastRefreshInstant - percentage $percentage ($numeratorDifference / $denominatorDuration)"
        )

        LinearProgressIndicator(
            progress = percentage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            color = MaterialTheme.colors.primary
        )
    }
}

@Composable
fun SoundUi(effectState: EffectTimeline) {
    Icon(
        painter = painterResource(id = R.drawable.audionouse2),
        contentDescription = null
    )
    Text("#${effectState.playingEffect!!.instanceId}")
    Text(effectState.playingEffect.toString())
}

@Composable
fun VibrationUi(effectState: EffectTimeline) {
    Icon(
        painter = painterResource(id = R.drawable.ic_baseline_vibration_24),
        contentDescription = null
    )
    Text(buildString {
        append("#${effectState.playingEffect!!.instanceId}")
        append(" - ")
        append(effectState.playingEffect.toString())
    })
}

@Composable
fun PausedUi(effectState: EffectTimeline) {
    Icon(imageVector = Icons.Default.PauseCircleOutline, contentDescription = null)
    Text(stringResource(id = R.string.in_pause))
    Text(effectState.nextEffect.instanceId.toString())
    Text(effectState.nextEffect.startAt.millis.toString())
}

@Composable
fun NextUi(nextEffect: EffectParameters) {
    Text("Next: #${nextEffect.instanceId} - $nextEffect")
}
