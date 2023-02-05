package com.ispgr5.locationsimulator.presentation.homescreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ispgr5.locationsimulator.R

/**
 * The Info Screen that shows Information about the developers and similar things.
 */
@Composable
@Preview
fun InfoScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.infoscreen_developer), fontSize = 30.sp)
        Text(text = "Felix Winkler")
        Text(text = "Florian Vierkant")
        Text(text = "Marie Biethahn")
        Text(text = "Max Henning Junghans")
        Text(text = "Sebastian Guhl")
        Text(text = "Steffen Marbach")
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = stringResource(id = R.string.infoscreen_support), fontSize = 30.sp)
        Text(text = stringResource(id = R.string.isp), textAlign = TextAlign.Center)
        Text(text = stringResource(id = R.string.university))
        Text(text = "Juljan Bouchagiar")
    }
}