package com.ispgr5.locationsimulator.presentation.run

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.network.ClientHandler
import com.ispgr5.locationsimulator.network.ClientSignal
import com.ispgr5.locationsimulator.network.Commands
import com.ispgr5.locationsimulator.presentation.previewData.AppPreview
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData.runScreenPreviewInitialRefresh
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData.runScreenPreviewStatePaused
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData.runScreenPreviewStatePlaying
import com.ispgr5.locationsimulator.presentation.universalComponents.SnackbarContent
import com.ispgr5.locationsimulator.presentation.util.AppSnackbarHost
import com.ispgr5.locationsimulator.presentation.util.RenderSnackbarOnChange
import com.ispgr5.locationsimulator.presentation.util.between
import com.ispgr5.locationsimulator.presentation.util.millisToSeconds
import com.ispgr5.locationsimulator.presentation.util.vibratorHasAmplitudeControlAndReason
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.joda.time.Instant
import java.math.BigDecimal
import java.math.RoundingMode

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
    snackbarHostState: SnackbarHostState,
    viewModel: RunViewModel = hiltViewModel()
) {
    BackHandler {
        viewModel.onEvent(RunEvent.StopClicked(stopServiceFunction))
        navController.popBackStack()
    }

    val snackbarContentState = remember {
        mutableStateOf<SnackbarContent?>(null)
    }

    val onStop: () -> Unit = {
        ClientHandler.sendToClients(Commands.IS_IDLE)
        ClientHandler.isPlayingState.set(false)
        SimulationService.IsPlayingEventBus.postValue(false)
        viewModel.onEvent(RunEvent.StopClicked(stopServiceFunction))
        navController.popBackStack()
    }

    RenderSnackbarOnChange(
        snackbarHostState = snackbarHostState,
        snackbarContent = snackbarContentState
    )

    val clientMessage: ClientSignal? by ClientHandler.clientSignal.observeAsState()
    if(clientMessage is ClientSignal.StopTraining) {
        ClientHandler.clientSignal.value = null
        onStop()
    }
    if(clientMessage is ClientSignal.StartTraining) {   // ignore message
        ClientHandler.clientSignal.value = null
    }

    val effectState: EffectTimelineState? by SimulationService.EffectTimelineStateBus.observeAsState()

    val playingEffect = effectState?.playingEffect

    val nextEffect = effectState?.nextEffect

    val currentPauseDuration by remember { derivedStateOf { effectState?.currentPauseDuration } }

    val startPauseAt by remember { derivedStateOf { effectState?.startPauseAt } }

    val longPressToStopButtonText = stringResource(id = R.string.long_press_button_to_stop)

    val initialRefreshInstant by remember {
        mutableStateOf(Instant.now())
    }

    BackPressHandler {
        snackbarContentState.value = SnackbarContent(
            text = longPressToStopButtonText,
            snackbarDuration = SnackbarDuration.Short,
            actionLabel = null
        )
    }

    val configuration by remember {
        derivedStateOf {
            viewModel.state.value.configuration
        }
    }

    configuration?.let { conf ->
        RunScreenScaffold(
            configuration = conf,
            playingEffect = playingEffect,
            nextEffect = nextEffect,
            startPauseAt = startPauseAt,
            currentPauseDuration = currentPauseDuration,
            snackbarHostState = snackbarHostState,
            snackbarContentState = snackbarContentState,
            initialRefreshInstant = initialRefreshInstant,
            onStop = onStop
        )
    }

}

@Composable
@AppPreview
fun RunScreenPausedPreview() {
    RunScreenPreviewScaffold(
        configuration = PreviewData.previewConfigurations.first(),
        effectTimelineState = runScreenPreviewStatePaused,
        initialRefreshInstant = runScreenPreviewInitialRefresh
    )
}

@Composable
@AppPreview
fun RunScreenActivePreview() {
    RunScreenPreviewScaffold(
        configuration = PreviewData.previewConfigurations.first(),
        effectTimelineState = runScreenPreviewStatePlaying,
        initialRefreshInstant = runScreenPreviewInitialRefresh
    )
}

@Composable
fun RunScreenScaffold(
    configuration: Configuration,
    playingEffect: EffectParameters?,
    nextEffect: EffectParameters?,
    startPauseAt: Instant?,
    currentPauseDuration: Long?,
    snackbarHostState: SnackbarHostState,
    snackbarContentState: MutableState<SnackbarContent?>,
    initialRefreshInstant: Instant,
    onStop: () -> Unit
) {
    Scaffold(
        snackbarHost = {
            AppSnackbarHost(snackbarHostState)
        },
        content = { padding ->
            RunScreenContent(
                paddingValues = padding,
                configuration = configuration,
                playingEffect = playingEffect,
                nextEffect = nextEffect,
                startPauseAt = startPauseAt,
                currentPauseDuration = currentPauseDuration,
                snackbarContentState = snackbarContentState,
                initialRefreshInstant = initialRefreshInstant,
                onStop = onStop
            )
        })
}

/**
 * https://www.valueof.io/blog/intercept-back-press-button-in-jetpack-compose
 */
@Composable
fun BackPressHandler(
    backPressedDispatcher: OnBackPressedDispatcher? = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher,
    onBackPressed: () -> Unit
) {
    val currentOnBackPressed by rememberUpdatedState(newValue = onBackPressed)

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
    }

    DisposableEffect(key1 = backPressedDispatcher) {
        backPressedDispatcher?.addCallback(backCallback)

        onDispose {
            backCallback.remove()
        }
    }
}

@Composable
fun RunScreenContent(
    paddingValues: PaddingValues,
    configuration: Configuration,
    playingEffect: EffectParameters?,
    nextEffect: EffectParameters?,
    startPauseAt: Instant?,
    currentPauseDuration: Long?,
    snackbarContentState: MutableState<SnackbarContent?>,
    iconSize: Dp = 42.dp,
    initialRefreshInstant: Instant,
    onStop: () -> Unit
) {
    val context = LocalContext.current
    val buttonInteractionSource = remember {
        MutableInteractionSource()
    }
    val viewConfiguration = LocalViewConfiguration.current

    val onStopLongClick: () -> Unit = {
        snackbarContentState.value = SnackbarContent(
            text = context.getString(R.string.run_stop), snackbarDuration = SnackbarDuration.Long
        )
        onStop()
    }

    val onStopShortTap: () -> Unit = {
        snackbarContentState.value = SnackbarContent(
            text = context.getString(R.string.long_press_to_stop),
            snackbarDuration = SnackbarDuration.Short,
            withDismissAction = true
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
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            buildAnnotatedString {
                append(context.getString(R.string.ScreenRun))
                append(": ")
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(configuration.name)
                }
            },
            modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Column(
            modifier = Modifier
                .weight(0.85f)
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            nextEffect?.let { next ->
                Box(
                    Modifier
                        .weight(1f)
                        .width(IntrinsicSize.Max)
                ) {
                    PlayingStateUi(
                        playingEffect = playingEffect,
                        nextEffect = next,
                        startPauseAt = startPauseAt,
                        currentPauseDuration = currentPauseDuration,
                        initialRefreshInstant = initialRefreshInstant,
                        iconSize = iconSize
                    )
                }
            }
        }
        Column(modifier = Modifier.height(IntrinsicSize.Min)) {
            StopButton(buttonInteractionSource)
        }
    }
}

@Composable
fun StopButton(interactionSource: MutableInteractionSource) {
    Column(
        Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(interactionSource = interactionSource,
            modifier = Modifier.height(IntrinsicSize.Min),
            onClick = {}) {
            Text(stringResource(id = R.string.run_stop), fontSize = 30.sp)
        }
    }
}

object RunScreenPreviewData {
    val baselineInstant: Instant = Instant.ofEpochMilli(1702738800000L)
    private val vibrationComponent = ConfigComponent.Vibration(
        id = 0,
        name = "vib0",
        minStrength = 20,
        maxStrength = 50,
        minPause = 200,
        maxPause = 500,
        minDuration = 200,
        maxDuration = 500
    )
    private val soundComponent = ConfigComponent.Sound(
        id = 1,
        name = "sound",
        source = "Source.mp3",
        minVolume = 0.1f,
        maxVolume = 0.5f,
        minPause = 200,
        maxPause = 500,
    )
    private val lastRefresh: Instant = baselineInstant.plus(300L)

    val configuration = Configuration(
        name = "Test configuration",
        description = "A description for the configuration",
        randomOrderPlayback = false,
        components = listOf(vibrationComponent, soundComponent)
    )

    val effectTimelinePlayingState = EffectTimelineState(
        lastRefresh, playingEffect = EffectParameters.Vibration(
            startAt = baselineInstant.minus(100L),
            durationMillis = 500L,
            pauseMillis = 500L,
            strength = 40,
            original = vibrationComponent
        ), nextEffect = EffectParameters.Sound(
            startAt = baselineInstant.plus(900L),
            durationMillis = 500L,
            pauseMillis = 500L,
            volume = 0.4f,
            soundName = soundComponent.source,
            original = soundComponent
        ), startPauseAt = null, currentPauseDuration = null, pauseReferenceRange = null
    )

    val effectTimelinePausedState = EffectTimelineState(
        lastRefresh,
        playingEffect = null,
        nextEffect = EffectParameters.Vibration(
            startAt = baselineInstant.plus(900L),
            durationMillis = 500L,
            pauseMillis = 500L,
            strength = 42,
            original = vibrationComponent
        ),
        startPauseAt = baselineInstant.minus(100L),
        currentPauseDuration = 1000L,
        pauseReferenceRange = BigDecimal.valueOf(1000L) to BigDecimal.valueOf(1000L)
    )
}

@Composable
@Preview("runScreenContent")
fun RunScreenPreview() {
    LocationSimulatorTheme {
        val snackbarContentState = remember {
            mutableStateOf<SnackbarContent?>(null)
        }
        Surface(modifier = Modifier.fillMaxSize(), color = colorScheme.background) {
            RunScreenContent(
                paddingValues = PaddingValues(4.dp),
                configuration = RunScreenPreviewData.configuration,
                playingEffect = RunScreenPreviewData.effectTimelinePlayingState.playingEffect,
                nextEffect = RunScreenPreviewData.effectTimelinePlayingState.nextEffect,
                startPauseAt = RunScreenPreviewData.effectTimelinePlayingState.startPauseAt,
                currentPauseDuration = RunScreenPreviewData.effectTimelinePlayingState.currentPauseDuration,
                snackbarContentState = snackbarContentState,
                initialRefreshInstant = RunScreenPreviewData.baselineInstant,
            ) {}
        }
    }
}

@Composable
fun PlayingStateUi(
    playingEffect: EffectParameters?,
    nextEffect: EffectParameters,
    startPauseAt: Instant?,
    currentPauseDuration: Long?,
    initialRefreshInstant: Instant,
    iconSize: Dp
) {
    var lastRefreshInstant by remember {
        mutableStateOf(initialRefreshInstant)
    }

    LaunchedEffect(SimulationService.IsPlayingEventBus.value) {
        while (true) {
            delay(49)
            lastRefreshInstant = Instant.now()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            ElevatedCard(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = colorScheme.surfaceContainer,
                    contentColor = colorScheme.onSurface
                )
            ) {
                Column(
                    modifier = Modifier
                        .weight(5f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (playingEffect) {
                        null -> PausedUi(
                            currentPauseDuration = currentPauseDuration!!, iconSize = iconSize
                        )

                        is EffectParameters.Vibration -> VibrationUi(playingEffect, iconSize)
                        is EffectParameters.Sound -> SoundUi(playingEffect, iconSize = iconSize)
                    }
                }
            }
            ElevatedCard(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = colorScheme.surfaceContainer,
                    contentColor = colorScheme.onSurface
                )
            ) {
                NextUi(nextEffect = nextEffect)
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.Center
        ) {
            EffectProgressBar(progressBarProgress = {
                calculateCurrentProgress(
                    playingEffect, nextEffect, startPauseAt, lastRefreshInstant
                )
            }, isInPause = currentPauseDuration != null)
        }


    }
}

fun calculateCurrentProgress(
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
    progressBarProgress: () -> Float, isInPause: Boolean
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
                true -> colorScheme.secondary.copy(alpha = 0.4f)
                else -> colorScheme.secondary
            }
        )
    }
}

@Composable
fun EffectPreviewUi(
    effectState: EffectParameters, iconSize: Dp, ranges: List<RefRangeValue>
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .verticalScroll(rememberScrollState()),
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
            colorFilter = ColorFilter.tint(colorScheme.onSurface)
        )
        ranges.forEach { range ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(id = range.label), modifier = Modifier.weight(1 / 3f, true)
                )
                RefRangeIndicator(range = range, modifier = Modifier.weight(2 / 3f, true))
            }
        }
    }
}

@Composable
fun RefRangeIndicator(modifier: Modifier = Modifier, range: RefRangeValue) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val boxWeight = range.breakpoints(range.upper - range.lower)
        val progress = range.valueRelative()
        val lowerText = range.formatValue(range.lower)
        val upperText = range.formatValue(range.upper)
        val valueText = range.formatValue(range.value)

        Text(text = valueText, style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        if (progress != null) {
            LinearProgressIndicator(
                modifier = Modifier
                    .height(3.dp)
                    .fillMaxWidth(boxWeight.weight),
                progress = { progress.setScale(2).toFloat() }
            )
            Row(
                modifier = Modifier.fillMaxWidth(boxWeight.weight)
            ) {
                Text(
                    text = lowerText,
                    style = typography.bodySmall.copy(textAlign = TextAlign.Start),
                    modifier = Modifier.weight(1f, fill = true)
                )
                Text(
                    text = upperText,
                    style = typography.bodySmall.copy(textAlign = TextAlign.End),
                    modifier = Modifier.weight(1f, fill = true)
                )
            }
        }
    }
}

@Composable
fun SoundUi(effectState: EffectParameters.Sound, iconSize: Dp) {
    val original = (effectState.original as ConfigComponent.Sound)
    val volumeRange = RefRangeValue(value = effectState.volume.toBigDecimal() * 100.toBigDecimal(),
        lower = original.minVolume.toBigDecimal() * 100.toBigDecimal(),
        upper = original.maxVolume.toBigDecimal() * 100.toBigDecimal(),
        label = R.string.editTimeline_SoundVolume,
        breakpoints = { width ->
            when {
                width.between(0f, 25f) -> RefRangeValue.Breakpoint.SMALL
                width.between(75f, 101f) -> RefRangeValue.Breakpoint.LARGE
                else -> RefRangeValue.Breakpoint.MEDIUM
            }
        },
        formatValue = {
            "${it.setScale(0, RoundingMode.HALF_UP)} %"
        })

    val pauseRange = buildPauseRange(effectState, original)
    Text(effectState.soundName, style = typography.titleSmall)
    EffectPreviewUi(
        effectState = effectState, ranges = listOf(volumeRange, pauseRange), iconSize = iconSize
    )
}

@Composable
fun VibrationUi(effectState: EffectParameters.Vibration, iconSize: Dp) {
    val original = effectState.original as ConfigComponent.Vibration
    val strengthRange = when {
        LocalContext.current.vibratorHasAmplitudeControlAndReason.first -> RefRangeValue(value = effectState.strength.toBigDecimal(),
            lower = original.minStrength.toBigDecimal(),
            upper = original.maxStrength.toBigDecimal(),
            label = R.string.editTimeline_Vibration_Strength,
            breakpoints = { width ->
                when (width.toInt()) {
                    in 0..100 -> RefRangeValue.Breakpoint.SMALL
                    in 100..200 -> RefRangeValue.Breakpoint.LARGE
                    else -> RefRangeValue.Breakpoint.MEDIUM
                }
            },
            formatValue = {
                it.setScale(0, RoundingMode.FLOOR).toString()
            })

        else -> null
    }
    val durationRange = RefRangeValue(value = effectState.durationMillis.toBigDecimal(),
        lower = original.minDuration.toBigDecimal(),
        upper = original.maxDuration.toBigDecimal(),
        label = R.string.editTimeline_Vibration_duration,
        breakpoints = { width ->
            when (width.millisToSeconds().toLong()) {
                in 0..5 -> RefRangeValue.Breakpoint.SMALL
                in 15..50 -> RefRangeValue.Breakpoint.LARGE
                else -> RefRangeValue.Breakpoint.MEDIUM
            }
        },
        formatValue = {
            "${it.millisToSeconds()} s"
        })


    val pauseRange = buildPauseRange(effectState, original)
    EffectPreviewUi(
        effectState = effectState,
        ranges = listOfNotNull(strengthRange, durationRange, pauseRange),
        iconSize = iconSize
    )
}

fun buildPauseRange(
    effectState: EffectParameters, original: ConfigComponent
): RefRangeValue {
    return RefRangeValue(effectState.pauseMillis.toBigDecimal(), lower = when (original) {
        is ConfigComponent.Sound -> original.minPause
        is ConfigComponent.Vibration -> original.minPause
    }.toBigDecimal(), upper = when (original) {
        is ConfigComponent.Sound -> original.maxPause
        is ConfigComponent.Vibration -> original.maxPause
    }.toBigDecimal(), label = R.string.editTimeline_Pause, breakpoints = { width ->
        when (width.millisToSeconds().setScale(0, RoundingMode.FLOOR).toLong()) {
            in 0..10 -> RefRangeValue.Breakpoint.SMALL
            in 10..20 -> RefRangeValue.Breakpoint.MEDIUM
            else -> RefRangeValue.Breakpoint.LARGE
        }
    }, formatValue = {
        "${it.millisToSeconds()} s"
    })
}


@Composable
fun PausedUi(currentPauseDuration: Long, iconSize: Dp) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = Icons.Default.PauseCircleOutline,
            contentDescription = null,
            modifier = Modifier.height(iconSize),
            contentScale = ContentScale.FillHeight,
            colorFilter = ColorFilter.tint(colorScheme.onSurface)
        )
        Text(stringResource(id = R.string.in_pause))
        Text(
            text = "${BigDecimal.valueOf(currentPauseDuration).millisToSeconds()} s",
            style = typography.titleLarge.copy(fontFamily = FontFamily.Monospace)
        )
    }
}

@Composable
fun NextUi(nextEffect: EffectParameters, iconSize: Dp = 32.dp) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(id = R.string.next_effect), style = typography.titleMedium)
        when (nextEffect) {
            is EffectParameters.Vibration -> VibrationUi(nextEffect, iconSize)
            is EffectParameters.Sound -> SoundUi(nextEffect, iconSize)
        }
    }
}

@Composable
fun RunScreenPreviewScaffold(
    configuration: Configuration,
    effectTimelineState: EffectTimelineState,
    initialRefreshInstant: Instant,
) {
    val snackbarContentState = remember {
        mutableStateOf<SnackbarContent?>(null)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    LocationSimulatorTheme {
        RunScreenScaffold(
            configuration = configuration,
            playingEffect = effectTimelineState.playingEffect,
            nextEffect = effectTimelineState.nextEffect,
            startPauseAt = effectTimelineState.startPauseAt,
            currentPauseDuration = effectTimelineState.currentPauseDuration,
            snackbarHostState = snackbarHostState,
            snackbarContentState = snackbarContentState,
            initialRefreshInstant = initialRefreshInstant
        ) { }
    }
}

data class RefRangeValue(
    val value: BigDecimal,
    val lower: BigDecimal,
    val upper: BigDecimal,
    @StringRes val label: Int,
    val breakpoints: (BigDecimal) -> Breakpoint,
    val formatValue: (BigDecimal) -> String
) {
    fun valueRelative(): BigDecimal? {
        val lowerSanitized = value - lower
        val rangeWidth = upper - lower
        return when {
            rangeWidth == BigDecimal.valueOf(0) -> null
            (BigDecimal.valueOf(0) - rangeWidth).abs() <= BigDecimal.valueOf(0.03) -> null //close but not quite null
            else -> lowerSanitized.divide(rangeWidth, 2, RoundingMode.HALF_UP)
        }
    }

    enum class Breakpoint(val weight: Float) {
        SMALL(1 / 2f), MEDIUM(2 / 3f), LARGE(1f)
    }
}