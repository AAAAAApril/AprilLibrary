package com.april.library

import android.app.Application
import com.april.develop.helper.GrayWindowHelper
import com.april.permission.APermission

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        APermission.init(this)

        //窗体整体变灰
//        registerActivityLifecycleCallbacks(GrayWindowHelper)
    }
}