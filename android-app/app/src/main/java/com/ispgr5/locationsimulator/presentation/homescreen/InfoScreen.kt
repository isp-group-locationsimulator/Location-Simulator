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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import com.ispgr5.locationsimulator.presentation.universalComponents.ClickableLink
import com.ispgr5.locationsimulator.presentation.universalComponents.ClickableLinkDefaults
import com.ispgr5.locationsimulator.presentation.universalComponents.LocationSimulatorTopBar
import com.ispgr5.locationsimulator.ui.theme.LocationSimulatorTheme
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.ui.theme.ThemeType

/**
 * The Info Screen that shows Information about the developers and similar things.
 */
@Composable
fun InfoScreen(
    navController: NavController,
) {
    InfoScreenScaffold {
        navController.popBackStack()
    }
}

@Preview
@Composable
fun InfoScreenPreview() {
    LocationSimulatorTheme {
        InfoScreenScaffold(onBackClick = {})
    }
}

@Composable
fun InfoScreenScaffold(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            LocationSimulatorTopBar(
                onBackClick = onBackClick,
                title = stringResource(id = R.string.ScreenInfo)
            )
        },
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
                    .padding(scaffoldPadding)
            ) {
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
                        Maintainer()
                        BlankSpacer()

                        OriginalDevelopers()

                        BlankSpacer()
                        OpenSource()

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
fun OpenSource() {
    Headline(text = stringResource(id = R.string.open_source))

    Text(
        text = stringResource(R.string.locationsimulator_is_open_source),
        textAlign = TextAlign.Center
    )

    val context = LocalContext.current
    val uri by remember {
        mutableStateOf(context.getString(R.string.github_uri))
    }

    ClickableLink(
        text = uri,
        urlTarget = uri,
        spanStyle = ClickableLinkDefaults.defaultSpanStyle().copy(
            color = MaterialTheme.colorScheme.secondary,
            fontFamily = FontFamily.Monospace
        )
    )
}

@Composable
fun NameText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    )
}

@Composable
fun IntroText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Italic
        )
    )
}

@Composable
fun Headline(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center)
    )
}

@Composable
fun Maintainer() {
    Headline(text = stringResource(id = R.string.maintainer))
    IntroText(text = stringResource(R.string.location_simulator_is_maintained_by))
    NameText(text = "Joshua Wiedekopf")

    val context = LocalContext.current
    val email by remember {
        mutableStateOf(context.getString(R.string.contact_email))
    }
    ClickableLink(
        text = email,
        urlTarget = stringResource(R.string.mailto_link, email, BuildConfig.VERSION_NAME)
    )
}

@Composable
fun OriginalDevelopers() {
    Headline(text = stringResource(id = R.string.infoscreen_developer))
    val developers = listOf(
        "Nihad Beddeiwi",
        "Lars Fockenga",
        "Tom Boje",
        "Brian Rickert",
        "Felix Winkler",
        "Florian Vierkant",
        "Marie Biethahn",
        "Max Henning Junghans",
        "Sebastian Guhl",
        "Steffen Marbach"
    )
    IntroText(
        text = stringResource(id = R.string.location_simulator_was_originally_created_by)
    )
    developers.forEach { dev ->
        NameText(dev)
    }
}

@Composable
fun SupportedBy() {
    Headline(text = stringResource(id = R.string.infoscreen_support))
    IntroText(text = stringResource(R.string.original_development_supported))
    Text(text = stringResource(id = R.string.isp), textAlign = TextAlign.Center)
    Text(text = stringResource(id = R.string.university), textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(5.dp))
    NameText(text = "Jan Matyssek")
    NameText(text = "Joshua Wiedekopf")
    NameText(text = "Juljan Bouchagiar")
    Image(
        painter = painterResource(id = R.drawable.logo_isp),
        contentDescription = "ISP Logo"
    )
}

@Composable
fun Disclaimer() {
    Text(text = "Disclaimer", style = MaterialTheme.typography.headlineSmall)
    Text(text = stringResource(id = R.string.Disclaimer), textAlign = TextAlign.Center)
}

@Composable
fun License() {
    Text(
        text = stringResource(id = R.string.infoscreen_license),
        style = MaterialTheme.typography.headlineSmall
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
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(id = R.string.app_version, BuildConfig.VERSION_NAME),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}
