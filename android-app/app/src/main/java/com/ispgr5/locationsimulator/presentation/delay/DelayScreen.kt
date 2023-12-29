package com.ispgr5.locationsimulator.presentation.delay

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Configuration
import com.ispgr5.locationsimulator.presentation.editTimeline.components.Timeline
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar
import com.ispgr5.locationsimulator.presentation.util.millisToSeconds

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
    context: Context, //context needed for calculating Sound Length
    soundsDirUri: String, //the sounds Directory Uri needed for calculating Sound Length
    scaffoldState: ScaffoldState
) {
    //The state from viewmodel
    val state = viewModel.state.value


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(onBackClick = {
            navController.popBackStack()
        }, title = stringResource(id = R.string.ScreenDelay)) },
        content = { paddingValues ->
            state.configuration?.let { configuration ->
                DelayScreenContent(
                    paddingValues = paddingValues,
                    configuration = configuration,
                    context = context,
                    soundsDirUri = soundsDirUri,
                    viewModel = viewModel,
                    startServiceFunction = startServiceFunction,
                    navController = navController
                )
            }
        })
}

@Composable
fun DelayScreenContent(
    paddingValues: PaddingValues,
    configuration: Configuration,
    context: Context,
    soundsDirUri: String,
    viewModel: DelayViewModel,
    startServiceFunction: (String, List<ConfigComponent>, Boolean) -> Unit,
    navController: NavController
) {
    Spacer(modifier = Modifier.height(paddingValues.calculateTopPadding()))
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
            Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
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
        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
        Spacer(modifier = Modifier.size(8.dp))

        Timeline(
            components = configuration.components,
            selectedComponent = null,
            onSelectAComponent = null,
            onAddClicked = {},
            showAddButton = false
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
        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
        Spacer(modifier = Modifier.size(8.dp))

        //The timer component
        //val configuration by viewModel.state
        Timer(viewModel, startServiceFunction, navController)
    }
}