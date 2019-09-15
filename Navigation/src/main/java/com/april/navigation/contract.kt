package com.april.navigation

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

/**
 * 由宿主 FragmentActivity 实现
 */
interface INavigationActivity {

    /**
     * [Int] 装载 Fragment 的布局
     */
    @IdRes
    fun navigationContainerID(): Int {
        return View.NO_ID
    }

    /**
     * [Navigator] 导航核心类
     */
    fun navigationOfNavigator(): Navigator

}

/**
 * 由内部 Fragment 实现
 */
interface INavigationFragment {

}

/**
 * Fragment 的扩展，用于在其内部启动 Fragment
 */
inline fun <T, reified D> T.pushFragment(popNow: Boolean = false,
                                         bundle: Bundle? = null)
    where T : Fragment,
          T : INavigationFragment,
          D : Fragment,
          D : INavigationFragment {
    checkSupport()?.navigationOfNavigator()?.push(D::class.java, popNow, bundle)
}

fun <T> T.pushFragmentForResult()
    where T : Fragment,
          T : INavigationFragment {
    checkSupport()
    TODO("启动 Fragment 并接受回复数据")
}

/**
 * 设置导航回调数据
 */
fun <T> T.setNavigatorResult(any: Any?)
    where T : Fragment,
          T : INavigationFragment {
    checkSupport()?.navigationOfNavigator()?.setNavigatorResult(this.javaClass.name, any)
}

fun <T> T.checkSupport(): INavigationActivity?
    where T : Fragment,
          T : INavigationFragment {
    return activity as? INavigationActivity
        ?: error("the host Activity is null or isn't implement interface : INavigationSupport")
}