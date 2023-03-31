import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.presentation.delay.DelayEvent
import com.ispgr5.locationsimulator.presentation.delay.DelayViewModel
import com.ispgr5.locationsimulator.presentation.util.Screen
import kotlinx.coroutines.delay

/**
 * The Timer
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
        while (timerRunning && (timerSeconds > 0 || timerMinutes > 0 || timerHours > 0)) {
            delay(1000)
            timerSeconds--
            if(timerSeconds<0) {
                timerSeconds = 59
                timerMinutes--
                if(timerMinutes<0) {
                    timerHours--
                    timerMinutes = 59
                }
            }
        }
        if(timerRunning) {
            viewModel.onEvent(DelayEvent.StartClicked(startServiceFunction))
            navController.navigate(Screen.RunScreen.route)
        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
    ) {
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
                onValueChange = {newVal:String -> timerHours = calculateValue(newVal) },
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
                onValueChange = {newVal:String -> timerMinutes = calculateValue(newVal) },
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
                onValueChange = {newVal:String -> timerSeconds = calculateValue(newVal) },
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

    Button(
        onClick = {timerRunning = !timerRunning},
        enabled = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
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


private fun calculateValue(value:String):Int {
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