import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.ScrollableTabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(name = "Preview1", device = Devices.PIXEL, showSystemUi = true)
@Composable
fun testScreen(){
    EditTimelineTimeline()
}

@Composable
fun EditTimelineTimeline(){

    Row(
        modifier = Modifier.padding(16.dp)
            ){
        TimelineItem()
        TimelineItem()
        TimelineItem()
        TimelineItem()
        TimelineItem()
        TimelineItem()
        TimelineItem()
        TimelineItem()
        TimelineItem()
        TimelineItem()
        TimelineItem()
        TimelineItem()
        TimelineItem()
        TimelineItem()
    }
}