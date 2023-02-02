import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    val current = viewModel.state.value.components[ viewModel.state.value.currentTimelineIndex]
    if(current === configItem){
        //draw selected timeline item
        Card( elevation = 15.dp, backgroundColor = MaterialTheme.colors.surface, modifier = Modifier.padding(6.dp).border(2.dp, MaterialTheme.colors.primary, RoundedCornerShape(10))) {
            Row(verticalAlignment = Alignment.CenterVertically,  modifier = Modifier.padding(4.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.audionouse2),
                    contentDescription = null
                )
                Text(text = configItem.javaClass.simpleName)
            }
        }
    }else{
        //draw not selected timeline item
        Card( elevation = 10.dp, backgroundColor = MaterialTheme.colors.surface, modifier = Modifier.padding(6.dp)
            .clickable { viewModel.onEvent(EditTimelineEvent.SelectedTimelineItem(configItem))} ) {
            Row( verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp) ) {
                Icon(
                    painter = painterResource(id = R.drawable.audionouse2),
                    contentDescription = null
                )
                Text(text = configItem.javaClass.simpleName)
            }
        }
    }
    }