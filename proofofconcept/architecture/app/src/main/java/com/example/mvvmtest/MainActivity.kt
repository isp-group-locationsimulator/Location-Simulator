package com.example.mvvmtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvvmtest.ui.theme.MVVMTestTheme
import com.example.mvvmtest.view.startScreen.SelectScreen
import com.example.mvvmtest.view.startScreen.StartScreen
import com.example.mvvmtest.viewModel.MainViewModel
import com.example.mvvmtest.viewModel.selectScreen.SelectScreenViewModel

class MainActivity : ComponentActivity() {
    val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //NavController for switching screens
            val navController = rememberNavController()
            MVVMTestTheme {
                NavHost(navController = navController, startDestination = "selectScreen") {
                    composable("selectScreen") {
                        SelectScreen(selectScreenViewModel = SelectScreenViewModel(mainViewModel), navController = navController)
                    }
                    composable("sessionStart") {
                        StartScreen(currentConfiguration = mainViewModel.state)
                    }
                }

            }
        }
    }
}