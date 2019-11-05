package com.april.navigation

import android.app.Activity
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * 对导航控制相关的功能进行包装
 *
 * Window.ID_ANDROID_CONTENT
 */
class Navigator(
    manager: FragmentManager,
    @IdRes
    containerViewID: Int
) {
    private val controller = NavigationController(manager, containerViewID)

    /**
     * 压栈操作
     */
    fun pushFragment(
        targetFragment: Fragment,
        removeNow: Boolean = false
    ) {
        pushFragment(
            PushOption(
                targetFragment,
                targetFragment.javaClass.name,
                removeNow = removeNow
            )
        )
    }

    /**
     * 压栈操作
     */
    fun pushFragment(
        targetFragmentClass: Class<out Fragment>,
        removeNow: Boolean = false
    ) {
        pushFragment(
            PushOption(
                createFragment(targetFragmentClass),
                targetFragmentClass.name,
                removeNow = removeNow
            )
        )
    }

    /**
     * 压栈操作
     */
    fun pushFragment(option: PushOption) {
        controller.pushFragment(option)
    }

    /**
     * 压栈操作，并需要回传数据
     */
    fun pushFragmentForResult(
        targetFragment: Fragment,
        resultCallBack: NavigationResultCallBack
    ) {
        pushFragment(
            PushOption(
                targetFragment,
                targetFragment.javaClass.name,
                callBack = resultCallBack
            )
        )
    }

    /**
     * 回退到对应的操作
     * 并且移除该操作
     */
    fun popFragment(backStackName: String? = null) {
        controller.popFragment(backStackName)
    }

    /**
     * 回退到对应的操作
     * 但不移除该操作
     */
    fun popToFragment(backStackName: String) {
        controller.popFragment(backStackName, 0)
    }

    /**
     * 清空回退栈
     */
    fun popFragmentAll() {
        controller.popFragmentAll()
    }

    /**
     * 由 [Activity.onBackPressed] 调度此函数
     *
     * [Boolean] 是否还有 Fragment 可以回退
     */
    fun onBackPressed(): Boolean {
        return controller.onBackPressed()
    }

    /**
     * [Boolean] 是否可以弹出 Fragment
     */
    fun canPopFragment(): Boolean {
        return controller.canPopFragment()
    }

    /**
     * 设置回传的数据
     */
    fun setNavigationResult(
        //回传数据的 Fragment
        fragment: Fragment,
        //结果码
        resultCode: Int,
        //结果数据
        resultData: Bundle?
    ) {
        controller.setNavigationResult(
            fragment,
            resultCode,
            resultData
        )
    }

    /**
     * 反射创建一个 Fragment
     */
    fun <T : Fragment> createFragment(fragmentClass: Class<T>): T {
        return controller.manager.fragmentFactory.instantiate(
            this.javaClass.classLoader!!,
            fragmentClass.name
        ) as T
    }

}

//==================================================================================================

inline fun <reified T> Navigator.pushFragment(
    arguments: Bundle? = null,
    removeNow: Boolean = false
) where T : Fragment,
        T : INavigationFragment {
    val backStackName = T::class.java.name
    val targetFragment = createFragment(T::class.java)
    targetFragment.arguments = arguments
    pushFragment(
        PushOption(
            targetFragment,
            backStackName,
            removeNow = removeNow
        )
    )
}

inline fun <reified T> Navigator.pushFragment(
    removeNow: Boolean = false,
    block: Bundle.() -> Bundle
) where T : Fragment,
        T : INavigationFragment {
    pushFragment<T>(block.invoke(Bundle()), removeNow)
}

//==================================================================================================

inline fun <reified T> Navigator.pushFragmentForResult(
    arguments: Bundle? = null,
    resultCallBack: NavigationResultCallBack
) where T : Fragment,
        T : INavigationFragment {
    val backStackName = T::class.java.name
    val targetFragment = createFragment(T::class.java)
    targetFragment.arguments = arguments
    pushFragment(
        PushOption(
            targetFragment,
            backStackName,
            callBack = resultCallBack
        )
    )
}

inline fun <reified T> Navigator.pushFragmentForResult(
    resultCallBack: NavigationResultCallBack,
    block: Bundle.() -> Bundle
) where T : Fragment,
        T : INavigationFragment {
    pushFragmentForResult<T>(
        arguments = block.invoke(Bundle()),
        resultCallBack = resultCallBack
    )
}
