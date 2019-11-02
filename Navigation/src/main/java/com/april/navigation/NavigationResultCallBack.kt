package com.april.navigation

import android.os.Bundle


interface NavigationResultCallBack {
    /**
     * 回传的数据
     */
    fun onNavigationResult(resultCode: Int, result: Bundle?)
}