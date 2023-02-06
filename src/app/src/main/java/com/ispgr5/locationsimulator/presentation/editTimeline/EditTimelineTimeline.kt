package com.ispgr5.locationsimulator.presentation.editTimeline

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RangeSlider
import androidx.compose.material.Text
import androidx.compose.runtime.*


@Preview(name = "Preview1", device = Devices.PIXEL, showSystemUi = true)
@Composable
fun TestScreen(){
    EditTimelineTimeline()
}

@Composable
fun EditTimelineTimeline(){





}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditVibration(){
    var sliderPosition by remember { mutableStateOf(0f..100f) }
    Text(text = sliderPosition.toString())
    RangeSlider(
        value = sliderPosition,
        onValueChange = { sliderPosition = it },
        valueRange = 0f..100f,
        onValueChangeFinished = {
            // launch some business logic update with the state you hold
            // viewModel.updateSelectedSliderValue(sliderPosition)
        },
    )

}