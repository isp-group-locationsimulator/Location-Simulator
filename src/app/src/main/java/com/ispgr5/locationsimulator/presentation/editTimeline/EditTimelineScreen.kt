import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.presentation.select.SelectEvent
import com.ispgr5.locationsimulator.presentation.select.SelectViewModel
import com.ispgr5.locationsimulator.presentation.select.components.SelectConfigurationButton


@Composable

fun EditTimelineScreen(
    navController: NavController,
    viewModel: SelectViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
}