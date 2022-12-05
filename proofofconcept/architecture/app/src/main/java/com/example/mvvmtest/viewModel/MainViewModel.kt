package com.example.mvvmtest.viewModel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.mvvmtest.model.Configuration

/**
 * This class ist the Main View Model
 * it contains the current Configuration
 * and getter for the different viewModels
 */
class MainViewModel : ViewModel() {

    val currentConfiguration = Configuration()
    private var selectScreenViewModel: SelectScreenViewModel? = null
    private lateinit var navController: NavHostController

    fun setNavController(navController: NavHostController) {
        this.navController = navController
    }

    fun getSelectScreenViewModel(): SelectScreenViewModel {
        if (this.selectScreenViewModel == null) {
            this.selectScreenViewModel = SelectScreenViewModel(
                currentConfiguration = currentConfiguration
            )
        }
        return this.selectScreenViewModel!!
    }
}