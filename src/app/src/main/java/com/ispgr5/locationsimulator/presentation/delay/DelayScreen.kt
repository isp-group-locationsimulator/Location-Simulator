package com.ispgr5.locationsimulator.presentation.delay

import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.ispgr5.locationsimulator.presentation.editTimeline.components.Timeline
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar

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
        topBar = { TopBar(navController, stringResource(id = R.string.ScreenDelay)) },
        content = {
            Spacer(modifier = Modifier.height(it.calculateTopPadding()))
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .testTag(TestTags.DELAY_MAIN_COLUMN),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(8.dp))

                if (state.configuration == null) {
                    Text(text = "Configuration is null")
                } else {
                    Text(
                        text = state.configuration.name,
                        style = TextStyle(fontSize = 24.sp),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    if (state.configuration.description.isNotBlank()) {
                        Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = state.configuration.description,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }

                /**
                 * The Timeline
                 */
                Spacer(modifier = Modifier.size(8.dp))
                Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
                Spacer(modifier = Modifier.size(8.dp))

                Column {
                    state.configuration?.components?.let { components ->
                        Timeline(
                            components = components,
                            selectedComponent = null,
                            onSelectAComponent = fun(_: ConfigComponent) {},
                            onAddClicked = fun() {},
                            showAddButton = false
                        )
                    }
                }

                Spacer(modifier = Modifier.size(5.dp))

                //extra runtime
                Text(
                    String.format(
                        "%.0f",
                        state.configuration?.getMinDuration(context, soundsDirUri)
                    )
                            + "s - " +
                            String.format(
                                "%.0f",
                                state.configuration?.getMaxDuration(context, soundsDirUri)
                            )
                            + "s " + stringResource(id = R.string.ConfigInfoPerIteration)
                )

                Spacer(modifier = Modifier.size(3.dp))
                Divider(color = MaterialTheme.colors.primary, thickness = 1.dp)
                Spacer(modifier = Modifier.size(8.dp))

                //The timer component
                //val configuration by viewModel.state
                Timer(viewModel, startServiceFunction, navController)
            }
        })
}