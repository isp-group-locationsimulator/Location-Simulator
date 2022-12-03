package com.example.mvvmtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvvmtest.ui.theme.MVVMTestTheme
import com.example.mvvmtest.view.mainScreen.SelectScreen
import com.example.mvvmtest.viewModel.MainViewModel

class MainActivity : ComponentActivity() {
    val mainViewModel by viewModels<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MVVMTestTheme {
                NavHost(navController = navController, startDestination = "selectScreen", ) {
                    composable("selectScreen") {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {
                            Column(
                                //modifier = Modifier.verticalScroll(rememberScrollState(0))
                            ) {
                                SelectScreen(mainViewModel = mainViewModel)
                                Button(onClick = { navController.navigate("sessionStart") }) {
                                    Text(text = "start")
                                }
                            }
                        }
                    }
                    composable("sessionStart") {
                        abc()

                    }
                }

            }
        }
    }

    @Composable
    fun abc() {
        Text("asfhiuzgwhiu")
    }
}
