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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleOutline
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
import com.ispgr5.locationsimulator.presentation.util.between
import com.ispgr5.locationsimulator.presentation.util.millisToSeconds
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import org.joda.time.Instant
import java.math.BigDecimal
import java.math.RoundingMode

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

    val effectState by SimulationService.EffectTimelineBus.observeAsState()

    val playingEffect = effectState?.playingEffect

    val nextEffect = effectState?.nextEffect

    val currentPauseDuration by remember { derivedStateOf { effectState?.currentPauseDuration } }

    val startPauseAt by remember { derivedStateOf { effectState?.startPauseAt } }

    val longPressToStopButtonText = stringResource(id = R.string.long_press_button_to_stop)

    BackPressHandler {
        snackbarContentState.value = SnackbarContent(
            text = longPressToStopButtonText,
            snackbarDuration = SnackbarDuration.Short,
            actionLabel = null
        )
    }

    RunScreenScaffold(
        scaffoldState = scaffoldState,
        configuration = viewModel.state.value.configuration ,
        playingEffect = playingEffect,
        nextEffect = nextEffect,
        startPauseAt = startPauseAt,
        currentPauseDuration = currentPauseDuration,
        snackbarContentState = snackbarContentState,
        onStop = {
            SimulationService.IsPlayingEventBus.postValue(false)
            viewModel.onEvent(RunEvent.StopClicked(stopServiceFunction))
            navController.popBackStack()
        }
    )
}

@Composable
fun RunScreenScaffold(
    scaffoldState: ScaffoldState,
    configuration: Configuration?,
    playingEffect: EffectParameters?,
    nextEffect: EffectParameters?,
    startPauseAt: Instant?,
    currentPauseDuration: Long?,
    snackbarContentState: MutableState<SnackbarContent?>,
    onStop: () -> Unit
) {
    Scaffold(scaffoldState = scaffoldState, topBar = {
        TopBar(
            onBackClick = null,
            title = stringResource(id = R.string.ScreenRun),
            backPossible = false
        )
    }, content = { padding ->
        RunScreenContent(
            paddingValues = padding,
            configuration = configuration,
            playingEffect = playingEffect,
            nextEffect = nextEffect,
            startPauseAt = startPauseAt,
            currentPauseDuration = currentPauseDuration,
            snackbarContentState = snackbarContentState,
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
    configuration: Configuration?,
    playingEffect: EffectParameters?,
    nextEffect: EffectParameters?,
    startPauseAt: Instant?,
    currentPauseDuration: Long?,
    snackbarContentState: MutableState<SnackbarContent?>,
    iconSize: Dp = 42.dp,
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
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(0.85f)
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.running_configuration), style = typography.h6
            )
            Text(
                text = configuration?.name ?: stringResource(R.string.unknown_configuration),
                style = typography.h6.copy(fontStyle = FontStyle.Italic),
            )
            if (configuration?.description?.isNotBlank() == true) {
                Text(
                    text = configuration.description,
                    style = typography.subtitle2.copy(fontWeight = FontWeight.Normal),
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }

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
                        iconSize = iconSize
                    )
                }
            }
        }
        Column(Modifier.fillMaxHeight(0.15f)) {
            StopButton(buttonInteractionSource)
        }
    }
}

@Composable
fun StopButton(interactionSource: MutableInteractionSource) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.8f),
            onClick = {}) {
            Text(stringResource(id = R.string.run_stop), fontSize = 30.sp)
        }
    }
}

private object PreviewData {
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
        ), nextEffect = EffectParameters.Sound(
            startAt = baselineInstant.plus(900L),
            durationMillis = 500L,
            pauseMillis = 500L,
            volume = 0.4f,
            soundName = soundComponent.source,
            original = soundComponent
        ), startPauseAt = null, currentPauseDuration = null, pauseReferenceRange = null
    )
}

@Composable
@Preview("runScreenContent")
fun RunScreenPreview() {
    LocationSimulatorTheme(ThemeState(themeType = ThemeType.LIGHT)) {

        val snackbarContentState = remember {
            mutableStateOf<SnackbarContent?>(null)
        }
        Surface(modifier = Modifier.fillMaxSize(), color = colors.background) {
            RunScreenContent(
                paddingValues = PaddingValues(4.dp),
                configuration = Configuration(
                    name = "Test configuration",
                    description = "This is a description that needs to be shown up to 3 rows in the running screeen, and I think the ajsdlkfj lakjjdsafh kjsdadhf kjsdahf kjsdahf kjsdahf kjsdahf kjsadhf jksadhfjksadhdkjfhasd kjfjhsadkjfhasdkdjjfh askjdfhkjsadhfkj",
                    randomOrderPlayback = false,
                    components = listOf(PreviewData.vibrationComponent, PreviewData.soundComponent)
                ),
                PreviewData.playingEffectState.playingEffect,
                PreviewData.playingEffectState.nextEffect,
                PreviewData.playingEffectState.startPauseAt,
                PreviewData.playingEffectState.currentPauseDuration,
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
    currentPauseDuration: Long?,
    iconSize: Dp
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


    val progressBarProgress = calculateCurrentProgress(
        playingEffect, nextEffect, startPauseAt, lastRefreshInstant
    )

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
            Card(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
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
            Card(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
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
            EffectProgressBar(progressBarProgress, isInPause = currentPauseDuration != null)
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
    progressBarProgress: Float, isInPause: Boolean
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
                true -> colors.secondary.copy(alpha = 0.4f)
                else -> colors.secondary
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
            colorFilter = ColorFilter.tint(colors.onSurface)
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

        Text(text = valueText, style = typography.body2.copy(fontWeight = FontWeight.Bold))
        if (progress != null) {
            LinearProgressIndicator(
                modifier = Modifier
                    .height(3.dp)
                    .fillMaxWidth(boxWeight.weight),
                progress = progress.setScale(2).toFloat()
            )
            Row(
                modifier = Modifier.fillMaxWidth(boxWeight.weight)
            ) {
                Text(
                    text = lowerText,
                    style = typography.caption.copy(textAlign = TextAlign.Start),
                    modifier = Modifier.weight(1f, fill = true)
                )
                Text(
                    text = upperText,
                    style = typography.caption.copy(textAlign = TextAlign.End),
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
    Text(effectState.soundName, style = typography.subtitle2)
    EffectPreviewUi(
        effectState = effectState, ranges = listOf(volumeRange, pauseRange), iconSize = iconSize
    )
}

@Composable
fun VibrationUi(effectState: EffectParameters.Vibration, iconSize: Dp) {
    val original = effectState.original as ConfigComponent.Vibration
    val strengthRange = RefRangeValue(value = effectState.strength.toBigDecimal(),
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
        ranges = listOf(strengthRange, durationRange, pauseRange),
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
            colorFilter = ColorFilter.tint(colors.onSurface)
        )
        Text(stringResource(id = R.string.in_pause))
        Text(
            text = "${BigDecimal.valueOf(currentPauseDuration).millisToSeconds()} s",
            style = typography.h6.copy(fontFamily = FontFamily.Monospace)
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
        Text(stringResource(id = R.string.next_effect), style = typography.subtitle1)
        when (nextEffect) {
            is EffectParameters.Vibration -> VibrationUi(nextEffect, iconSize)
            is EffectParameters.Sound -> SoundUi(nextEffect, iconSize)
        }
    }
}

@Composable
fun RunScreenScreenshotPreview() {
    // TODO:
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
        return when (val rangeWidth = upper - lower) {
            BigDecimal.valueOf(0) -> null
            else -> lowerSanitized.divide(rangeWidth, 2, RoundingMode.HALF_UP)
        }
    }

    enum class Breakpoint(val weight: Float) {
        SMALL(1 / 2f), MEDIUM(2 / 3f), LARGE(1f)
    }
}