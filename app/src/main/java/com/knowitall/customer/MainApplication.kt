package com.knowitall.customer


import android.app.Application
import android.content.Context
import java.util.*

class MainApplication : Application() {

    companion object {
        var ctx: Context? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        ctx = applicationContext
//        if (!Places.isInitialized()) {
//            Places.initialize(
//                applicationContext,
//                getString(R.string.api_key_map),
//                Locale.US
//            )
//        }
    }

}