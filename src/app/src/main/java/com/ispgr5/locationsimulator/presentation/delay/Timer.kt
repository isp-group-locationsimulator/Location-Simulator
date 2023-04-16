import android.os.CountDownTimer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.presentation.delay.DelayEvent
import com.ispgr5.locationsimulator.presentation.delay.DelayViewModel
import com.ispgr5.locationsimulator.presentation.util.Screen

/**
 * The Timer Compose Element, to input and show the delay Time
*/
@Composable
fun Timer(
    viewModel: DelayViewModel,
    startServiceFunction: (List<ConfigComponent>, Boolean) -> Unit,
    navController: NavController
) {
    var timerSeconds by remember { mutableStateOf(0) }
    var timerMinutes by remember { mutableStateOf(0) }
    var timerHours by remember { mutableStateOf(0) }
    var timerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(timerRunning) {
        if(timerRunning) {
            //calculate the duration of the timer in milliseconds
            val duration =
                (timerHours * 1000 * 60 * 60) + (timerMinutes * 1000 * 60) + (timerSeconds * 1000)

            val timer = object : CountDownTimer(duration.toLong(), 1000L) {

                /**
                 * Update the timer when Timer is running
                 */
                override fun onTick(millisUntilFinished: Long) {
                    if (timerRunning) {
                        val remainingSeconds = (millisUntilFinished / 1000).toInt()
                        timerSeconds = remainingSeconds % 60
                        timerMinutes = (remainingSeconds / 60) % 60
                        timerHours = remainingSeconds / (60 * 60)
                    } else {
                        // cancel the timer when the stop button was pressed
                        this.cancel()
                    }
                }

                /**
                 * Go to Run Screen when Timer is finished
                 */
                override fun onFinish() {
                    viewModel.onEvent(DelayEvent.StartClicked(startServiceFunction))
                    navController.navigate(Screen.RunScreen.route)
                }
            }

            timer.start()
        }
    }

    //Timer Input

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
                onClick = {if(timerHours<59){timerHours++}},
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
                value = timerHours.toString(),
                onValueChange = {newVal:String -> timerHours = calculateTimerValue(newVal) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.padding(horizontal = 16.dp),
                readOnly = timerRunning,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp
                ),
            )
            Button(
                onClick = {if(timerHours>0){timerHours--}},
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
                onClick = {if(timerMinutes<59){timerMinutes++}},
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
                value = timerMinutes.toString(),
                onValueChange = {newVal:String -> timerMinutes = calculateTimerValue(newVal) },
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
                onClick = {if(timerMinutes>0){timerMinutes--}},
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
                onClick = {if(timerSeconds<59){timerSeconds++}},
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
                value = timerSeconds.toString(),
                onValueChange = {newVal:String -> timerSeconds = calculateTimerValue(newVal) },
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
                onClick = {if(timerSeconds>0){timerSeconds--}},
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
        onClick = {timerRunning = !timerRunning},
        enabled = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag(TestTags.DELAY_START_BUTTON)
    ) {
        if(timerRunning) {
            Text(
                text = stringResource(id = R.string.run_stop),
                style = TextStyle(fontSize = 40.sp)
            )
        }
        else {
            Text(
                text = stringResource(id = R.string.delay_btn_start),
                style = TextStyle(fontSize = 40.sp)
            )
        }
    }
}

/**
 * maps a string Input to a int between 0 and 59, with 0 as the default/error value
 */
fun calculateTimerValue(value:String):Int {
    var res:Int
    try {
        res = value.toInt()
        if(res < 0) {
            res = 0
        } else if(res > 59) {
            res = 59
        }
    } catch (e:NumberFormatException) {
        res = 0
    }
    return res
}