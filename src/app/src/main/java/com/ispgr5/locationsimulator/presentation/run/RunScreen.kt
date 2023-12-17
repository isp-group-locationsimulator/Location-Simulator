package com.ispgr5.locationsimulator.presentation.run

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar
import com.ispgr5.locationsimulator.presentation.util.MakeSnackbar
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.joda.time.Instant
import java.math.BigDecimal

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

    val snackbarContentState = remember {
        mutableStateOf<SnackbarContent?>(null)
    }

    MakeSnackbar(scaffoldState = scaffoldState, snackbarContent = snackbarContentState)

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
                currentPauseDuration = currentPauseDuration,
                snackbarContentState = snackbarContentState,
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
    snackbarContentState: MutableState<SnackbarContent?>,
    onStop: () -> Unit
) {
    Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding()))
    val context = LocalContext.current
    val buttonInteractionSource = remember {
        MutableInteractionSource()
    }
    val viewConfiguration = LocalViewConfiguration.current

    val onStopLongClick: () -> Unit = {
        snackbarContentState.value = SnackbarContent(
            text = context.getString(R.string.run_stop),
            snackbarDuration = SnackbarDuration.Long
        )
        onStop()
    }

    val onStopShortTap: () -> Unit = {
        snackbarContentState.value = SnackbarContent(
            text = context.getString(R.string.long_press_to_stop),
            snackbarDuration = SnackbarDuration.Short
        )
    }

    LaunchedEffect(buttonInteractionSource) {
        var isLongPress = false
        buttonInteractionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    isLongPress = false
                    delay(viewConfiguration.longPressTimeoutMillis)
                    isLongPress = true
                    onStopLongClick.invoke()
                }

                is PressInteraction.Release -> {
                    if (!isLongPress) {
                        onStopShortTap.invoke()
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(0.8f)
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .border(2.dp, Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.running_configuration),
                style = MaterialTheme.typography.h6
            )
            Text(
                text = configuration?.name ?: stringResource(R.string.unknown_configuration),
                style = MaterialTheme.typography.h6.copy(fontStyle = FontStyle.Italic)
            )
            if (configuration?.description?.isNotBlank() == true) {
                Text(
                    text = configuration.description,
                    style = MaterialTheme.typography.subtitle2.copy(fontWeight = FontWeight.Normal),
                    maxLines = 3,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }

            Spacer(Modifier.height(16.dp))

            nextEffect?.let { next ->
                PlayingStateUi(
                    playingEffect = playingEffect,
                    nextEffect = next,
                    startPauseAt = startPauseAt,
                    currentPauseDuration = currentPauseDuration
                )
            }
        }
        StopButton(buttonInteractionSource)
    }
}

@Composable
fun ColumnScope.StopButton(interactionSource: MutableInteractionSource) {
    Column(
        Modifier
            .weight(0.15f)
            .fillMaxWidth()
            .border(1.dp, Color.Green),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.8f)
                .padding(bottom = 16.dp),
            onClick = {}) {
            Text(stringResource(id = R.string.run_stop), fontSize = 30.sp)
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
@Preview("runScreenContent")
fun RunScreenPreview() {
    LocationSimulatorTheme(ThemeState(themeType = ThemeType.DARK)) {

        val snackbarContentState = remember {
            mutableStateOf<SnackbarContent?>(null)
        }
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {

            RunScreenContent(
                paddingValues = PaddingValues(4.dp),
                configuration = Configuration(
                    name = "Test configuration",
                    description = "This is a description that needs to be shown up to 3 rows in the running screeen, and I think the ajsdlkfj lakjjdsafh kjsdadhf kjsdahf kjsdahf kjsdahf kjsdahf kjsadhf jksadhfjksadhdkjfhasd kjfjhsadkjfhasdkdjjfh askjdfhkjsadhfkj",
                    randomOrderPlayback = false,
                    components = listOf(TestData.vibrationComponent, TestData.soundComponent)
                ),
                TestData.playingEffectState.playingEffect,
                TestData.playingEffectState.nextEffect,
                TestData.playingEffectState.startPauseAt,
                TestData.playingEffectState.currentPauseDuration,
                snackbarContentState
            ) {}
        }
    }
}

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
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(5f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (playingEffect) {
                null -> PausedUi(currentPauseDuration = currentPauseDuration!!)
                is EffectParameters.Vibration -> VibrationUi(playingEffect)
                is EffectParameters.Sound -> SoundUi(playingEffect)
            }
        }
        Divider(modifier = Modifier.weight(1f), color = Color.Green.copy(alpha = 0.1f))
        Column(
            modifier = Modifier
                .weight(5f)
                .fillMaxWidth()
        ) {
            NextUi(nextEffect = nextEffect)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, Color.Black)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
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
fun EffectPreviewUi(
    effectState: EffectParameters,
    iconSize: Dp = 42.dp,
    ranges: List<RefRangeValue>
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .border(1.dp, Color.Green),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val drawable = when (effectState) {
            is EffectParameters.Vibration -> R.drawable.ic_baseline_vibration_24
            else -> R.drawable.audionouse2
        }
        Image(
            modifier = Modifier.height(iconSize),
            painter = painterResource(id = drawable),
            contentScale = ContentScale.FillHeight,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground)
        )
    }
}

@Composable
fun SoundUi(effectState: EffectParameters.Sound) {
    val volumeRange by remember {
        mutableStateOf(RefRangeValue(
            value = effectState.volume.toBigDecimal(),
            lower = effectState.original.minVolume.toBigDecimal(),
            upper = effectState.original.maxVolume.toBigDecimal(),
            label = R.string.editTimeline_SoundVolume
        ) { value ->
            when {
                value.between(0f, 25f) -> RefRangeValue.Breakpoint.SMALL
                in 75f..100f -> RefRangeValue.Breakpoint.LARGE
                else -> RefRangeValue.Breakpoint.MEDIUM
            }
        })
    }
    EffectPreviewUi(effectState = effectState, ranges = listOf(volumeRange))
}

@Composable
fun VibrationUi(effectState: EffectParameters.Vibration) {

    val strengthRange by remember {
        mutableStateOf(
            RefRangeValue(
                value = effectState.strength.toBigDecimal(),
                lower = effectState.original.minStrength.toBigDecimal(),
                upper = effectState.original.maxStrength.toBigDecimal(),
                label = R.string.editTimeline_Vibration_Strength,
            ) { value ->
                when (value.toInt()) {
                    in 0..25 -> RefRangeValue.Breakpoint.SMALL
                    in 75..100 -> RefRangeValue.Breakpoint.LARGE
                    else -> RefRangeValue.Breakpoint.MEDIUM
                }
            }
        )
    }
    val durationRange by remember {
        mutableStateOf(
            RefRangeValue(
                value = effectState.durationMillis.toBigDecimal(),
                lower = effectState.original.minDuration.toBigDecimal(),
                upper = effectState.original.maxDuration.toBigDecimal(),
                label = R.string.editTimeline_Vibration_duration,
            ) { value ->
                when (value.toLong()) {
                    in 0..5 -> RefRangeValue.Breakpoint.SMALL
                    in 15..50 -> RefRangeValue.Breakpoint.LARGE
                    else -> RefRangeValue.Breakpoint.MEDIUM
                }
            }
        )
    }
    EffectPreviewUi(effectState = effectState, ranges = listOf(strengthRange, durationRange))
}

@Composable
fun PausedUi(currentPauseDuration: Long) {
    Image(imageVector = Icons.Default.PauseCircleOutline, contentDescription = null)
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

data class RefRangeValue(
    val value: BigDecimal,
    val lower: BigDecimal,
    val upper: BigDecimal,
    @StringRes val label: Int,
    val breakpoints: (BigDecimal) -> Breakpoint
) {
    enum class Breakpoint {
        SMALL, MEDIUM, LARGE
    }
}

fun <T : Number> BigDecimal.between(lowerInclusive: T, upperExclusive: T): Boolean {
    val satisfiesLower = this >= lowerInclusive.toBigDecimal()
    val satisfiesUpper = this < upperExclusive.toBigDecimal()
    return satisfiesLower && satisfiesUpper
}