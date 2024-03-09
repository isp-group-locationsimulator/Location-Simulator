package com.ispgr5.locationsimulator.presentation.delay

import android.os.CountDownTimer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

/**
 * The Timer Compose Element, to input and show the delay Time
 */
@Composable
fun Timer(
    initialTimerState: TimerState,
    onFinishTimer: (configurationId: Int) -> Unit,
    configurationId: Int
) {
    var timerState by remember { mutableStateOf(initialTimerState) }
    val timerRunning by remember {
        derivedStateOf {
            timerState.isRunning
        }
    }

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            //calculate the duration of the timer in milliseconds
            val timer = object : CountDownTimer(timerState.setDuration, 1000L) {

                /**
                 * Update the timer when com.ispgr5.locationsimulator.presentation.delay.Timer is running
                 */
                override fun onTick(millisUntilFinished: Long) {
                    if (timerRunning) {
                        val remainingSeconds = (millisUntilFinished / 1000)
                        timerState = timerState.copy(
                            secondsRemaining = remainingSeconds % 60,
                            hoursRemaining = (remainingSeconds / 60) % 60,
                            minutesRemaining = remainingSeconds / (60 * 60)
                        )
                    } else {
                        // cancel the timer when the stop button was pressed
                        this.cancel()
                    }
                }

                /**
                 * Go to Run Screen when com.ispgr5.locationsimulator.presentation.delay.Timer is finished
                 */
                override fun onFinish() {
                    onFinishTimer(configurationId)
                }
            }

            timer.start()
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.delay_start),
            style = MaterialTheme.typography.h4,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            textAlign = TextAlign.Center
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
    ) {
        /**
         * The input for hours
         */
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(id = R.string.TimerHours),
                style = TextStyle(fontSize = 24.sp)
            )
            Button(
                onClick = {
                    timerState = timerState.addHours(1)
                },
                shape = CircleShape,
                enabled = !timerRunning,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_keyboard_double_arrow_up_24),
                    contentDescription = null,
                )
            }
            TextField(
                value = timerState.stringHours(),
                onValueChange = { newVal: String ->
                    when (val intVal = newVal.toIntOrNull()) {
                        null -> {}
                        else -> timerState =
                            timerState.copy(setHours = intVal.coerceIn(0, 24).toLong())
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.padding(horizontal = 16.dp),
                readOnly = timerRunning,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                ),
            )
            Button(
                onClick = {
                    timerState = timerState.addHours(-1)
                },
                shape = CircleShape,
                enabled = !timerRunning,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_keyboard_double_arrow_down_24),
                    contentDescription = null
                )
            }
        }

        /**
         * The input for minutes
         */
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(id = R.string.TimerMinutes),
                style = TextStyle(fontSize = 24.sp)
            )
            Button(
                onClick = {
                    timerState = timerState.addMinutes(1)
                },
                shape = CircleShape,
                enabled = !timerRunning,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_keyboard_double_arrow_up_24),
                    contentDescription = null,
                )
            }
            TextField(
                value = timerState.stringMinutes(),
                onValueChange = { newVal: String ->
                    when (val intVal = newVal.toIntOrNull()) {
                        null -> {}
                        else -> timerState =
                            timerState.copy(setMinutes = intVal.coerceIn(0, 59).toLong())
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                readOnly = timerRunning,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )
            )
            Button(
                onClick = {
                    timerState = timerState.addMinutes(-1)
                },
                shape = CircleShape,
                enabled = !timerRunning,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_keyboard_double_arrow_down_24),
                    contentDescription = null,
                )
            }
        }

        /**
         * The input for hours
         */
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(id = R.string.TimerSeconds),
                style = TextStyle(fontSize = 24.sp)
            )
            Button(
                onClick = {
                    timerState = timerState.addSeconds(1)
                },
                shape = CircleShape,
                enabled = !timerRunning,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_keyboard_double_arrow_up_24),
                    contentDescription = null,
                )
            }
            TextField(
                value = timerState.stringSeconds(),
                onValueChange = { newVal: String ->
                    when (val intVal = newVal.toIntOrNull()) {
                        null -> {}
                        else -> timerState =
                            timerState.copy(setHours = intVal.coerceIn(0, 59).toLong())
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                readOnly = timerRunning,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                )
            )
            Button(
                onClick = {
                    timerState = timerState.addSeconds(-1)
                },
                shape = CircleShape,
                enabled = !timerRunning,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_keyboard_double_arrow_down_24),
                    contentDescription = null,
                )
            }
        }
    }

    /**
     * The button to start or stop the timer
     */
    Button(
        onClick = { timerState = timerState.copy(isRunning = !timerState.isRunning) },
        enabled = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag(TestTags.DELAY_START_BUTTON)
    ) {
        if (timerRunning) {
            Text(
                text = stringResource(id = R.string.run_stop),
                style = TextStyle(fontSize = 40.sp)
            )
        } else {
            Text(
                text = stringResource(id = R.string.delay_btn_start),
                style = TextStyle(fontSize = 40.sp)
            )
        }
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
    val setHours: Long = 0,
    val setMinutes: Long = 0,
    val setSeconds: Long = 0,
    val hoursRemaining: Long = 0,
    val minutesRemaining: Long = 0,
    val secondsRemaining: Long = 0
) {
    val setDuration
        get() =
            listOf(
                (setHours * 1000 * 60 * 60),
                (setMinutes * 1000 * 60),
                (setSeconds * 1000)
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
}