package com.ispgr5.locationsimulator.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gigamole.composescrollbars.Scrollbars
import com.gigamole.composescrollbars.config.ScrollbarsConfig
import com.gigamole.composescrollbars.config.ScrollbarsOrientation
import com.gigamole.composescrollbars.rememberScrollbarsState
import com.gigamole.composescrollbars.scrolltype.ScrollbarsScrollType
import com.gigamole.composescrollbars.scrolltype.knobtype.ScrollbarsStaticKnobType
import com.ispgr5.locationsimulator.BuildConfig
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.presentation.util.ThemeToggle
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun OnboardingScreen(
    viewModel: OnboardingScreenViewModel = hiltViewModel(),
    themeState: MutableState<ThemeState>,
    activity: MainActivity,
    @Suppress("UNUSED_PARAMETER") navController: NavHostController,
    checkBatteryOptimizationStatus: () -> Boolean,
    batteryOptDisableFunction: () -> Unit
) {
    LocationSimulatorTheme {
        OnboardingScreenScaffold(
            themeState = themeState,
            onChangeTheme = { newThemeState ->
                viewModel.onEvent(
                    OnboardingScreenEvent.ChangedAppTheme(activity, newThemeState)
                )
            },
            checkBatteryOptimizationStatus = checkBatteryOptimizationStatus,
            batteryOptDisableFunction = batteryOptDisableFunction
        )
    }
}

@Composable
fun OnboardingScreenScaffold(
    themeState: MutableState<ThemeState>,
    onChangeTheme: (ThemeState) -> Unit,
    checkBatteryOptimizationStatus: () -> Boolean,
    batteryOptDisableFunction: () -> Unit
) {
    val scrollState = rememberScrollState()
    val scrollbarsState = rememberScrollbarsState(
        config = ScrollbarsConfig(orientation = ScrollbarsOrientation.Vertical),
        scrollType = ScrollbarsScrollType.Scroll(
            knobType = ScrollbarsStaticKnobType.Auto(), state = scrollState
        )
    )
    Scaffold { appPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(appPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.welcome_to),
                    modifier = Modifier.padding(top = 4.dp),
                    style = typography.titleMedium.copy(fontStyle = FontStyle.Italic)
                )
                Text(stringResource(R.string.app_name), style = typography.headlineLarge)
                Text(
                    stringResource(R.string.app_version, BuildConfig.VERSION_NAME),
                    style = typography.headlineSmall
                )

            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                thickness = 2.dp,
                color = colorScheme.primary
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 8.dp)
                        .verticalScroll(
                            state = scrollState, enabled = true
                        )
                ) {
                    ThemeSettingsCard(themeState, onChangeTheme)
                    BatteryOptimizationCard(
                        checkBatteryOptimizationStatus, batteryOptDisableFunction
                    )
                    VibrationTestCard()
                }
                Scrollbars(scrollbarsState)
            }
        }
    }
}

@Composable
fun BatteryOptimizationCard(
    isIgnoringBatteryOptimization: () -> Boolean, batteryOptDisableFunction: () -> Unit
) {
    var status by remember { mutableStateOf(isIgnoringBatteryOptimization()) }
    var interactionCounter by remember { mutableIntStateOf(0) }
    LaunchedEffect(interactionCounter) {
        for (counter in 0 until 3) {
            val newStatus = isIgnoringBatteryOptimization()
            if (newStatus != status) {
                status = newStatus
                break
            }
            delay(500L)
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.battery_optimization), style = typography.headlineSmall)
            when (status) {
                true -> {
                    Text(
                        text = stringResource(id = R.string.locationsimulator_is_already_exempt_from_battery_optimizations),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        tint = colorScheme.primary
                    )
                }

                else -> {
                    Text(
                        text = stringResource(R.string.battery_opt_recommendation),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = {
                            interactionCounter++
                            batteryOptDisableFunction()
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primaryContainer,
                            contentColor = colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.battery_opt_button))
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeSettingsCard(
    themeState: MutableState<ThemeState>, onChangeTheme: ((ThemeState) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Theme Settings", style = typography.headlineSmall)
            ThemeToggle(
                selectedTheme = themeState.value
            ) { newState ->
                themeState.value = newState
                onChangeTheme?.invoke(newState)
            }
        }
    }
}

@Composable
fun VibrationTestCard(
) {
    var minimumStrength by remember {
        mutableIntStateOf(1)
    }
    var minimumDuration by remember {
        mutableIntStateOf(0)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.vibration_test), style = typography.headlineSmall
                )
                Text(
                    text = stringResource(R.string.vibration_test_text),
                    textAlign = TextAlign.Center
                )

                HorizontalDivider()

                Text("Vibration Strength")
                SliderWithPreciseInput(
                    value = minimumStrength.toFloat(),
                    steps = 100,
                    range = 1f..100f,
                    label = "Strength (%)",
                    onValueChange = { value ->
                        minimumStrength = value.roundToInt()
                    }
                )

                HorizontalDivider()
                Text("Vibration Duration")
                SliderWithPreciseInput(
                    value = minimumDuration.toFloat(),
                    steps = 2000,
                    range = 1f..2000f,
                    label = "Duration (ms)"
                ) { value ->
                    minimumDuration = value.roundToInt()
                }
                HorizontalDivider()
                Button(onClick = {}) {
                    Icon(
                        Icons.Default.Vibration,
                        contentDescription = null,
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Vibrate")
                }
            }
        }
    }
}

@Composable
fun SliderWithPreciseInput(
    value: Float,
    steps: Int,
    range: ClosedFloatingPointRange<Float>,
    label: String,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Slider(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            value = value,
            valueRange = range,
            steps = steps,
            onValueChange = onValueChange
        )
        OutlinedTextField(
            modifier = Modifier
                .width(100.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            value = value.toInt().toString(),
            label = { Text(label) },

            onValueChange = { text ->
                val floatValue = text.toFloatOrNull()
                floatValue?.coerceIn(range)?.let(onValueChange)
            }
        )
    }
}

@Composable
@Preview
fun SplashScreenPreview() {
    val themeState = remember {
        mutableStateOf(ThemeState())
    }
    OnboardingScreenScaffold(themeState, {}, { false }, { })
}