package com.ispgr5.locationsimulator.presentation.editTimeline.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.gigamole.composescrollbars.Scrollbars
import com.gigamole.composescrollbars.config.ScrollbarsConfig
import com.gigamole.composescrollbars.config.ScrollbarsOrientation
import com.gigamole.composescrollbars.rememberScrollbarsState
import com.gigamole.composescrollbars.scrolltype.ScrollbarsScrollType
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.RangeConverter
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineEventHandlers
import com.ispgr5.locationsimulator.presentation.previewData.LocalesPreview
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData
import com.ispgr5.locationsimulator.presentation.previewData.ThemePreview
import com.ispgr5.locationsimulator.presentation.universalComponents.ConfirmDeleteDialog
import com.ispgr5.locationsimulator.presentation.util.vibratorHasAmplitudeControlAndReason
import com.ispgr5.locationsimulator.ui.theme.DISABLED_ALPHA
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import java.util.Locale

/**
 * The composable for showing and Editing a ConfigComponent(Sound or Vibration).
 * the corresponding functions get called if the user performed actions to edit
 */
@Composable
fun EditConfigComponent(
    configComponent: ConfigComponent?,
    editTimelineEventHandlers: EditTimelineEventHandlers?,
    vibrationSupportHintMode: VibrationSupportHintMode = VibrationSupportHintMode.AUTOMATIC,
    isFirstComponent: Boolean?,
    isLastComponent: Boolean?
) {
    //so no Time line Item is selected for now
    if (configComponent == null) {
        return
    }

    //Delete Confirm Dialog
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showStrengthNotSupportedDialog by remember { mutableStateOf(false) }

    val blackSubtitle1 = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)

    val scrollState = rememberScrollState()
    val scrollbarsState = rememberScrollbarsState(
        config = ScrollbarsConfig(orientation = ScrollbarsOrientation.Vertical),
        scrollType = ScrollbarsScrollType.Scroll(state = scrollState)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 2.dp)
                .padding(horizontal = 8.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .testTag(TestTags.EDIT_MOVE_LEFT),
                    contentPadding = PaddingValues(horizontal = 1.dp),
                    enabled = isFirstComponent == false,
                    onClick = {
                        editTimelineEventHandlers?.onMoveLeftClicked?.invoke(configComponent)
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null
                    )
                    Text(stringResource(id = R.string.TimelineMoveLeft))
                }

                Spacer(Modifier.weight(0.2f))

                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .testTag(TestTags.EDIT_MOVE_RIGHT),
                    contentPadding = PaddingValues(horizontal = 1.dp),
                    enabled = isLastComponent == false,
                    onClick = {
                        editTimelineEventHandlers?.onMoveRightClicked?.invoke(configComponent)
                    },
                ) {
                    Text(stringResource(id = R.string.TimelineMoveRight))
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                        contentDescription = null
                    )
                }
            }

            ConfigComponentNameTextInput(configComponent, onConfigComponentNameChanged = {
                editTimelineEventHandlers?.onConfigComponentNameChanged?.invoke(it)
            })


            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)) {
                when (configComponent) {
                    is ConfigComponent.Sound -> {
                        SoundParameters(configComponent, blackSubtitle1, editTimelineEventHandlers)
                    }

                    is ConfigComponent.Vibration -> {
                        VibrationParameters(
                            vibrationSupportHintMode,
                            configComponent,
                            onShowStrengthNotSupportedDialog = {
                                showStrengthNotSupportedDialog = it
                            },
                            editTimelineEventHandlers = editTimelineEventHandlers,
                            blackSubtitle1 = blackSubtitle1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {
                        editTimelineEventHandlers?.onCopyConfigComponent?.invoke(
                            configComponent
                        )
                    }, //show Confirm Dialog
                    contentPadding = PaddingValues(0.dp),
                    enabled = true,
                    shape = MaterialTheme.shapes.small,
                    border = null,
                    elevation = null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = colorScheme.onSurface
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.duplicate_icon_24),
                        contentDescription = null,
                    )
                }
                Button(
                    onClick = { showDeleteConfirmDialog = true }, //show Confirm Dialog
                    contentPadding = PaddingValues(0.dp),
                    enabled = true,
                    shape = MaterialTheme.shapes.small,
                    border = null,
                    elevation = null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = colorScheme.error
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_delete_outline_24),
                        contentDescription = null,
                    )
                }

            }
        }
        Scrollbars(state = scrollbarsState)
    }

//Dialog to confirm the deleting of an config Component
    val revShowDialog = fun() { showDeleteConfirmDialog = !showDeleteConfirmDialog }
    if (showDeleteConfirmDialog) {
        ConfirmDeleteDialog(onDismiss = revShowDialog) {
            editTimelineEventHandlers?.onDeleteClicked?.invoke(configComponent)
        }
    }

    if (showStrengthNotSupportedDialog) {
        AlertDialog(
            onDismissRequest = { showStrengthNotSupportedDialog = false },
            text = {
                val (_, reasonStringRes) = LocalContext.current.vibratorHasAmplitudeControlAndReason

                Text(
                    text = buildString {
                        append(stringResource(id = R.string.vibrationStrengthNotSupported))
                        if (reasonStringRes != null) {
                            append(" ")
                            append(stringResource(id = reasonStringRes))
                        }
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { showStrengthNotSupportedDialog = false }) {
                    Text(text = stringResource(id = R.string.DialogConfirmation))
                }
            }
        )
    }
}

@Composable
private fun PauseEditor(
    blackSubtitle1: TextStyle,
    editTimelineEventHandlers: EditTimelineEventHandlers?,
    getMinPause: () -> Int,
    getMaxPause: () -> Int,
    //setPause: (ClosedFloatingPointRange<Float>) -> Unit,
) {
    /**
     * The Pause
     */
    Text(
        text = stringResource(id = R.string.editTimeline_Pause),
        style = blackSubtitle1
    )
    SecText(
        min = RangeConverter.msToS(getMinPause()), max =
        RangeConverter.msToS(getMaxPause())
    )
    SliderForRange(
        modifier = Modifier.testTag(TestTags.EDIT_SLIDER_PAUSE),
        onValueChange = {
            editTimelineEventHandlers?.onPauseValueChanged?.invoke(it)
        },
        value = RangeConverter.msToS(getMinPause())..RangeConverter.msToS(getMaxPause()),
        range = 0f..60f
    )
}

@Composable
private fun VibrationParameters(
    vibrationSupportHintMode: VibrationSupportHintMode,
    configComponent: ConfigComponent.Vibration,
    onShowStrengthNotSupportedDialog: (Boolean) -> Unit,
    editTimelineEventHandlers: EditTimelineEventHandlers?,
    blackSubtitle1: TextStyle
) {
    val (hasAmplitudeControl, _) = when (vibrationSupportHintMode) {
        VibrationSupportHintMode.ENFORCED -> false to null
        VibrationSupportHintMode.SUPPRESSED -> true to null
        else -> LocalContext.current.vibratorHasAmplitudeControlAndReason
    }

    /**
     * The Vibration Strength
     */
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = buildAnnotatedString {
                withStyle(
                    MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                        .toSpanStyle()
                ) {
                    append(stringResource(id = R.string.editTimeline_Vibration_Strength))
                }
                if (hasAmplitudeControl) {
                    append("\n")
                    val lowerBound =
                        RangeConverter.eightBitIntToPercentageFloat(
                            configComponent.minStrength
                        ).toInt()
                    val upperBound =
                        RangeConverter.eightBitIntToPercentageFloat(
                            configComponent.maxStrength
                        ).toInt()
                    append(lowerBound.toString())
                    append("% ")
                    append(stringResource(id = R.string.editTimeline_range))
                    append(upperBound.toString())
                    append("%")
                }
            }
        )
        if (!hasAmplitudeControl) {

            Button(
                onClick = {
                    onShowStrengthNotSupportedDialog(true)
                },
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = colorScheme.error,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = colorScheme.error.copy(alpha = DISABLED_ALPHA)
                ),
                elevation = null
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_info_24),
                    contentDescription = null,
                )
            }
        }
    }
    if (hasAmplitudeControl) {
        SliderForRange(
            modifier = Modifier.testTag(TestTags.EDIT_VIB_SLIDER_STRENGTH),
            value = RangeConverter.eightBitIntToPercentageFloat(
                configComponent.minStrength
            )..RangeConverter.eightBitIntToPercentageFloat(
                configComponent.maxStrength
            ),
            onValueChange = {
                editTimelineEventHandlers?.onVibStrengthChanged?.invoke(it)
            },
            range = 0f..100f
        )
    }


    /**
     * The Vibration duration
     */
    Text(
        text = stringResource(id = R.string.editTimeline_Vibration_duration),
        style = blackSubtitle1
    )

    SecText(
        min = RangeConverter.msToS(configComponent.minDuration),
        max = RangeConverter.msToS(configComponent.maxDuration)
    )
    SliderForRange(
        modifier = Modifier.testTag(TestTags.EDIT_VIB_SLIDER_DURATION),
        enabled = hasAmplitudeControl,
        onValueChange = {
            editTimelineEventHandlers?.onVibDurationChanged?.invoke(it)
        },
        value = RangeConverter.msToS(configComponent.minDuration)..RangeConverter.msToS(
            configComponent.maxDuration
        ),
        range = 0f..30f
    )

    PauseEditor(blackSubtitle1, editTimelineEventHandlers,
        getMaxPause = { configComponent.maxPause },
        getMinPause = { configComponent.minPause })
}

@Composable
private fun SoundParameters(
    configComponent: ConfigComponent.Sound,
    blackSubtitle1: TextStyle,
    editTimelineEventHandlers: EditTimelineEventHandlers?
) {
    Text(
        text = buildAnnotatedString {
            withStyle(MaterialTheme.typography.titleMedium.toSpanStyle()) {
                withStyle(SpanStyle(fontWeight = FontWeight.Black)) {
                    append(stringResource(R.string.sound_filename))

                }
                append(" ")
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) {
                    append(configComponent.source)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
    /**
     * Volume
     */
    Text(
        text = stringResource(id = R.string.editTimeline_SoundVolume),
        style = blackSubtitle1
    )
    Text(
        RangeConverter.transformFactorToPercentage(configComponent.minVolume)
            .toInt().toString() + "% "
                + stringResource(id = R.string.editTimeline_range) + RangeConverter.transformFactorToPercentage(
            configComponent.maxVolume
        )
            .toInt()
            .toString() + "% "
    )
    SliderForRange(
        onValueChange = {
            editTimelineEventHandlers?.onSoundValueChanged?.invoke(it)
        },
        value = RangeConverter.transformFactorToPercentage(configComponent.minVolume)..
                RangeConverter.transformFactorToPercentage(configComponent.maxVolume),
        range = 0f..100f
    )

    PauseEditor(blackSubtitle1, editTimelineEventHandlers,
        getMaxPause = { configComponent.maxPause },
        getMinPause = { configComponent.minPause })
}

@Composable
fun ConfigComponentNameTextInput(
    configComponent: ConfigComponent,
    onConfigComponentNameChanged: (name: String) -> Unit
) {
    OutlinedTextField(
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            color = colorScheme.onBackground
        ),
        value = configComponent.name,
        modifier = Modifier.testTag(TestTags.EDIT_ITEM_NAME_TEXTINPUT),
        label = {
            Text(text = stringResource(R.string.editTimeline_name))
        },
        onValueChange = { name ->
            onConfigComponentNameChanged(name)
        }
    )
}

@Composable
fun SliderForRange(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    value: ClosedFloatingPointRange<Float>,
    range: ClosedFloatingPointRange<Float>
) {
    RangeSlider(
        value = (value),
        onValueChange = onValueChange,
        enabled = enabled,
        valueRange = range,
        onValueChangeFinished = {},
        modifier = modifier
    )
}

@Composable
fun SecText(min: Float, max: Float, modifier: Modifier = Modifier) {
    Text(
        String.format(
            Locale.US,
            "%.1f",
            min
        ) + "s " + stringResource(id = R.string.editTimeline_range) + String.format(
            Locale.US,
            "%.1f",
            max
        ) + "s ", modifier = modifier
    )
}


enum class VibrationSupportHintMode {
    AUTOMATIC,
    SUPPRESSED,
    ENFORCED
}

@LocalesPreview
@ThemePreview
@Composable
fun EditVibrationPreview() {
    LocationSimulatorTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = colorScheme.background) {
            EditConfigComponent(
                configComponent = PreviewData.defaultVibration,
                editTimelineEventHandlers = null,
                vibrationSupportHintMode = VibrationSupportHintMode.SUPPRESSED,
                isFirstComponent = true,
                isLastComponent = true
            )
        }
    }
}

@LocalesPreview
@ThemePreview
@Composable
fun EditVibrationUnsupportedVibrationPreview() {
    LocationSimulatorTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = colorScheme.background) {
            EditConfigComponent(
                configComponent = PreviewData.defaultVibration,
                editTimelineEventHandlers = null,
                vibrationSupportHintMode = VibrationSupportHintMode.ENFORCED,
                isFirstComponent = true,
                isLastComponent = false
            )
        }
    }
}


@LocalesPreview
@ThemePreview
@Composable
fun EditSoundPreview() {
    LocationSimulatorTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = colorScheme.background) {
            EditConfigComponent(
                configComponent = PreviewData.defaultSound,
                editTimelineEventHandlers = null,
                isFirstComponent = true,
                isLastComponent = false
            )
        }

    }
}