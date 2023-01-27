import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.RangeSlider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.domain.model.Sound
import com.ispgr5.locationsimulator.domain.model.Vibration
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineEvent
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineViewModel
import com.ispgr5.locationsimulator.presentation.select.SelectEvent
import com.ispgr5.locationsimulator.presentation.select.SelectViewModel



@Composable
fun EditTimelineScreen(
    navController: NavController,
    viewModel: EditTimelineViewModel = hiltViewModel()
) {
    val state = viewModel.state.value


        Column() {
            Column( modifier = Modifier.padding(12.dp)){
                Text(text = state.name , fontWeight = FontWeight.Bold)
                Text(text = state.description)
            }
            LazyRow(){
                items(state.components) { configComponent -> TimelineItem(configComponent, viewModel) }
            }
            Button( onClick = { viewModel.onEvent(EditTimelineEvent.AddSound)}){
                Text(text = "Add Sound")
            }
            Column( modifier = Modifier.padding(12.dp)){
                EditConfigComponent( viewModel)
            }

        }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditConfigComponent(viewModel: EditTimelineViewModel){
    if(viewModel.state.value.components.isEmpty()){
        return
    }
    val current = viewModel.state.value.current
    when(current){
        is Sound ->{
            Text("Volume:")
            SliderForRange(
                value = current.minVolume.toFloat()..current.maxVolume.toFloat(),
                func =  {value : ClosedFloatingPointRange<Float> -> viewModel.onEvent(EditTimelineEvent.ChangedSoundVolume(value))}
            )
            Text("Pause:")
            SliderForRange(
                value = current.minPause.toFloat()..current.maxPause.toFloat(),
                func =  {value : ClosedFloatingPointRange<Float> -> viewModel.onEvent(EditTimelineEvent.ChangedPause(value))}
            )
        }
        is Vibration -> {
            Text("Strength:")
            SliderForRange(
                value = current.minStrength.toFloat()..current.maxStrength.toFloat(),
                func =  {value : ClosedFloatingPointRange<Float> -> viewModel.onEvent(EditTimelineEvent.ChangedVibStrength(value))}
            )
            Text("Duration:")
            SliderForRange(
                value = current.minDuration.toFloat()..current.maxDuration.toFloat(),
                func =  {value : ClosedFloatingPointRange<Float> -> viewModel.onEvent(EditTimelineEvent.ChangedVibDuration(value))}
            )
            Text("Pause:")
            SliderForRange(
                value = current.minPause.toFloat()..current.maxPause.toFloat(),
                func =  {value : ClosedFloatingPointRange<Float> -> viewModel.onEvent(EditTimelineEvent.ChangedPause(value))}
            )
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SliderForRange(func: (ClosedFloatingPointRange<Float>) -> Unit, value: ClosedFloatingPointRange<Float>){
    Text(value.toString())
    RangeSlider(
        value = (value),
        onValueChange = func,
        valueRange = 0f..100f,
        onValueChangeFinished = {},
    )
}