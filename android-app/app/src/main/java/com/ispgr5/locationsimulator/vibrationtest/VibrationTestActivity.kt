package com.ispgr5.locationsimulator.vibrationtest

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gigamole.composescrollbars.Scrollbars
import com.gigamole.composescrollbars.config.ScrollbarsConfig
import com.gigamole.composescrollbars.config.ScrollbarsOrientation
import com.gigamole.composescrollbars.rememberScrollbarsState
import com.gigamole.composescrollbars.scrolltype.ScrollbarsScrollType
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val VIBRATION_DURATION_ONESHOT = 500L
const val VIBRATION_DURATION_STRENGTH_ONESHOT_MAX = 255
const val VIBRATION_DURATION_STRENGTH_ONESHOT_LOW = 100

@SuppressLint("NewApi")
private val VIBRATION_USAGE_ALARM = VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM)

class VibrationTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationSimulatorTheme(themeState = ThemeState(themeType = ThemeType.LIGHT)) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorSchemecheme.background
                ) {
                    VibrationTesterScaffold()
                }
            }
        }
    }
}

@Composable
fun VibrationTesterScaffold() {

    val vibrationTestResult = remember {
        mutableStateMapOf<String, Map<String, String>>()
    }
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        val lazyListState = rememberLazyListState()
        val scrollbarsState = rememberScrollbarsState(
            config = ScrollbarsConfig(orientation = ScrollbarsOrientation.Vertical),
            scrollType = ScrollbarsScrollType.Lazy.List.Static(state = lazyListState)
        )
        Button(modifier = Modifier.fillMaxWidth(0.8f), onClick = {
            val map = vibrationTestResult.toMap()
            Log.i("VibrationTest", Json.encodeToString(map))
            TODO("Write out to file and share?!")
        }) {
            Text("Write report")
        }
        VibrationTesterContent(lazyListState, onAddToReport = { key, value ->
            vibrationTestResult[key] = value.mapValues { it.value.toString() }
        })
        Scrollbars(state = scrollbarsState)
    }
}

@SuppressLint("NewApi")
@Composable
fun VibrationTesterContent(
    lazyListState: LazyListState, onAddToReport: (String, Map<String, Any>) -> Unit
) {
    val context = LocalContext.current
    val vibrationTestManager by remember {
        mutableStateOf(VibrationTestManager(context))
    }
    val simpleVibrator by remember {
        derivedStateOf {
            vibrationTestManager.getVibratorViaSystemService()
        }
    }
    val vibratorManagerApiS by remember {
        derivedStateOf {
            vibrationTestManager.getVibratorManagerApiS()
        }
    }
    LazyColumn(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            VibrationTestCard(
                description = "Simple vibrator via system service", parameterDescriptions = mapOf(
                    "vibrator" to "Simple",
                    "duration" to "$VIBRATION_DURATION_ONESHOT ms",
                    "strength" to "depends on platform",
                    "amplitude_control" to simpleVibrator.hasAmplitudeControl()
                ), onAddToReport = onAddToReport
            ) {
                @Suppress("DEPRECATION") simpleVibrator.vibrate(VIBRATION_DURATION_ONESHOT)
            }
        }
        item {
            VibrationTestCard(
                description = "Simple vibrator, oneshot, default strength",
                parameterDescriptions = mapOf(
                    "vibrator" to "Simple",
                    "duration" to "$VIBRATION_DURATION_ONESHOT ms",
                    "strength" to "default (${VibrationEffect.DEFAULT_AMPLITUDE})",
                    "amplitude_control" to simpleVibrator.hasAmplitudeControl()
                ),
                onAddToReport = onAddToReport
            ) {
                simpleVibrator.vibrate(
                    VibrationEffect.createOneShot(
                        VIBRATION_DURATION_ONESHOT, VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }
        }
        item {
            VibrationTestCard(
                description = "Simple vibrator, oneshot, default strength, with usage",
                parameterDescriptions = mapOf(
                    "vibrator" to "Simple",
                    "duration" to "$VIBRATION_DURATION_ONESHOT ms",
                    "strength" to "default (${VibrationEffect.DEFAULT_AMPLITUDE})",
                    "amplitude_control" to simpleVibrator.hasAmplitudeControl()
                ),
                onAddToReport = onAddToReport
            ) {
                simpleVibrator.vibrate(
                    VibrationEffect.createOneShot(
                        VIBRATION_DURATION_ONESHOT, VibrationEffect.DEFAULT_AMPLITUDE
                    ),
                    VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM),
                )
            }
        }
        item {
            HorizontalDivider()
        }
        complexEffectTests(
            "Simple vibrator, effect %s",
            vibrator = simpleVibrator,
            parameterDescriptions = mapOf(),
            vibrationAttributes = null,
            onAddToReport = onAddToReport
        )
        complexEffectTests(
            "Simple vibrator, effect %s, with usage",
            vibrator = simpleVibrator,
            parameterDescriptions = mapOf(),
            vibrationAttributes = VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM),
            onAddToReport = onAddToReport
        )
        item {
            HorizontalDivider()
        }
        vibratorManagerApiS?.let { vibratorManager ->
            val vibrators = vibrationTestManager.getVibratorsFromVibratorManager(vibratorManager)
            vibrators.forEach { (vibratorId, vibrator) ->
                complexEffectTests(
                    titlePlaceholder = "Vibrator $vibratorId, effect %s",
                    vibrator = vibrator,
                    parameterDescriptions = mapOf(),
                    vibrationAttributes = null,
                    onAddToReport = onAddToReport,
                )
                complexEffectTests(
                    titlePlaceholder = "Vibrator $vibratorId, effect %s, with usage",
                    vibrator = vibrator,
                    parameterDescriptions = mapOf(),
                    vibrationAttributes = VibrationAttributes.createForUsage(VibrationAttributes.USAGE_ALARM),
                    onAddToReport = onAddToReport,
                )
                if (vibrators.maxOf { it.key } != vibratorId) {
                    item {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@SuppressLint("InlinedApi")
fun LazyListScope.complexEffectTests(
    titlePlaceholder: String,
    vibrator: Vibrator,
    parameterDescriptions: Map<String, String>,
    vibrationAttributes: VibrationAttributes?,
    onAddToReport: (String, Map<String, Any>) -> Unit
) {
    val prefabEffects = mapOf(
        "Click" to VibrationEffect.EFFECT_CLICK,
        "Tick" to VibrationEffect.EFFECT_TICK,
        "Double Click" to VibrationEffect.EFFECT_DOUBLE_CLICK,
        "Heavy Click" to VibrationEffect.EFFECT_HEAVY_CLICK
    )
    val primitiveEffects = mapOf(
        "Primitive Click" to VibrationEffect.Composition.PRIMITIVE_CLICK,
        "Primitive Thud" to VibrationEffect.Composition.PRIMITIVE_THUD,
        "Primitive Spin" to VibrationEffect.Composition.PRIMITIVE_SPIN,
        "Primitive Quick Rise" to VibrationEffect.Composition.PRIMITIVE_QUICK_RISE,
        "Primitive Slow Rise" to VibrationEffect.Composition.PRIMITIVE_SLOW_RISE,
        "Primitive Quick Fall" to VibrationEffect.Composition.PRIMITIVE_QUICK_FALL,
        "Primitive Primitive Tick" to VibrationEffect.Composition.PRIMITIVE_TICK,
        "Primitive Primitive Low Tick" to VibrationEffect.Composition.PRIMITIVE_LOW_TICK
    )
    val isSupported = prefabEffects.mapValues {
        vibrator.areAllEffectsSupported(it.value).let { effectId ->
            when (effectId) {
                Vibrator.VIBRATION_EFFECT_SUPPORT_YES -> "Yes"
                Vibrator.VIBRATION_EFFECT_SUPPORT_NO -> "No"
                Vibrator.VIBRATION_EFFECT_SUPPORT_UNKNOWN -> "Unknown"
                else -> throw UnsupportedOperationException()
            }
        }
    } + primitiveEffects.mapValues {
        vibrator.areAllPrimitivesSupported(it.value).toString()
    }
    val oneshotMaxStrength = VibrationEffect.createOneShot(
        VIBRATION_DURATION_ONESHOT, VIBRATION_DURATION_STRENGTH_ONESHOT_MAX
    )
    val oneshotLowStrength = VibrationEffect.createOneShot(
        VIBRATION_DURATION_ONESHOT, VIBRATION_DURATION_STRENGTH_ONESHOT_LOW
    )
    val waveform = VibrationEffect.createWaveform(
        longArrayOf(0, 100, 200, 300, 400, 500), intArrayOf(50, 100, 0, 150, 200, 255), -1
    )
    val effects = prefabEffects.mapValues { VibrationEffect.createPredefined(it.value) } +
            primitiveEffects.mapValues {
                VibrationEffect.startComposition().addPrimitive(it.value).compose()
            } + mapOf(
        "Oneshot max" to oneshotMaxStrength,
        "Oneshot Low" to oneshotLowStrength,
        "Waveform" to waveform
    )
    effects.forEach { (effectName, effect) ->
        val vibrationSupported = isSupported[effectName]
        item {
            VibrationTestCard(
                description = titlePlaceholder.format(effectName),
                parameterDescriptions = parameterDescriptions + ("effect" to effectName),
                isSupported = vibrationSupported,
                onAddToReport = onAddToReport
            ) {
                when (vibrationAttributes) {
                    null -> vibrator.vibrate(effect)
                    else -> vibrator.vibrate(effect, vibrationAttributes)
                }
            }
        }
    }
}

@Composable
@Preview
fun VibrationTesterPreview() {
    VibrationTesterScaffold()
}

@Composable
fun VibrationTestCard(
    description: String,
    parameterDescriptions: Map<String, Any>,
    onAddToReport: (String, Map<String, Any>) -> Unit,
    isSupported: String? = null,
    onClickVibrate: () -> Unit,
) {
    var vibrationSuccess: Boolean? by remember {
        mutableStateOf(null)
    }
    var hasTested: Boolean by remember {
        mutableStateOf(false)
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(text = description, style = MaterialTheme.typography.titleLarge)
            isSupported?.let {
                Text("Android report support as: $it")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    parameterDescriptions.forEach {
                        Text(buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${it.key}: ")
                            }
                            append(it.value.toString())
                        })
                    }
                }

                Button(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    onClick = {
                        onClickVibrate()
                        hasTested = true
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorSchemecheme.primary)
                ) {
                    Text("Vibrate")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        vibrationSuccess = false
                        onAddToReport(
                            description, mapOf("success" to false) + parameterDescriptions
                        )
                    },
                    enabled = hasTested,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red.copy(alpha = 0.5f))
                ) {
                    Text(":(")
                }
                Text("Success: $vibrationSuccess")
                Button(
                    onClick = {
                        vibrationSuccess = true
                        onAddToReport(
                            description, mapOf("success" to true) + parameterDescriptions
                        )
                    },
                    enabled = hasTested,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Cyan.copy(alpha = 0.5f))
                ) {
                    Text(":)")
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
@SuppressLint("NewApi")
class VibrationTestManager(private val context: Context) {
    fun getVibratorManagerApiS(): VibratorManager? {
        return when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            true -> context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            else -> null
        }
    }

    fun getVibratorsFromVibratorManager(vibratorManager: VibratorManager): Map<Int, Vibrator> {
        return vibratorManager.vibratorIds.associateWith {
            vibratorManager.getVibrator(it)
        }
    }

    fun getVibratorViaSystemService(): Vibrator {
        return context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}