package com.ispgr5.locationsimulator.presentation.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar

/**
 * The Info Screen that shows Information about the developers and similar things.
 */
@Composable
fun InfoScreen(
	navController: NavController
) {
	Scaffold(
		topBar = { TopBar(navController, stringResource(id = R.string.ScreenInfo)) },

		content = {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(30.dp + it.calculateTopPadding())
					.verticalScroll(rememberScrollState()),
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
				Spacer(modifier = Modifier.height(30.dp))
				Image(
					painter = painterResource(id = R.drawable.logo_isp),
					contentDescription = "ISP Logo"
				)
			}

		}
	)

}