package com.april.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * 导航处理类
 */
class Navigator(private val navigationSupport: INavigationActivity,
                private val manager: FragmentManager) {

    private val controllerKeyList = mutableListOf<String>()
    private val controllerList = mutableListOf<Controller>()

    /**
     * 压栈一个 Fragment
     */
    fun <F : Fragment> push(fragmentClass: Class<F>,
                            popNow: Boolean = false,
                            bundle: Bundle? = null) {
        TODO("压栈一个 Fragment")
    }

    /**
     * 压栈，并获取结果
     */
    fun <F : Fragment, R : Any> pushWithResult(fragmentClass: Class<F>): Result<R> {
        TODO("")
    }

    /**
     * 出栈一个 Fragment
     *
     * [targetFragmentClassName] 回退到目标 Fragment 位置
     */
    fun pop(targetFragmentClassName: String? = null) {
        if (controllerList.size == 1) {
            return
        }
//        controllerList.last().result?.call()
        TODO(
            "只弹出最后一个和弹出到前面中的某一个的处理方式不同，这里需要考虑全，" +
                "只要不是弹出最后一个，就不要回调结果数据，避免出错"
        )


        manager.popBackStackImmediate(targetFragmentClassName, 0)
    }

    /**
     * 导航返回支持
     * [Boolean] 为 true 时表示 Fragment 没有可以回退的了
     */
    fun onNavigatorBackPressed(): Boolean {
        if (controllerList.size == 1) {
            return true
        }
        pop()
        return false
    }

    /**
     * 设置结果数据
     */
    internal fun setNavigatorResult(name: String, any: Any?) {
        controllerList[controllerKeyList.indexOf(name)].result?.setResult(any)
    }

}

/**
 * 控制器，用来存放导航需要的数据
 */
class Controller {
    internal var result: Result<Any>? = null
}

/**
 * 跳转回复结果
 *
 * 这个类可以考虑和 [Controller] 合并
 */
class Result<T : Any> {

    private var t: T? = null
    private var listener: ((T?) -> Unit)? = null

    internal fun setResult(t: T?) {
        this.t = t
    }

    internal fun call() {
        listener?.invoke(t)
    }

    fun listen(block: (T?) -> Unit) {
        listener = block
    }
}