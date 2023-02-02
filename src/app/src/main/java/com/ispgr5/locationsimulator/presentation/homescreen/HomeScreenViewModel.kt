package com.ispgr5.locationsimulator.presentation.homescreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
/**
 * The ViewModel for the DelayScreen
 */
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    ) :ViewModel() {



        /**
         * Handles UI Events
         */
        fun onEvent(event: HomeScreenEvent) {
            when(event){
                is HomeScreenEvent.MusterAuswahl ->{
                    return
                }
            }
        }
    }