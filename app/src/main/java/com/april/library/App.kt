package com.april.library

import android.app.Application
import com.april.permission.APermission

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        APermission.init(this)
    }
}