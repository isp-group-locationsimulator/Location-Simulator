package com.ispgr5.locationsimulator.presentation.editTimeline.components

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RangeSlider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Sound
import com.ispgr5.locationsimulator.domain.model.Vibration
import com.ispgr5.locationsimulator.presentation.editTimeline.RangeConverter
import kotlin.properties.Delegates

@Composable
fun EditConfigComponent(
    configComponent: ConfigComponent?,
    onSoundValueChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onPauseValueChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onVibStrengthChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onVibDurationChanged: (ClosedFloatingPointRange<Float>) -> Unit
) {
    //so no Time line Item is selected for now
    if (configComponent == null) {
        return
    }

    //needed to show the Pause Slider separately
    var minPause by Delegates.notNull<Int>()
    var maxPause by Delegates.notNull<Int>()

    when (configComponent) {
        is Sound -> {
            minPause = configComponent.minPause
            maxPause = configComponent.maxPause

            /**
             * Volume
             */
            Text(text = stringResource(id = R.string.editTimeline_SoundVolume) + ":")
            Text(
                RangeConverter.eightBitIntToPercentageFloat(configComponent.minVolume).toInt().toString() + "%"
                        + stringResource(id = R.string.editTimeline_range) + RangeConverter.eightBitIntToPercentageFloat(configComponent.maxVolume).toInt()
                    .toString() + "% "
            )
            SliderForRange(
                value = RangeConverter.eightBitIntToPercentageFloat(configComponent.minVolume)..RangeConverter.eightBitIntToPercentageFloat(configComponent.maxVolume),
                func = { value: ClosedFloatingPointRange<Float> -> onSoundValueChanged(value) },
                range = 0f..100f
            )
        }
        is Vibration -> {
            minPause = configComponent.minPause
            maxPause = configComponent.maxPause

            /**
             * The Vibration Strength
             */
            Text(text = stringResource(id = R.string.editTimeline_Vibration_Strength))
            Text(
                RangeConverter.eightBitIntToPercentageFloat(configComponent.minStrength).toInt().toString() + "% "
                        + stringResource(id = R.string.editTimeline_range) + RangeConverter.eightBitIntToPercentageFloat(configComponent.maxStrength).toInt()
                    .toString() + "%"
            )
            SliderForRange(
                value = RangeConverter.eightBitIntToPercentageFloat(configComponent.minStrength)..RangeConverter.eightBitIntToPercentageFloat(configComponent.maxStrength),
                func = { value: ClosedFloatingPointRange<Float> -> onVibStrengthChanged(value) },
                range = 0f..100f
            )

            /**
             * The Vibration duration
             */
            Text(text = stringResource(id = R.string.editTimeline_Vibration_duration))
            SecText(min = RangeConverter.msToS(configComponent.minDuration), max = RangeConverter.msToS(configComponent.maxDuration))
            SliderForRange(
                value = RangeConverter.msToS(configComponent.minDuration)..RangeConverter.msToS(configComponent.maxDuration),
                func = { value: ClosedFloatingPointRange<Float> -> onVibDurationChanged(value) },
                range = 0f..30f
            )
        }
    }

    /**
     * The Pause
     */
    Text(text = stringResource(id = R.string.editTimeline_Pause))
    SecText(min = RangeConverter.msToS(minPause), max = RangeConverter.msToS(maxPause))
    SliderForRange(
        value = RangeConverter.msToS(minPause)..RangeConverter.msToS(maxPause),
        func = { value: ClosedFloatingPointRange<Float> -> onPauseValueChanged(value) },
        range = 0f..30f
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SliderForRange(
    func: (ClosedFloatingPointRange<Float>) -> Unit,
    value: ClosedFloatingPointRange<Float>,
    range: ClosedFloatingPointRange<Float>
) {
    RangeSlider(
        value = (value),
        onValueChange = func,
        valueRange = range,
        onValueChangeFinished = {},
    )
}

@Composable
fun SecText(min: Float, max: Float) {
    Text(String.format("%.1f", min) + "s " + stringResource(id = R.string.editTimeline_range) + String.format("%.1f", max) + "s ")
}