import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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

@Preview(showBackground = true)
@Composable
fun TimelineItem(
    text : String = "hallo"
){
    Card( elevation = 4.dp, backgroundColor = Color.White, modifier = Modifier.padding(5.dp) ) {
        Row() {
            Text("x ")
            /*Icon(
               painter = painterResource(id = R.drawable.audionouse2),
                contentDescription = null
            )*/
            Text(text = text)
        }
    }
}