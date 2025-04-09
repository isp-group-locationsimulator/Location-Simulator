package com.ispgr5.locationsimulator.presentation.delay

import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.network.ClientHandler
import com.ispgr5.locationsimulator.network.Commands
import com.ispgr5.locationsimulator.presentation.ChosenRole
import com.ispgr5.locationsimulator.presentation.previewData.AppPreview
import com.ispgr5.locationsimulator.presentation.previewData.PreviewData
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme

private const val TAG = "DelayTimer"

private fun onStartVibration(
    configurationId: Int,
    onFinishTimer: (configurationId: Int) -> Unit,
    timerState: MutableState<TimerState>,
    countDownTimer: CountDownTimer?
) {
    timerState.value = timerState.value.reset(false)
    countDownTimer?.cancel()
    onFinishTimer(configurationId)
}

private class DelayCountdownTimer(
    millisInFuture: Long,
    val timerState: MutableState<TimerState>,
    val onStartVibration: (CountDownTimer) -> Unit
) : CountDownTimer(millisInFuture, 1000L) {
    /**
     * Update the timer when com.ispgr5.locationsimulator.presentation.delay.Timer is running
     */
    override fun onTick(millisUntilFinished: Long) {
        if (timerState.value.isRunning) {
            val remainingSeconds = (millisUntilFinished / 1000)
            timerState.value = timerState.value.copy(
                secondsRemaining = remainingSeconds.mod(60L),
                hoursRemaining = remainingSeconds.div(3600L),
                minutesRemaining = remainingSeconds.mod(3600L).div(60)
            )
        } else {
            // cancel the timer when the stop button was pressed
            this.cancel()
        }
    }

    /**
     * Go to Run Screen when Timer is finished
     */
    override fun onFinish() {
        Log.i(TAG, "Timer is elapsed, running? ${timerState.value.isRunning}")
        if (timerState.value.isRunning) {
            onStartVibration(this)
        }
    }
}

/**
 * The Timer Compose Element, to input and show the delay Time
 */
@Composable
fun DelayTimer(
    timerState: MutableState<TimerState>,
    chosenRole: ChosenRole,
    onFinishTimer: (configurationId: Int) -> Unit,
    onTrainerTimerStart: (Long, Long, Long) -> Unit,
    configurationId: Int
) {

    val timerRunning by remember {
        derivedStateOf {
            timerState.value.isRunning
        }
    }
    var countDownTimer: DelayCountdownTimer? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            //calculate the duration of the timer in milliseconds
            countDownTimer = DelayCountdownTimer(
                timerState.value.setDurationInMillis,
                timerState = timerState,
                onStartVibration = {
                    onStartVibration(configurationId, onFinishTimer, timerState, it)
                })

            countDownTimer?.start()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.delay_start),
                style = MaterialTheme.typography.headlineMedium,
                color = colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                textAlign = TextAlign.Center
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()
        ) {
            TimerFlipper(titleText = R.string.TimerHours,
                stringValue = timerState.value.stringHours(),
                timerRunning = timerRunning,
                onIncrement = { timerState.value = timerState.value.addHours(1) },
                onDecrement = { timerState.value = timerState.value.addHours(-1) }
            ) {
                timerState.value = timerState.value.copy(setHours = it.coerceIn(0, 24))
            }

            TimerFlipper(titleText = R.string.TimerMinutes,
                stringValue = timerState.value.stringMinutes(),
                timerRunning = timerRunning,
                onIncrement = { timerState.value = timerState.value.addMinutes(1) },
                onDecrement = { timerState.value = timerState.value.addMinutes(-1) }) {
                timerState.value = timerState.value.copy(setMinutes = it.coerceIn(0, 59))
            }


            TimerFlipper(
                titleText = R.string.TimerSeconds,
                stringValue = timerState.value.stringSeconds(),
                timerRunning = timerRunning,
                onIncrement = { timerState.value = timerState.value.addSeconds(1) },
                onDecrement = { timerState.value = timerState.value.addSeconds(-1) }
            ) {
                timerState.value = timerState.value.copy(setSeconds = it.coerceIn(0, 59))
            }
        }

        /**
         * The button to start or stop the timer
         */
        val buttonText = when {
            timerState.value.isZero() -> stringResource(id = R.string.start_now)
            timerRunning -> stringResource(id = R.string.stop_timer)
            else -> stringResource(id = R.string.delay_btn_start)
        }
        Button(
            onClick = {
                if(chosenRole == ChosenRole.TRAINER) {
                    onTrainerTimerStart(timerState.value.setHours, timerState.value.setMinutes, timerState.value.setSeconds)
                }
                else {
                    when {
                        timerState.value.isZero() -> onStartVibration(
                            configurationId = configurationId,
                            onFinishTimer = onFinishTimer,
                            timerState = timerState,
                            countDownTimer = countDownTimer
                        )

                        timerState.value.isRunning -> {
                            timerState.value =
                                timerState.value.reset(inhibitStart = true)
                            ClientHandler.sendToClients(Commands.IS_IDLE)
                        }

                        else -> {
                            timerState.value = timerState.value.copy(
                                isRunning = true, inhibitStart = false, remoteConfigStr = null
                            )
                            ClientHandler.sendToClients(
                                Commands.formatTimerState(
                                    timerState.value.setHours,
                                    timerState.value.setMinutes,
                                    timerState.value.setSeconds
                                )
                            )
                        }
                    }
                }
            },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag(TestTags.DELAY_START_BUTTON)
        ) {
            Text(
                text = buttonText, style = TextStyle(fontSize = 40.sp)
            )
        }


        if (timerRunning) {
            Button(
                onClick = {
                    onStartVibration(configurationId, onFinishTimer, timerState, countDownTimer)
                }, colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = colorScheme.secondary
                )

            ) {
                Text(
                    stringResource(id = R.string.start_now),
                    style = TextStyle(fontSize = 30.sp),
                    color = colorScheme.onSecondary
                )
            }
        }
    }
}

@Composable
fun RowScope.TimerFlipper(
    @StringRes titleText: Int,
    stringValue: String,
    timerRunning: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onSetValue: (Long) -> Unit
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)
) {
    Text(
        text = stringResource(id = titleText),
        color = colorScheme.onBackground,
        style = TextStyle(fontSize = 20.sp)
    )
    Button(
        onClick = onIncrement,
        shape = CircleShape,
        enabled = !timerRunning,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_keyboard_double_arrow_up_24),
            contentDescription = null,
        )
    }
    TextField(
        value = stringValue,
        onValueChange = { newVal: String ->
            when (val longVal = newVal.toLongOrNull()) {
                null -> {}
                else -> onSetValue(longVal)
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.padding(horizontal = 16.dp),
        readOnly = timerRunning,
        textStyle = TextStyle(
            textAlign = TextAlign.Center, fontSize = 30.sp
        ),
    )
    Button(
        onClick = onDecrement,
        shape = CircleShape,
        enabled = !timerRunning,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_keyboard_double_arrow_down_24),
            contentDescription = null
        )
    }
}

/**
 * maps a string Input to a Long between 0 and 59, with 0 as the default/error value
 */
fun calculateTimerValue(value: String): Long {
    var res: Long
    try {
        res = value.toLong()
        if (res < 0) {
            res = 0
        } else if (res > 59) {
            res = 59
        }
    } catch (e: NumberFormatException) {
        res = 0
    }
    return res
}

data class TimerState(
    val isRunning: Boolean = false,
    val inhibitStart: Boolean = false,
    val remoteConfigStr: String? = null,
    val setHours: Long = 0,
    val setMinutes: Long = 0,
    val setSeconds: Long = 0,
    val hoursRemaining: Long = 0,
    val minutesRemaining: Long = 0,
    val secondsRemaining: Long = 0
) {
    val setDurationInMillis
        get() = listOf(
            (setHours * 1000 * 60 * 60), (setMinutes * 1000 * 60), (setSeconds * 1000)
        ).sum()

    fun addHours(amount: Int) = this.copy(setHours = (setHours + amount).coerceIn(0, 24))
    fun addMinutes(amount: Int) = this.copy(setMinutes = (setMinutes + amount).coerceIn(0, 59))
    fun addSeconds(amount: Int) = this.copy(setSeconds = (setSeconds + amount).coerceIn(0, 59))

    fun stringHours() = when (isRunning) {
        true -> hoursRemaining
        else -> setHours
    }.toString()

    fun stringMinutes() = when (isRunning) {
        true -> minutesRemaining
        else -> setMinutes
    }.toString()

    fun stringSeconds() = when (isRunning) {
        true -> secondsRemaining
        else -> setSeconds
    }.toString()

    fun isZero(): Boolean = setDurationInMillis == 0L
    fun reset(inhibitStart: Boolean = false): TimerState {
        Log.i("TAG", "cancelled timer, inhibit=$inhibitStart")
        return this.copy(
            isRunning = false,
            secondsRemaining = 0L,
            minutesRemaining = 0L,
            hoursRemaining = 0L,
            inhibitStart = inhibitStart
        )
    }
}

@Composable
@AppPreview
fun DelayTimerStoppedPreview() {
    val timerState = remember {
        mutableStateOf(PreviewData.delayScreenInitialTimerState)
    }
    LocationSimulatorTheme {
        DelayTimer(
            timerState = timerState,
            chosenRole = ChosenRole.STANDALONE,
            onFinishTimer = {},
            onTrainerTimerStart = fun(_: Long, _: Long, _: Long) { },
            configurationId = PreviewData.previewConfigurations.first().id!!
        )
    }
}

@Composable
@AppPreview
fun DelayTimerRunningPreview() {
    val timerState = remember {
        mutableStateOf(PreviewData.delayScreenRunningTimerState)
    }
    LocationSimulatorTheme {
        DelayTimer(
            timerState = timerState,
            chosenRole = ChosenRole.STANDALONE,
            onFinishTimer = {},
            onTrainerTimerStart = fun(_: Long, _: Long, _: Long) { },
            configurationId = PreviewData.previewConfigurations.first().id!!
        )
    }
}