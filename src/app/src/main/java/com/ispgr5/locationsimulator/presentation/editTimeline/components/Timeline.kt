package com.ispgr5.locationsimulator.presentation.editTimeline.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ispgr5.locationsimulator.R
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
    onSelectAComponent: (ConfigComponent) -> Unit
) {
    //iterate through all Configuration components and Display it with a TimelineItem
    Column {
        components.forEach { configComponent ->
            TimelineItem(
                isSelected = selectedComponent === configComponent,
                configItem = configComponent,
                onSelect = onSelectAComponent
            )
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
    Card(elevation = 2.dp, backgroundColor = MaterialTheme.colors.surface,
        //if selected the Item gets a Border in another Color
        modifier = if (isSelected) {
            Modifier
                .width(150.dp)
                .padding(6.dp)
                .border(2.dp, MaterialTheme.colors.primary, RoundedCornerShape(10))
                .clickable { onSelect(configItem) }
        } else {
            Modifier
                .width(150.dp)
                .padding(6.dp)
                .clickable { onSelect(configItem) }
        }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp)
        ) {
            Icon(

                painter = if (configItem is Sound) {
                    painterResource(id = R.drawable.audionouse2)
                } else {
                    painterResource(id = R.drawable.ic_baseline_vibration_24)
                },
                contentDescription = null
            )
            Text(
                text = configItem.javaClass.simpleName,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .offset(x= (-12).dp)
            )
        }
    }
}