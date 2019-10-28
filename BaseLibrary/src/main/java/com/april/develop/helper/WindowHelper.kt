package com.april.develop.helper

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


/**
 * 5.0 以上所有
 *
 * 如果设置全屏，需要自己在布局中处理布局问题
 */
fun Window.fitSystemStatusBar_up5All(
    @ColorInt backgroundColor: Int = 0X44000000,
    fullScreen: Boolean = false
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        if (fullScreen) {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        statusBarColor = backgroundColor
    }
}

/**
 * 5.0 到 6.0，设置状态栏颜色
 *
 * 如果设置全屏，需要自己在布局中处理布局问题
 */
fun Window.fitSystemStatusBar_5to6(
    @ColorInt backgroundColor: Int = 0X44000000,
    fullScreen: Boolean = false
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        && Build.VERSION.SDK_INT < Build.VERSION_CODES.M
    ) {
        fitSystemStatusBar_up5All(backgroundColor, fullScreen)
    }
}

/**
 * 6.0 以上
 *
 * 如果设置状态栏文字颜色，则只支持全屏，需要自己在布局中处理布局问题
 *
 * if  darkText != null
 *      fullScreen = true
 */
fun Window.fitSystemStatusBar_up6All(
    @ColorInt backgroundColor: Int = 0X00000000,
    fullScreen: Boolean = false,
    darkText: Boolean? = null
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (darkText != null) {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or if (darkText) {
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
        } else {
            if (fullScreen) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
        statusBarColor = backgroundColor
    }
}
