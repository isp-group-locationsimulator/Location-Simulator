import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineEvent
import com.ispgr5.locationsimulator.presentation.editTimeline.EditTimelineViewModel



@Composable
fun TimelineItem(
    configItem : ConfigComponent,
    viewModel: EditTimelineViewModel
){

    if(viewModel.state.value.current == configItem){
        //draw selected timeline item
        Card( elevation = 4.dp, backgroundColor = Color.Gray, modifier = Modifier.padding(5.dp).border(2.dp, Color.Red), shape = RoundedCornerShape(20) ) {
            Row() {
                Icon(
                    painter = painterResource(id = R.drawable.audionouse2),
                    contentDescription = null
                )
                Text(text = "Current")
            }
        }
    }else{
        //draw not selected timeline item
        Card( elevation = 4.dp, backgroundColor = Color.Gray, modifier = Modifier.padding(5.dp)
            .clickable { viewModel.onEvent(EditTimelineEvent.SelectedTimelineItem(configItem))} ) {
            Row() {
                Icon(
                    painter = painterResource(id = R.drawable.audionouse2),
                    contentDescription = null
                )
                Text(text = "SoundTest")
            }
        }
    }
    }