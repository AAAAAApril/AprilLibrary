package com.april.library.navigation

import com.april.develop.navigation.AbsNavActivity
import com.april.library.R

/**
 * Navigation test
 */
class NavTestActivity : AbsNavActivity() {
    override fun getNavigationGraphResId(): Int = R.navigation.nav_outer
}