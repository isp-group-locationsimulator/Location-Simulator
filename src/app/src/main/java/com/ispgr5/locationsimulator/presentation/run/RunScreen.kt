package com.ispgr5.locationsimulator.presentation.run

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar

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
	BackHandler{
		viewModel.onEvent(RunEvent.StopClicked(stopServiceFunction))
		navController.popBackStack()
	}

	Scaffold(
		scaffoldState = scaffoldState,
		topBar = { TopBar(navController, stringResource(id = R.string.ScreenRun),false) },
		content = {
			Spacer(modifier = Modifier.height(it.calculateTopPadding()))

			/**
			 * The stop button
			 */
			Column(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Button(
					modifier = Modifier
						.width(200.dp)
						.height(120.dp)
						.testTag(TestTags.RUN_END_BUTTON),
					onClick = {
						viewModel.onEvent(RunEvent.StopClicked(stopServiceFunction))
						navController.popBackStack()
					})  {
					Text(text = stringResource(id = R.string.run_stop), fontSize = 30.sp)
				}
			}
		})
}