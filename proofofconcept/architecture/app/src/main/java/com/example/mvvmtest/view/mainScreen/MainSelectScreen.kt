package com.example.mvvmtest.view.mainScreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mvvmtest.view.composeElements.NavigateButton
import com.example.mvvmtest.viewModel.SelectScreenViewModel


/**
 * This Composable creates the Select Screen
 */
@Composable
fun SelectScreen(
    selectScreenViewModel: SelectScreenViewModel,
    navController:NavHostController
) {
    Column(
        Modifier
            .padding(15.dp)
            .fillMaxSize()
    ) {
        MyNumberField(
            //The Text shown over the NumberField
            description = "Duration Vibration",
            //The number shown in the NumberField
            number = selectScreenViewModel.currentConfiguration.durationVibrateInSec,
            //The function from the viewModel to react on changes
            onValueChanges = selectScreenViewModel::onDurationVibrationChanges
        )
        MyNumberField(
            //The Text shown over the NumberField
            description = "Duration Pause Vibration",
            //The number shown in the NumberField
            number = selectScreenViewModel.currentConfiguration.durationPauseVibrateInSec,
            //The function from the viewModel to react on changes
            onValueChanges = selectScreenViewModel::onDurationPauseVibrationChanges
        )
        //space between content above an button
        Spacer(modifier = Modifier.height(15.dp))
        //new column for only centering the Button
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavigateButton("START", selectScreenViewModel::onStartPressed, navController)
        }
    }
}