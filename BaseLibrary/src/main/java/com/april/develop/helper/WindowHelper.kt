package com.april.develop.helper

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


/**
 * 设置状态栏
 *
 * [backgroundColor] 状态栏背景色
 * [fullScreen] 布局是否延申到状态栏下面去
 * [darkText] 文字是否为暗色 (6.0 及以上生效)
 */
fun Window.statusBar(@ColorRes backgroundColor: Int,
                     fullScreen: Boolean = false,
                     darkText: Boolean = false) {
    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    if (fullScreen) {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or if (darkText) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }
    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    statusBarColor = ContextCompat.getColor(context, backgroundColor)
}