import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.domain.model.Sound
import com.ispgr5.locationsimulator.domain.model.Vibration
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineEvent
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineViewModel
import com.ispgr5.locationsimulator.presentation.editTimeline.RangeConverter


@Composable
fun EditTimelineScreen(
    navController: NavController,
    viewModel: EditTimelineViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    var showCustomDialogWithResult by remember { mutableStateOf(false) }

        Column() {
            Column( modifier = Modifier.padding(12.dp)){
                val textState = remember { mutableStateOf(TextFieldValue()) }
                Text(text = "Name:" , fontWeight = FontWeight.Bold )
                BasicTextField(
                    value = state.name,
                    singleLine = true,
                    onValueChange = { name -> viewModel.onEvent(EditTimelineEvent.ChangedName(name)) }
                )
                Text("Description:")
                Spacer(modifier = Modifier.size(4.dp))
                BasicTextField(
                    value = state.description,
                    onValueChange = { description -> viewModel.onEvent(EditTimelineEvent.ChangedDescription(description)) }
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            Divider(color = MaterialTheme.colors.primary, thickness = 1.dp )
            Spacer(modifier = Modifier.size(4.dp))
            
            Row{
                LazyRow(){
                    items(state.components) {
                            configComponent -> TimelineItem(configComponent, viewModel)
                    }
                }

            }

            Button( onClick = { showCustomDialogWithResult = true }){
                Text(text = "Add")
            }
            Column( modifier = Modifier.padding(12.dp)){
                EditConfigComponent( viewModel)
            }

        }

    if (showCustomDialogWithResult) {
        AddConfigComponentDialog(
            onDismiss = {
                showCustomDialogWithResult = !showCustomDialogWithResult
            },
            onNegativeClick = {
                showCustomDialogWithResult = !showCustomDialogWithResult
            },
            onSoundClicked = {
                showCustomDialogWithResult = !showCustomDialogWithResult
                viewModel.onEvent(EditTimelineEvent.AddSound)
            },
            onVibrationClicked = {
                showCustomDialogWithResult = !showCustomDialogWithResult
                viewModel.onEvent(EditTimelineEvent.AddVibration)
            }
        )
    }


}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditConfigComponent(viewModel: EditTimelineViewModel){
    /*TODO Bug doesnt get printed in the beginning*/
    if(viewModel.state.value.components.isEmpty()){
        System.out.println("isEmpty")
        return
    }
    val current = viewModel.state.value.current
    when(current){
        is Sound ->{
            Text("Volume:")
            Text( RangeConverter.EightBitIntToPercentageFloat(current.minVolume).toInt().toString() + "%"
                    + " to " + RangeConverter.EightBitIntToPercentageFloat(current.maxVolume).toInt().toString() + "%"
            )
            SliderForRange(
                value = RangeConverter.EightBitIntToPercentageFloat(current.minVolume)..RangeConverter.EightBitIntToPercentageFloat(current.maxVolume),
                func =  {value : ClosedFloatingPointRange<Float> -> viewModel.onEvent(EditTimelineEvent.ChangedSoundVolume(value))},
                range = 0f..100f
            )
            Text("Pause:")
            SecText(min = RangeConverter.MsToS(current.minPause), max = RangeConverter.MsToS(current.maxPause))
            SliderForRange(
                value = RangeConverter.MsToS(current.minPause)..RangeConverter.MsToS(current.maxPause),
                func =  {value : ClosedFloatingPointRange<Float> -> viewModel.onEvent(EditTimelineEvent.ChangedPause(value)) } ,
                range = 0f..30f
            )
        }
        is Vibration -> {
            Text("Strength:")
            Text( RangeConverter.EightBitIntToPercentageFloat(current.minStrength).toInt().toString() + "%"
            + " to " + RangeConverter.EightBitIntToPercentageFloat(current.maxStrength).toInt().toString() + "%"
            )
            SliderForRange(
                value = RangeConverter.EightBitIntToPercentageFloat(current.minStrength)..RangeConverter.EightBitIntToPercentageFloat(current.maxStrength),
                func =  {value : ClosedFloatingPointRange<Float> -> viewModel.onEvent(EditTimelineEvent.ChangedVibStrength(value))},
                range = 0f..100f
            )
            Text("Duration:")
            SecText(min = RangeConverter.MsToS(current.minDuration), max = RangeConverter.MsToS(current.maxDuration))
            SliderForRange(
                value = RangeConverter.MsToS(current.minDuration)..RangeConverter.MsToS(current.maxDuration),
                func =  {value : ClosedFloatingPointRange<Float> -> viewModel.onEvent(EditTimelineEvent.ChangedVibDuration(value))},
                range = 0f..30f
            )
            Text("Pause:")
            SecText(min = RangeConverter.MsToS(current.minPause), max = RangeConverter.MsToS(current.maxPause))
            SliderForRange(
                value = RangeConverter.MsToS(current.minPause)..RangeConverter.MsToS(current.maxPause),
                func =  {value : ClosedFloatingPointRange<Float> -> viewModel.onEvent(EditTimelineEvent.ChangedPause(value))},
                range = 0f..30f
            )
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SliderForRange(func: (ClosedFloatingPointRange<Float>) -> Unit,
                   value: ClosedFloatingPointRange<Float>,
                   range: ClosedFloatingPointRange<Float>)
{
    RangeSlider(
        value = (value),
        onValueChange = func,
        valueRange = range,
        onValueChangeFinished = {},
    )
}

@Composable
fun SecText(min : Float, max : Float){
    Text( String.format("%.1f", min) + "s to " + String.format("%.1f", max) +"s" )
}