package com.ispgr5.locationsimulator.presentation.editTimeline.components

import android.content.Context
import android.os.Build
import android.os.Vibrator
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RangeSlider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.sp
import com.gigamole.composescrollbars.Scrollbars
import com.gigamole.composescrollbars.config.ScrollbarsConfig
import com.gigamole.composescrollbars.config.ScrollbarsOrientation
import com.gigamole.composescrollbars.rememberScrollbarsState
import com.gigamole.composescrollbars.scrolltype.ScrollbarsScrollType
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.RangeConverter
import com.ispgr5.locationsimulator.presentation.universalComponents.ConfirmDeleteDialog
import kotlin.properties.Delegates

/**
 * The composable for showing and Editing a ConfigComponent(Sound or Vibration).
 * the corresponding functions get called if the user performed actions to edit
 */
@Composable
fun EditConfigComponent(
    configComponent: ConfigComponent?,
    onSoundValueChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onPauseValueChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onVibStrengthChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onVibDurationChanged: (ClosedFloatingPointRange<Float>) -> Unit,
    onDeleteClicked: (configComponent: ConfigComponent) -> Unit,
    onMoveLeftClicked: (configComponent: ConfigComponent) -> Unit,
    onMoveRightClicked: (configComponent: ConfigComponent) -> Unit,
    onConfigComponentNameChanged: (name: String) -> Unit,
    onCopyConfigComponent: (configComponent: ConfigComponent) -> Unit,
) {
    //so no Time line Item is selected for now
    if (configComponent == null) {
        return
    }
    //needed to show the Pause Slider separately
    var minPause by Delegates.notNull<Int>()
    var maxPause by Delegates.notNull<Int>()

    //Delete Confirm Dialog
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showStrengthNotSupportedDialog by remember { mutableStateOf(false) }


    val blackSubtitle1 = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Black)

    val scrollState = rememberScrollState()
    val scrollbarsState = rememberScrollbarsState(
        config = ScrollbarsConfig(orientation = ScrollbarsOrientation.Vertical),
        scrollType = ScrollbarsScrollType.Scroll(state = scrollState)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = { onMoveLeftClicked(configComponent) },
                            modifier = Modifier.testTag(TestTags.EDIT_MOVE_LEFT)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                                contentDescription = null
                            )
                        }
                        Text(
                            stringResource(id = R.string.TimelineMoveLeft),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(100.dp),
                            fontSize = 15.sp,
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = { onMoveRightClicked(configComponent) },
                            modifier = Modifier.testTag(TestTags.EDIT_MOVE_RIGHT)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                                contentDescription = null
                            )
                        }
                        Text(
                            text = stringResource(id = R.string.TimelineMoveRight),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(100.dp),
                            fontSize = 15.sp,
                        )
                    }
                }

                /**
                 * Sound and vibration name
                 */
                Spacer(modifier = Modifier.size(7.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ConfigComponentNameTextInput(configComponent, onConfigComponentNameChanged)
                }
            }

            Column {
                when (configComponent) {
                    is ConfigComponent.Sound -> {
                        minPause = configComponent.minPause
                        maxPause = configComponent.maxPause
                        Text(
                            text = buildAnnotatedString {
                                withStyle(MaterialTheme.typography.subtitle1.toSpanStyle()) {
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
                            value = RangeConverter.transformFactorToPercentage(configComponent.minVolume)..
                                    RangeConverter.transformFactorToPercentage(configComponent.maxVolume),
                            func = { value: ClosedFloatingPointRange<Float> ->
                                onSoundValueChanged(
                                    value
                                )
                            },
                            range = 0f..100f
                        )
                    }

                    is ConfigComponent.Vibration -> {
                        minPause = configComponent.minPause
                        maxPause = configComponent.maxPause

                        /**
                         * The Vibration Strength
                         */
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = stringResource(id = R.string.editTimeline_Vibration_Strength),
                                    style = blackSubtitle1
                                )
                                Text(
                                    RangeConverter.eightBitIntToPercentageFloat(configComponent.minStrength)
                                        .toInt().toString() + "% "
                                            + stringResource(id = R.string.editTimeline_range) + RangeConverter.eightBitIntToPercentageFloat(
                                        configComponent.maxStrength
                                    )
                                        .toInt()
                                        .toString() + "%",
                                    modifier = Modifier.testTag(TestTags.EDIT_VIB_SLIDER_STRENGTH_TEXT)
                                )
                            }
                            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                // Use the recommended method to get the Vibrator service
                                LocalContext.current.getSystemService(Vibrator::class.java)
                            } else {
                                // Use the deprecated method to get the Vibrator service
                                @Suppress("DEPRECATION")
                                LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                            }
                            if (Build.VERSION.SDK_INT < 26 || !vibrator.hasAmplitudeControl()) {
                                Button(
                                    onClick = {
                                        showStrengthNotSupportedDialog = true
                                    }, //show Confirm Dialog
                                    shape = MaterialTheme.shapes.small,
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = androidx.compose.ui.graphics.Color.Transparent,
                                        contentColor = androidx.compose.ui.graphics.Color.Red,
                                        disabledBackgroundColor = androidx.compose.ui.graphics.Color.Transparent,
                                        disabledContentColor = MaterialTheme.colors.primary.copy(
                                            alpha = ContentAlpha.disabled
                                        ),
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

                        SliderForRange(
                            modifier = Modifier.testTag(TestTags.EDIT_VIB_SLIDER_STRENGTH),
                            value = RangeConverter.eightBitIntToPercentageFloat(configComponent.minStrength)..RangeConverter.eightBitIntToPercentageFloat(
                                configComponent.maxStrength
                            ),
                            func = { value: ClosedFloatingPointRange<Float> ->
                                onVibStrengthChanged(
                                    value
                                )
                            },
                            range = 0f..100f,

                            )

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
                            value = RangeConverter.msToS(configComponent.minDuration)..RangeConverter.msToS(
                                configComponent.maxDuration
                            ),
                            func = { value: ClosedFloatingPointRange<Float> ->
                                onVibDurationChanged(
                                    value
                                )
                            },
                            range = 0f..30f,
                            modifier = Modifier.testTag(TestTags.EDIT_VIB_SLIDER_DURATION)
                        )
                    }
                }
                /**
                 * The Pause
                 */
                Text(
                    text = stringResource(id = R.string.editTimeline_Pause),
                    style = blackSubtitle1
                )
                SecText(min = RangeConverter.msToS(minPause), max = RangeConverter.msToS(maxPause))
                SliderForRange(
                    value = RangeConverter.msToS(minPause)..RangeConverter.msToS(maxPause),
                    func = { value: ClosedFloatingPointRange<Float> -> onPauseValueChanged(value) },
                    range = 0f..60f,
                    modifier = Modifier.testTag(TestTags.EDIT_SLIDER_PAUSE)
                )
            }
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = { onCopyConfigComponent(configComponent) }, //show Confirm Dialog
                        contentPadding = PaddingValues(0.dp),
                        enabled = true,
                        shape = MaterialTheme.shapes.small,
                        border = null,
                        elevation = null,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = androidx.compose.ui.graphics.Color.Transparent,
                            contentColor = MaterialTheme.colors.onSurface,
                            disabledBackgroundColor = androidx.compose.ui.graphics.Color.Transparent,
                            disabledContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
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
                            backgroundColor = androidx.compose.ui.graphics.Color.Transparent,
                            contentColor = androidx.compose.ui.graphics.Color.Red,
                            disabledBackgroundColor = androidx.compose.ui.graphics.Color.Transparent,
                            disabledContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_delete_outline_24),
                            contentDescription = null,
                        )
                    }

                }
            }
        }
        Scrollbars(state = scrollbarsState)
    }
//Dialog to confirm the deleting of an config Component
    val revShowDialog = fun() { showDeleteConfirmDialog = !showDeleteConfirmDialog }
    if (showDeleteConfirmDialog) {
        ConfirmDeleteDialog(onDismiss = revShowDialog, onConfirm = {
            onDeleteClicked(configComponent)
        })
    }

    if (showStrengthNotSupportedDialog) {
        AlertDialog(
            onDismissRequest = { showStrengthNotSupportedDialog = false },
            text = {
                Text(
                    text = stringResource(id = R.string.vibrationStrengthNotSupported),
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
fun ConfigComponentNameTextInput(
    configComponent: ConfigComponent,
    onConfigComponentNameChanged: (name: String) -> Unit
) {
    OutlinedTextField(
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground
        ),
        value = configComponent.name,
        modifier = Modifier.testTag(TestTags.EDIT_ITEM_NAME_TEXTINPUT),
        placeholder = {
            Text(text = stringResource(R.string.editTimeline_name))
        },
        onValueChange = { name ->
            onConfigComponentNameChanged(name)
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SliderForRange(
    func: (ClosedFloatingPointRange<Float>) -> Unit,
    value: ClosedFloatingPointRange<Float>,
    range: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier
) {
    RangeSlider(
        value = (value),
        onValueChange = func,
        valueRange = range,
        onValueChangeFinished = {},
        modifier = modifier
    )
}

@Composable
fun SecText(min: Float, max: Float, modifier: Modifier = Modifier) {
    Text(
        String.format(
            "%.1f",
            min
        ) + "s " + stringResource(id = R.string.editTimeline_range) + String.format(
            "%.1f",
            max
        ) + "s ", modifier = modifier
    )
}