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
 * 注：最好是使用窗口的根布局来观察
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
    callBackWhenCoverHeightTheSame: Boolean = false,
    //监听回调
    listener: OnSoftInputHeightChangeListener
): SoftInputHeightObserver {
    val observer = SoftInputHeightObserver(
        softInputShowingHeightThreshold,
        callBackWhenCoverHeightTheSame,
        listener
    )
    viewTreeObserver.addOnGlobalLayoutListener(observer)
    return observer
}

class SoftInputHeightObserver(
    /**
     * 判定为软件盘弹起的高度阈值
     */
    @Px
    private val softInputShowingHeightThreshold: Int = 250,
    /**
     * 当前后两次遮挡高度相同时，是否回调（默认不回调，避免一些不需要的重新测量）
     */
    private val callBackWhenCoverHeightTheSame: Boolean = false,
    /**
     * 观察回调
     */
    private val listener: OnSoftInputHeightChangeListener
) : ViewTreeObserver.OnGlobalLayoutListener {

    //上一次软件盘显示的高度，默认为 0
    private var mSoftInputShowingHeight: Int = 0

    /**
     * 获取软件盘正在展示的高度，单位： px
     */
    @Px
    fun getSoftInputShowingHeight(): Int {
        return mSoftInputShowingHeight
    }

    /**
     * 软件盘是否正在展示
     */
    fun isSoftInputShowing(): Boolean {
        return mSoftInputShowingHeight >= softInputShowingHeightThreshold
    }

    override fun onGlobalLayout() {
        listener.getSoftInputObservableWindow()?.let { window ->
            Rect().let { rect ->
                window.decorView.getWindowVisibleDisplayFrame(rect)
                onChange(window, rect)
            }
        }
    }

    /**
     * 确认是否变化
     */
    private fun onChange(window: Window, rect: Rect) {
        //当前的遮挡高度
        val softInputShowingHeight =
            //屏幕整体高度 - 可视区域底部高度 - 导航栏高度
            window.decorView.rootView.height - rect.bottom - window.navigationBarHeight()
        //前后两次的高度是否相同
        val heightTheSame = (mSoftInputShowingHeight == softInputShowingHeight)
        //前后两次的高度相同，并且不需要回调，则返回
        if (heightTheSame
            && !callBackWhenCoverHeightTheSame
        ) {
            return
        }
        //更新缓存高度
        mSoftInputShowingHeight = softInputShowingHeight
        //需要回调才继续处理
        listener.onSoftInputHeightChanged(
            this,
            getSoftInputShowingHeight(),
            isSoftInputShowing()
        )
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
        heightObserver: SoftInputHeightObserver,
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