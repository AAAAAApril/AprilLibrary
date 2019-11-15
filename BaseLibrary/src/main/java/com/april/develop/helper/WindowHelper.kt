package com.april.develop.helper

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt

/**
 * 5.0 以上所有
 *
 * 如果设置全屏，需要自己在布局中处理布局问题
 */
fun Window.fitSystemStatusBar_up5All(
    @ColorInt backgroundColor: Int = 0X33000000,
    fullScreen: Boolean = false,
    needFitSoftInput: Boolean = true
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        if (fullScreen) {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            if (needFitSoftInput) {
                fitSoftInput()
            }
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
    @ColorInt backgroundColor: Int = 0X33000000,
    fullScreen: Boolean = false,
    needFitSoftInput: Boolean = true
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        && Build.VERSION.SDK_INT < Build.VERSION_CODES.M
    ) {
        fitSystemStatusBar_up5All(backgroundColor, fullScreen, needFitSoftInput)
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
    darkText: Boolean? = null,
    needFitSoftInput: Boolean = true
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (darkText != null) {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or if (darkText) {
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
            if (needFitSoftInput) {
                fitSoftInput()
            }
        } else {
            if (fullScreen) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                if (needFitSoftInput) {
                    fitSoftInput()
                }
            }
        }
        statusBarColor = backgroundColor
    }
}

/**
 * [Int] 获取系统状态栏高度
 */
fun Window.getStatusBarHeight(): Int = context.statusBarHeight()

/**
 * 获取导航栏的高度 （单位：px）
 *
 * [checkNavigationBarExit] 是否检测导航栏是否存在
 */
fun Window.getNavigationBarHeight(checkNavigationBarExit: Boolean = true): Int {
    var navigationBarExit = true
    if (checkNavigationBarExit) {
        navigationBarExit = isNavigationBarExist()
    }
    if (!navigationBarExit) {
        return 0
    }
    var height = 0
    val resourceID = context.resources.getIdentifier(
        "navigation_bar_height",
        "dimen",
        "android"
    )
    if (resourceID > 0) {
        height = context.resources.getDimensionPixelSize(resourceID)
    }
    return height
}

/**
 * 导航栏是否存在
 */
fun Window.isNavigationBarExist(): Boolean {
    var exist = false
    (decorView as? ViewGroup)?.let { parent ->
        for (index in 0 until parent.childCount) {
            val child = parent.getChildAt(index)
            if (child.visibility == View.GONE
                || child.visibility == View.INVISIBLE
            ) {
                continue
            }
            if (child.id != View.NO_ID
                && "navigationBarBackground" == context.resources.getResourceEntryName(child.id)
            ) {
                exist = true
                break
            }
        }
    }
    return exist
}

/**
 * 这个只是一个参考，具体可以自行实现
 */
private fun Window.fitSoftInput() {
    setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                or
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
    )
}
