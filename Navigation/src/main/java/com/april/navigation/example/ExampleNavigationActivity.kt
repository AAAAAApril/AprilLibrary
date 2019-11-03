package com.april.navigation.example

import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.april.navigation.INavigationActivity
import com.april.navigation.Navigator

open class ExampleNavigationActivity : AppCompatActivity(), INavigationActivity {

    override val navigator: Navigator by lazy {
        Navigator(supportFragmentManager, Window.ID_ANDROID_CONTENT)
    }

    override fun onBackPressed() {
        if (!navigator.onBackPressed()) {
            super.onBackPressed()
        }
    }
}