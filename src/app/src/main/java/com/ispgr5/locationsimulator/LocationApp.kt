package com.ispgr5.locationsimulator

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

/**
 * This class is needed for the AppModule for Data Injection
 */
@HiltAndroidApp
class LocationApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }


    // TODO: Is this a acceptable solution?
    companion object {
        var context: Context? = null
            internal set
    }
}

