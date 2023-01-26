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

    /**
     * This companion object enables the rest of the project to access context.
     * TODO: I am not sure that this is a good solution. Should be checked and changed eventually. Memory leak?
     */
    companion object {
        var context: Context? = null
            internal set
    }
}

