package com.balsdon.watchapplication

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WatchFaceApplication : Application()

fun log(message: String) = android.util.Log.d("WatchFaceApplication", message)