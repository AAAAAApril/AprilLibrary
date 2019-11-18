package com.april.develop.watcher

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import androidx.annotation.Px

/**
 * 观察窗口软件盘变化
 *
 * 注意：设置这个监听时，当前 Activity 一定不能同时设置
 * WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
 * 和
 * WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
 * 否则会监听不到变化
 */
fun View.observerWindowSoftInputChange(
    //弹起判定高度阈值（大于等于这个值则表示弹起了，否则表示隐藏了）
    @Px
    softInputShowingHeightThreshold: Int = 250,
    //监听回调
    listener: OnSoftInputHeightChangeListener
) {
    SoftInputHeightObserver(this, softInputShowingHeightThreshold, listener)
}

class SoftInputHeightObserver(
    view: View,
    /**
     * 判定为软件盘弹起的高度阈值
     */
    @Px
    private val softInputShowingHeightThreshold: Int = 250,
    /**
     * 观察回调
     */
    private val listener: OnSoftInputHeightChangeListener
) : ViewTreeObserver.OnGlobalLayoutListener {
    init {
        view.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        listener.getSoftInputObservableWindow()?.let { window ->
            Rect().let { rect ->
                window.decorView.getWindowVisibleDisplayFrame(rect)
                val softInputShowingHeight =
                    //屏幕整体高度 - 可视区域底部高度 - 导航栏高度
                    window.decorView.rootView.height - rect.bottom - window.navigationBarHeight()
                listener.onSoftInputHeightChanged(
                    softInputShowingHeight,
                    softInputShowingHeight >= softInputShowingHeightThreshold
                )
            }
        }
    }
}

interface OnSoftInputHeightChangeListener {

    /**
     * [Window] 获取被观察的窗口
     * 如果返回 null，则不会执行操作，[onSoftInputHeightChanged] 也不会回调
     */
    fun getSoftInputObservableWindow(): Window?

    /**
     * [softInputHeight] 软件盘的高度
     * [softInputShowing] 软件盘是否正在展示
     */
    fun onSoftInputHeightChanged(
        @Px
        softInputHeight: Int,
        softInputShowing: Boolean
    )
}

/**
 * 获取导航栏的高度 （单位：px）
 */
private fun Window.navigationBarHeight(checkNavigationBarExit: Boolean = true): Int {
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
private fun Window.isNavigationBarExist(): Boolean {
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