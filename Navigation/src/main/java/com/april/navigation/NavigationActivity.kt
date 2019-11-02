package com.april.navigation

import androidx.appcompat.app.AppCompatActivity

abstract class NavigationActivity : AppCompatActivity(), INavigationActivity {
    override fun onBackPressed() {
        if (!navigator.onBackPressed()) {
            super.onBackPressed()
        }
    }
}