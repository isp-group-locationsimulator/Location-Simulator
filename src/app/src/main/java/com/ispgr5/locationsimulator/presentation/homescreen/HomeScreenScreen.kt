package com.ispgr5.locationsimulator.presentation.homescreen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ispgr5.locationsimulator.R
import androidx.navigation.NavController

/**
 * The Home Screen.
 *
 */
@ExperimentalAnimationApi
@Composable
fun HomeScreenScreen(
    navController: NavController,
    viewModel:HomeScreenViewModel = hiltViewModel()
) {

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {Button(onClick = {
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