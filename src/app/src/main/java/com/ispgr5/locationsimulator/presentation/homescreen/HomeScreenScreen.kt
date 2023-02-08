package com.ispgr5.locationsimulator.presentation.homescreen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.ui.theme.Shapes
import com.ispgr5.locationsimulator.ui.theme.theBlue

/**
 * The Home Screen.
 *
 */
@ExperimentalAnimationApi
@Composable
fun HomeScreenScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    batteryOptDisableFunction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(onClick = {
            navController.navigate("infoScreen")
        }, modifier = Modifier.padding(5.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_info_24),
                contentDescription = ""
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(110.dp))
        Text(text = stringResource(id = R.string.homescreen_appname), fontSize = 40.sp, color = theBlue)

    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Button(onClick = {
            viewModel.onEvent(HomeScreenEvent.SelectConfiguration)
            navController.navigate("selectScreen")
            Modifier
                .height(100.dp)
                .width(300.dp)

        }) {
            Text(text = stringResource(id = R.string.homescreen_btn_select_profile), fontSize = 30.sp)
        }
        Spacer(modifier = Modifier.height(60.dp))
        Button(onClick = {
            viewModel.onEvent(HomeScreenEvent.SelectConfiguration)
            navController.navigate("selectScreen")
        }) {
            Text(text = stringResource(id = R.string.homescreen_btn_quickstart), fontSize = 30.sp)
        }
        Spacer(modifier = Modifier.height(60.dp))
        Text(text = stringResource(id = R.string.battery_opt_recommendation), textAlign = TextAlign.Center)
        Button(onClick = {
            viewModel.onEvent(HomeScreenEvent.DisableBatteryOptimization(batteryOptDisableFunction))
        }) {
            Text(text = stringResource(id = R.string.battery_opt_button))
        }
    }
}
/*@Composable
@Preview
@ExperimentalAnimationApi
fun TestScreen(){
    HomeScreenScreen()
}*/