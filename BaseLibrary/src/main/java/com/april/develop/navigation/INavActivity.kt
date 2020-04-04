package com.april.develop.navigation

import androidx.annotation.IdRes
import androidx.annotation.NavigationRes

/**
 * 导航相关约束
 */
interface INavActivity {

    /**
     * 设置导航图资源文件
     */
    @NavigationRes
    fun getNavigationGraphResId(): Int

    /**
     * 获取导航承载布局id
     */
    @IdRes
    fun getNavigationContainerViewId(): Int
}