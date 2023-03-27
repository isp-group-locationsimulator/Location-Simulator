package com.ispgr5.locationsimulator.presentation.homescreen

import android.os.Build
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.data.storageManager.SoundStorageManager
import com.ispgr5.locationsimulator.presentation.MainActivity
import com.ispgr5.locationsimulator.presentation.select.components.OneConfigurationListMember
import com.ispgr5.locationsimulator.ui.theme.ThemeState
import com.ispgr5.locationsimulator.presentation.universalComponents.TopBar
import com.ispgr5.locationsimulator.ui.theme.theBlue

/**
 * The Home Screen.
 *
 */
@ExperimentalAnimationApi
@Composable
fun HomeScreenScreen(
	navController: NavController,
	viewModel: HomeScreenViewModel = hiltViewModel(),
	batteryOptDisableFunction: () -> Unit,
	soundStorageManager: SoundStorageManager,
	toaster: (String) -> Unit,
	activity: MainActivity,
	darkTheme: MutableState<ThemeState>
) {
	viewModel.updateConfigurationWithErrorsState(soundStorageManager = soundStorageManager)
	val state = viewModel.state.value
	val notFound: String = stringResource(id = R.string.not_found)

	Scaffold(
		topBar = { TopBar(navController, stringResource(id = R.string.ScreenHome),false,
		extraActions = {IconButton(onClick = {
			navController.navigate("infoScreen")
		}, modifier = Modifier.padding(5.dp)) {
			Icon(
				painter = painterResource(id = R.drawable.baseline_info_24),
				contentDescription = ""
			)
		}}

		) },
		content = {
			Spacer(modifier = Modifier.height(it.calculateTopPadding()))


			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(30.dp),
				verticalArrangement = Arrangement.Top,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Spacer(modifier = Modifier.height(110.dp))
				Text(
					text = stringResource(id = R.string.homescreen_appname),
					fontSize = 8.em,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colors.onBackground
				)

			}
			Column(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Spacer(modifier = Modifier.height(50.dp))
				Button(onClick = {
					viewModel.onEvent(HomeScreenEvent.SelectConfiguration)
					navController.navigate("selectScreen")
					Modifier
						.height(100.dp)
						.width(300.dp)

				}) {
					Text(
						text = stringResource(id = R.string.homescreen_btn_select_profile),
						fontSize = 30.sp
					)
				}
				Spacer(modifier = Modifier.height(60.dp))

		/**
		 * The Favorite Configurations
		 */
		LazyColumn(
			Modifier
				.padding(15.dp)
				.fillMaxWidth()
		) {
			items(state.favoriteConfigurations) { configuration ->
				OneConfigurationListMember(
					configuration = configuration,
					onToggleClicked = {
						if (state.configurationsWithErrors.find { conf -> conf.id == configuration.id } == null) {
							navController.navigate("delayScreen?configurationId=${configuration.id}")
						} else {
							for (error in viewModel.whatIsHisErrors(
								configuration = configuration,
								soundStorageManager = soundStorageManager
							)) {
								toaster("$error $notFound")
							}
						}
					},
					isToggled = false,
					onEditClicked = {},
					onSelectClicked = {},
					onExportClicked = {},
					hasErrors = state.configurationsWithErrors.find { conf -> conf.id == configuration.id } != null,
					onErrorInfoClicked = {
						for (error in viewModel.whatIsHisErrors(
							configuration = configuration,
							soundStorageManager = soundStorageManager
						)) {
							toaster("$error $notFound")
						}
					},
					isFavorite = configuration.isFavorite,
					onFavoriteClicked = {}
				)
				Spacer(modifier = Modifier.height(6.dp))
			}

		}
		Row(
			Modifier
				.fillMaxWidth()
				.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceEvenly


		){
			// Get the current theme mode


			// Switch to toggle the theme mode
			Switch(
				checked = darkTheme.value.isDarkTheme,
				onCheckedChange = {
					viewModel.onEvent(HomeScreenEvent.ChangedAppTheme(it, activity, darkTheme))
					/*if(state.isLightTheme) {
						// Toggle the theme mode
						AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
					}
					else{
						AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
					}*/
				},
				colors = SwitchDefaults.colors(
					checkedThumbColor = MaterialTheme.colors.primary,
					uncheckedThumbColor = MaterialTheme.colors.primary,
					checkedTrackColor = MaterialTheme.colors.secondary,
					uncheckedTrackColor = MaterialTheme.colors.secondary,
				)
			)
		}
	}


			/**
			 * battery optimization hint
			 */
			/**
			 * battery optimization hint
			 */
			val pm =
				LocalContext.current.getSystemService(ComponentActivity.POWER_SERVICE) as PowerManager
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !pm.isIgnoringBatteryOptimizations(
					LocalContext.current.packageName
				)
			) {
				Column(
					modifier = Modifier.fillMaxSize(),
					verticalArrangement = Arrangement.Bottom,
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = stringResource(id = R.string.battery_opt_recommendation),
						textAlign = TextAlign.Center
					)
					//var forceUpdate:Boolean by remember { mutableStateOf(true) }
					Button(onClick = {
						viewModel.onEvent(HomeScreenEvent.DisableBatteryOptimization { batteryOptDisableFunction() })
					}) {
						Text(text = stringResource(id = R.string.battery_opt_button))
					}
				}
			}
		})
}