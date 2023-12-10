package com.ispgr5.locationsimulator.presentation.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gigamole.composescrollbars.Scrollbars
import com.gigamole.composescrollbars.config.ScrollbarsConfig
import com.gigamole.composescrollbars.config.ScrollbarsOrientation
import com.gigamole.composescrollbars.rememberScrollbarsState
import com.gigamole.composescrollbars.scrolltype.ScrollbarsScrollType
import com.gigamole.composescrollbars.scrolltype.knobtype.ScrollbarsStaticKnobType
import com.ispgr5.locationsimulator.BuildConfig
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar

/**
 * The Info Screen that shows Information about the developers and similar things.
 */
@Composable
fun InfoScreen(
    navController: NavController,
    scaffoldState: ScaffoldState
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(navController, stringResource(id = R.string.ScreenInfo)) },
        content = { scaffoldPadding ->

            val scrollState = rememberScrollState()
            val scrollbarsState = rememberScrollbarsState(
                config = ScrollbarsConfig(orientation = ScrollbarsOrientation.Vertical),
                scrollType = ScrollbarsScrollType.Scroll(
                    knobType = ScrollbarsStaticKnobType.Auto(),
                    state = scrollState
                )
            )

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)) {
                AppNameAndVersion()

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 30.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Developers()

                        BlankSpacer()
                        SupportedBy()

                        BlankSpacer()
                        License()

                        BlankSpacer()
                        Disclaimer()

                    }

                    Scrollbars(state = scrollbarsState)
                }
            }
        }
    )

}

@Composable
fun SupportedBy() {
    Text(
        text = stringResource(id = R.string.infoscreen_support),
        style = MaterialTheme.typography.h5
    )
    Text(text = stringResource(id = R.string.isp), textAlign = TextAlign.Center)
    Text(text = stringResource(id = R.string.university))
    Spacer(modifier = Modifier.height(5.dp))
    Text(text = "Jan Matyssek")
    Text(text = "Joshua Wiedekopf")
    Text(text = "Juljan Bouchagiar")
    Image(
        painter = painterResource(id = R.drawable.logo_isp),
        contentDescription = "ISP Logo"
    )
}

@Composable
fun Disclaimer() {
    Text(text = "Disclaimer", style = MaterialTheme.typography.h5)
    Text(text = stringResource(id = R.string.Disclaimer), textAlign = TextAlign.Center)
}

@Composable
fun License() {
    Text(
        text = stringResource(id = R.string.infoscreen_license),
        style = MaterialTheme.typography.h5
    )
    Text(
        text = stringResource(id = R.string.infoscreen_ownLicense),
        textAlign = TextAlign.Center
    )
    Text(
        text = stringResource(id = R.string.infoscreen_usingLicense),
        textAlign = TextAlign.Center
    )
}

@Composable
fun Developers() {
    Text(
        text = stringResource(id = R.string.infoscreen_developer),
        style = MaterialTheme.typography.h5
    )
    val developers = listOf(
        "Felix Winkler",
        "Florian Vierkant",
        "Marie Biethahn",
        "Max Henning Junghans",
        "Sebastian Guhl",
        "Steffen Marbach"
    )
    developers.forEach { dev ->
        Text(dev, style = MaterialTheme.typography.body1)
    }
}

@Composable
fun BlankSpacer(height: Dp = 30.dp) {
    Spacer(modifier = Modifier.height(height = height))
}

@Composable
fun AppNameAndVersion() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h4,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(id = R.string.app_version, BuildConfig.VERSION_NAME),
            style = MaterialTheme.typography.h5
        )
    }
}
