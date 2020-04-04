package com.april.develop.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 * Activity 基类
 */
abstract class AbsActivity : AppCompatActivity {

    constructor() : super()

    constructor(@LayoutRes contentLayoutRes: Int) : super(contentLayoutRes)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onInitScreenOrientation()
        onFitSystemWindowStatusBar()
    }

    /**
     * 初始化屏幕方向
     */
    @SuppressLint("SourceLockedOrientationActivity")
    protected open fun onInitScreenOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * 适配状态栏
     */
    protected open fun onFitSystemWindowStatusBar(
        @ColorInt statusBarColor: Int = 0X00000000,
        darkText: Boolean? = null,
        fullScreen: Boolean = true
    ) {
        window.apply {
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
                this.statusBarColor = statusBarColor
            }
        }
    }

}