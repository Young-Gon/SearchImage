package com.gondev.searchimage

import android.app.Application
import com.gondev.searchimage.util.timber.DebugLogTree
import com.gondev.searchimage.util.timber.ReleaseLogTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ImageApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugLogTree())
        } else {
            Timber.plant(ReleaseLogTree())
        }
    }
}