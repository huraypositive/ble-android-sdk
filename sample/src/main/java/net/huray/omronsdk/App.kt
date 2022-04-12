package net.huray.omronsdk

import android.app.Application
import net.huray.omronsdk.ble.OHQDeviceManager

class App : Application() {

    init {
        application = this
    }

    override fun onCreate() {
        super.onCreate()
        OHQDeviceManager.init(applicationContext)
    }

    companion object {
        private lateinit var application: App

        val instance: App get() = application
    }
}