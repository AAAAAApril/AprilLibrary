package com.april.navigation

import android.view.Window
import androidx.appcompat.app.AppCompatActivity

class NavigationActivity : AppCompatActivity(), INavigationActivity {

    //导航核心类
    private val navigator = Navigator(this, supportFragmentManager)

    override fun navigationContainerID(): Int {
        /**
         * 示例返回根布局 ID
         */
        return Window.ID_ANDROID_CONTENT
    }

    override fun navigationOfNavigator(): Navigator {
        return navigator
    }

    override fun onBackPressed() {
        if (navigator.onNavigatorBackPressed()) {
            super.onBackPressed()
        }
    }

}