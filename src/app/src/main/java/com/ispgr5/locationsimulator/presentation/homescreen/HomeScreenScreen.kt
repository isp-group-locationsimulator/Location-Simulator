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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R

/**
 * The Home Screen.
 *
 */
@ExperimentalAnimationApi
@Composable
fun HomeScreenScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
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
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            viewModel.onEvent(HomeScreenEvent.MusterAuswahl)
            navController.navigate("selectScreen")
        }) {
            Text(text = stringResource(id = R.string.homescreen_btn_select_profile))
        }
        Button(onClick = {
            viewModel.onEvent(HomeScreenEvent.MusterAuswahl)
            navController.navigate("selectScreen")
        }) {
            Text(text = stringResource(id = R.string.homescreen_btn_quickstart))
        }
    }
}
/*@Composable
@Preview
@ExperimentalAnimationApi
fun TestScreen(){
    HomeScreenScreen()
}*/