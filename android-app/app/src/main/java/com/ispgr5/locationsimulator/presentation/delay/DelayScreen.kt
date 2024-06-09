package com.ispgr5.locationsimulator.presentation.delay

import android.content.Context
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.editTimeline.components.Timeline
import com.ispgr5.locationsimulator.presentation.previewData.AppPreview
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData.delayScreenInitialTimerState
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData.delayScreenPreviewState
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData.themePreviewState
import com.ispgr5.locationsimulator.presentation.universalComponents.LocationSimulatorTopBar
import com.ispgr5.locationsimulator.presentation.util.Screen
import com.ispgr5.locationsimulator.presentation.util.millisToSeconds
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme

private const val TAG = "DelayScreen"

/**
 * The Delay Screen.
 * Here you can check you have Select the right Configuration
 * and set a timer
 */
@ExperimentalAnimationApi
@Composable
fun DelayScreen(
    navController: NavController,
    viewModel: DelayViewModel = hiltViewModel(),
    startServiceFunction: (String, List<ConfigComponent>, Boolean) -> Unit,
    soundsDirUri: String, //the sounds Directory Uri needed for calculating Sound Length
) {
    //The state from viewmodel
    val state = viewModel.state.value
    val timerState = remember {
        mutableStateOf(TimerState(inhibitStart = false))
    }

    DelayScreenScaffold(
        state = state,
        timerState = timerState,
        soundsDirUri = soundsDirUri,
        onBackClick = {
            timerState.value = timerState.value.reset(inhibitStart = true)
            navController.popBackStack()
        }
    ) { configurationId ->
        // make very sure that the simulation doesn't start when the timer has been cancelled,
        // either by clicking the "back' button in the scaffold, or by clicking the big "cancel"
        // button in the timer composable
        if (!timerState.value.inhibitStart) {
            viewModel.onEvent(DelayEvent.StartClicked(startServiceFunction))
            navController.navigate(route = Screen.RunScreen.createRoute(configurationId))
        } else {
            Log.w(TAG, "start vibration fired, but inhibited: $timerState")
        }
    }

}

@Composable
fun DelayScreenScaffold(
    state: DelayScreenState,
    timerState: MutableState<TimerState>,
    soundsDirUri: String,
    onBackClick: () -> Unit,
    onFinishTimer: (configurationId: Int) -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            LocationSimulatorTopBar(onBackClick = {
                onBackClick()
            }, title = stringResource(id = R.string.ScreenDelay))
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                state.configuration?.let { configuration ->
                    DelayScreenContent(
                        configuration = configuration,
                        timerState = timerState,
                        context = context,
                        soundsDirUri = soundsDirUri,
                        onFinishTimer = onFinishTimer
                    )
                }
            }
        })
}

@Composable
fun DelayScreenContent(
    configuration: Configuration,
    timerState: MutableState<TimerState>,
    context: Context,
    soundsDirUri: String,
    onFinishTimer: (configurationId: Int) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .testTag(TestTags.DELAY_MAIN_COLUMN),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = configuration.name,
            style = TextStyle(fontSize = 24.sp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        if (configuration.description.isNotBlank()) {
            HorizontalDivider(color = colorScheme.primary, thickness = 1.dp)
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = configuration.description,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        /**
         * The Timeline
         */
        Spacer(modifier = Modifier.size(8.dp))
        HorizontalDivider(color = colorScheme.primary, thickness = 1.dp)
        Spacer(modifier = Modifier.size(8.dp))

        Timeline(
            components = configuration.components,
            selectedComponent = null,
            onSelectAComponent = null,
            onAddClicked = {},
            interactive = false
        )

        Spacer(modifier = Modifier.size(5.dp))

        val minDuration = configuration.getMinDuration(context, soundsDirUri)
        val maxDuration = configuration.getMaxDuration(context, soundsDirUri)

        //extra runtime
        val runtimeString = stringResource(
            id = R.string.ConfigInfoSecondsPerIteration,
            minDuration.millisToSeconds().toString(),
            maxDuration.millisToSeconds().toString()
        )

        Text(runtimeString)

        Spacer(modifier = Modifier.size(3.dp))
        HorizontalDivider(color = colorScheme.primary, thickness = 1.dp)
        Spacer(modifier = Modifier.size(8.dp))

        //The timer component
        DelayTimer(
            timerState = timerState,
            configurationId = configuration.id!!,
            onFinishTimer = onFinishTimer
        )
    }
}

@Composable
@AppPreview
fun DelayScreenPreview() {
    val timerState = remember {
        mutableStateOf(delayScreenInitialTimerState)
    }
    LocationSimulatorTheme(themeState = themePreviewState) {
        DelayScreenScaffold(
            state = delayScreenPreviewState,
            soundsDirUri = "sounds",
            timerState = timerState,
            onBackClick = {},
            onFinishTimer = { }
        )
    }
}