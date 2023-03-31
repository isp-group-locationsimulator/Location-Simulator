package com.ispgr5.locationsimulator.presentation.editTimeline.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.domain.model.ConfigComponent
import com.ispgr5.locationsimulator.domain.model.Sound

/**
 * @param components all Configuration Components that the timeline should show
 * @param selectedComponent the Configuration Component that is currently selected
 * @param onSelectAComponent what should happen if a Time line Item gets clicked
 */
@Composable
fun Timeline(
	components: List<ConfigComponent>,
	selectedComponent: ConfigComponent?,
	onSelectAComponent: (ConfigComponent) -> Unit,
	onAddClicked: () -> Unit,
	showAddButton: Boolean = true
) {
	//iterate through all Configuration components and Display it with a TimelineItem
	Row(
		modifier = Modifier.horizontalScroll(rememberScrollState())
	) {
		components.forEach { configComponent ->
			TimelineItem(
				isSelected = selectedComponent === configComponent,
				configItem = configComponent,
				onSelect = onSelectAComponent
			)
		}
		/**
		 * Add new Timeline Item(Configuration Component)
		 */
		if (showAddButton) {
			Button(
				elevation = ButtonDefaults.elevation(4.dp),
				onClick = onAddClicked,
				modifier = Modifier
					.width(55.dp)
					.height(55.dp)
					.padding(6.dp)
					.testTag(TestTags.EDIT_TIMELINE_SCREEN_ADD_BUTTON)
			) {
				Icon(
					painter = painterResource(id = R.drawable.ic_baseline_add_24),
					contentDescription = null
				)
			}
		}
	}
}

/**
 * @param isSelected is this Configuration Component currently selected?
 * @param configItem the Configuration Component that should showed
 * @param onSelect what should happen if this Time line Item gets clicked
 */
@Composable
fun TimelineItem(
	isSelected: Boolean,
	configItem: ConfigComponent,
	onSelect: (ConfigComponent) -> Unit
) {
	Card(elevation = 4.dp, backgroundColor = MaterialTheme.colors.surface,
		//if selected the Item gets a Border in another Color
		modifier = if (isSelected) {
			Modifier
				.width(55.dp)
				.height(55.dp)
				.padding(6.dp)
				.border(1.dp, MaterialTheme.colors.primary, RoundedCornerShape(10))
				.clickable { onSelect(configItem) }
		} else {
			Modifier
				.width(55.dp)
				.height(55.dp)
				.padding(6.dp)
				.clickable { onSelect(configItem) }
		}) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Center,
			modifier = Modifier.padding(2.dp)
		) {
			Icon(
				painter = if (configItem is Sound) {
					painterResource(id = R.drawable.audionouse2)
				} else {
					painterResource(id = R.drawable.ic_baseline_vibration_24)
				},
				contentDescription = null
			)

		}
	}
}