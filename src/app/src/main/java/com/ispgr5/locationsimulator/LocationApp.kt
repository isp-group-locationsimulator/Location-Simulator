package com.ispgr5.locationsimulator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * This class is needed for the AppModule for Data Injection
 */
@HiltAndroidApp
class LocationApp : Application()