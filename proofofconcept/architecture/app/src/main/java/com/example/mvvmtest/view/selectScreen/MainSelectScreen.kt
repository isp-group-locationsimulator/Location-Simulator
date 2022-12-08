package com.example.mvvmtest.view.startScreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mvvmtest.view.composeElements.NavigateButton
import com.example.mvvmtest.view.composeElements.NavigateConfigurationButton
import com.example.mvvmtest.viewModel.selectScreen.SelectScreenViewModel


/**
 * This Composable creates the Select Screen
 */
@Composable
fun SelectScreen(
    selectScreenViewModel: SelectScreenViewModel,
    navController: NavHostController
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
            number = selectScreenViewModel.mainViewModel.state.value.configuration.durationVibrateInSec,
            //The function from the viewModel to react on changes
            onValueChanges = selectScreenViewModel::onDurationVibrationChanges
        )
        MyNumberField(
            //The Text shown over the NumberField
            description = "Duration Pause Vibration",
            //The number shown in the NumberField
            number = selectScreenViewModel.mainViewModel.state.value.configuration.durationPauseVibrateInSec,
            //The function from the viewModel to react on changes
            onValueChanges = selectScreenViewModel::onDurationPauseVibrationChanges
        )
        Spacer(modifier = Modifier.height(15.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
            NavigateConfigurationButton(text = "prev", onClickButton = selectScreenViewModel::onNextPrevPressed, -1)
            NavigateConfigurationButton(text = "next", onClickButton = selectScreenViewModel::onNextPrevPressed, 1)
        }
        //space between content above an button
        Spacer(modifier = Modifier.height(15.dp))
        //new column for only centering the Button
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavigateButton("START", selectScreenViewModel::onStartPressed, navController)
        }
    }
}