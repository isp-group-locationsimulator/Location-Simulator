package com.ispgr5.locationsimulator.presentation.run

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType
import kotlinx.coroutines.delay
import org.joda.time.Instant

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

    val effectState = SimulationService.EffectTimelineBus.observeAsState()
    val playingEffect by remember {
        derivedStateOf {
            effectState.value?.playingEffect
        }
    }
    val nextEffect by remember {
        derivedStateOf {
            effectState.value?.nextEffect
        }
    }
    val currentPauseDuration by remember {
        derivedStateOf {
            effectState.value?.currentPauseDuration
        }
    }
    val startPauseAt by remember {
        derivedStateOf {
            effectState.value?.startPauseAt
        }
    }

    Scaffold(scaffoldState = scaffoldState,
        topBar = { TopBar(navController, stringResource(id = R.string.ScreenRun), false) },
        content = { padding ->
            RunScreenContent(
                paddingValues = padding,
                configuration = viewModel.state.value.configuration,
                playingEffect = playingEffect,
                nextEffect = nextEffect,
                startPauseAt = startPauseAt,
                currentPauseDuration = currentPauseDuration
            ) {
                SimulationService.IsPlayingEventBus.postValue(false)
                viewModel.onEvent(RunEvent.StopClicked(stopServiceFunction))
                navController.popBackStack()
            }
        })
}

@Composable
fun RunScreenContent(
    paddingValues: PaddingValues,
    configuration: Configuration?,
    playingEffect: EffectParameters?,
    nextEffect: EffectParameters?,
    startPauseAt: Instant?,
    currentPauseDuration: Long?,
    onStop: () -> Unit
) {
    Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding()))
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(0.8f)
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .border(1.dp, Color.Red),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.running_configuration),
                style = MaterialTheme.typography.h5
            )
            Text(
                text = configuration?.name ?: stringResource(R.string.unknown_configuration),
                style = MaterialTheme.typography.h5.copy(fontFamily = FontFamily.Monospace)
            )
            nextEffect?.let { next ->
                PlayingStateUi(
                    playingEffect = playingEffect,
                    nextEffect = next,
                    startPauseAt = startPauseAt,
                    currentPauseDuration = currentPauseDuration
                )
            }
        }
        Column(
            Modifier
                .weight(0.15f)
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(1.dp, Color.Green),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .aspectRatio(2f)
                    .padding(vertical = 8.dp)
                    .testTag(TestTags.RUN_END_BUTTON)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                onStop()
                            },)
                    },

                ) {
                Text(text = stringResource(id = R.string.run_stop), fontSize = 30.sp)
            }
        }
    }
}

private object TestData {
    val baselineInstant: Instant = Instant.ofEpochMilli(1702738800000L)
    val vibrationComponent = ConfigComponent.Vibration(
        id = 0,
        name = "vib0",
        minStrength = 20,
        maxStrength = 50,
        minPause = 200,
        maxPause = 500,
        minDuration = 200,
        maxDuration = 500
    )
    val soundComponent = ConfigComponent.Sound(
        id = 1,
        name = "sound",
        source = "Source.mp3",
        minVolume = 0.1f,
        maxVolume = 0.5f,
        minPause = 200,
        maxPause = 500,
    )
    val lastRefresh: Instant = baselineInstant.plus(300L)

    val pausedEffectState = EffectTimeline(
        lastRefresh,
        playingEffect = null,
        currentPauseDuration = 500L,
        startPauseAt = baselineInstant,
        nextEffect = EffectParameters.Sound(
            startAt = baselineInstant.plus(500),
            durationMillis = 500L,
            pauseMillis = 500L,
            volume = 0.42f,
            soundName = soundComponent.source,
            original = soundComponent
        )
    )

    val playingEffectState = EffectTimeline(
        lastRefresh, playingEffect = EffectParameters.Vibration(
            startAt = baselineInstant.minus(100L),
            durationMillis = 500L,
            pauseMillis = 500L,
            strength = 40,
            original = vibrationComponent
        ), currentPauseDuration = null, nextEffect = EffectParameters.Sound(
            startAt = baselineInstant.plus(900L),
            durationMillis = 500L,
            pauseMillis = 500L,
            volume = 0.4f,
            soundName = soundComponent.source,
            original = soundComponent
        ), startPauseAt = null
    )
}

@Composable
@Preview("progress-playing")
fun ProgressBarPreview() {
    val progress = calculateProgress(
        playingEffect = TestData.playingEffectState.playingEffect,
        nextEffect = TestData.playingEffectState.nextEffect,
        startPauseAt = null,
        now = TestData.lastRefresh
    )
    EffectProgressBar(progressBarProgress = progress, isInPause = false)
}

@Composable
@Preview("progress-paused")
fun ProgressBarPausedPreview() {
    val progress = calculateProgress(
        playingEffect = null,
        nextEffect = TestData.pausedEffectState.nextEffect,
        startPauseAt = TestData.pausedEffectState.startPauseAt,
        now = TestData.lastRefresh
    )
    EffectProgressBar(progressBarProgress = progress, isInPause = true)
}


@Composable
@Preview("runScreenContent")
fun RunScreenPreview() {
    LocationSimulatorTheme(ThemeState(themeType = ThemeType.LIGHT)) {

        RunScreenContent(
            paddingValues = PaddingValues(4.dp),
            configuration = Configuration(
                name = "Test configuration",
                description = "",
                randomOrderPlayback = false,
                components = listOf(TestData.vibrationComponent, TestData.soundComponent)
            ),
            TestData.playingEffectState.playingEffect,
            TestData.playingEffectState.nextEffect,
            TestData.playingEffectState.startPauseAt,
            TestData.playingEffectState.currentPauseDuration
        ) {}
    }
}

@Composable
@Preview("playing")
fun PlayingStateUiPreview() {
    LocationSimulatorTheme(themeState = ThemeState(ThemeType.LIGHT)) {
        PlayingStateUi(
            playingEffect = TestData.playingEffectState.playingEffect,
            nextEffect = TestData.playingEffectState.nextEffect,
            startPauseAt = null,
            currentPauseDuration = null
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun PlayingStateUi(
    playingEffect: EffectParameters?,
    nextEffect: EffectParameters,
    startPauseAt: Instant?,
    currentPauseDuration: Long?
) {
    var lastRefreshInstant by remember {
        mutableStateOf(Instant.now())
    }

    LaunchedEffect(SimulationService.IsPlayingEventBus.value) {
        while (true) {
            delay(49)
            lastRefreshInstant = Instant.now()
        }
    }


    val progressBarProgress = calculateProgress(
        playingEffect, nextEffect, startPauseAt, lastRefreshInstant
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
            .border(1.dp, Color.Magenta),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (playingEffect) {
                null -> PausedUi(currentPauseDuration = currentPauseDuration!!)
                is EffectParameters.Vibration -> VibrationUi(playingEffect)
                is EffectParameters.Sound -> SoundUi(playingEffect)
            }
        }
        Column(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxWidth()
        ) {
            NextUi(nextEffect = nextEffect)
        }

        Column(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()
        ) {
            EffectProgressBar(progressBarProgress, isInPause = currentPauseDuration != null)
        }
    }
}

fun calculateProgress(
    playingEffect: EffectParameters?,
    nextEffect: EffectParameters,
    startPauseAt: Instant?,
    now: Instant
): Float {
    val startInstant = when (playingEffect) {
        null -> startPauseAt!!
        else -> playingEffect.startAt
    }
    val endInstant = when (playingEffect) {
        null -> nextEffect.startAt
        else -> playingEffect.endEffectAt
    }
    val denominatorDuration = endInstant.millis.minus(startInstant.millis)
    val numeratorElapsed = now.millis.minus(startInstant.millis)
    return numeratorElapsed.toFloat().div(denominatorDuration)
}

@Composable
fun EffectProgressBar(
    progressBarProgress: Float,
    isInPause: Boolean
) {
    Row(
        modifier = Modifier.wrapContentHeight(Alignment.Bottom),
        horizontalArrangement = Arrangement.Center
    ) {
        LinearProgressIndicator(
            progress = progressBarProgress,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .padding(8.dp),
            color = when (isInPause) {
                true -> MaterialTheme.colors.secondary.copy(alpha = 0.4f)
                else -> MaterialTheme.colors.primary
            }
        )
    }
}

@Composable
fun SoundUi(effectState: EffectParameters.Sound) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.audionouse2), contentDescription = null
        )
        Text(text = effectState.soundName)
    }

    Text("#$effectState.instanceId}")
    Text(effectState.toString())
}

@Composable
fun VibrationUi(effectState: EffectParameters) {
    Icon(
        modifier = Modifier.scale(2f),
        painter = painterResource(id = R.drawable.ic_baseline_vibration_24),
        contentDescription = null
    )
    Text(buildString {
        append("#${effectState.instanceId}")
        append(" - ")
        append(effectState.toString())
    })
}

@Composable
fun PausedUi(currentPauseDuration: Long) {
    Icon(imageVector = Icons.Default.PauseCircleOutline, contentDescription = null)
    Text(stringResource(id = R.string.in_pause))
    Text(
        "$currentPauseDuration ms",
        style = MaterialTheme.typography.h6.copy(fontFamily = FontFamily.Monospace)
    )
}

@Composable
fun NextUi(nextEffect: EffectParameters) {
    Text("Next: #${nextEffect.instanceId} - $nextEffect")
}
