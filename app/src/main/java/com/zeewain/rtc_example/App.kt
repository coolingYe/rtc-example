package com.zeewain.rtc_example

import android.app.Application
import com.zeewain.rtc.RtcFactory

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        RtcFactory.initialize(this)
    }
}