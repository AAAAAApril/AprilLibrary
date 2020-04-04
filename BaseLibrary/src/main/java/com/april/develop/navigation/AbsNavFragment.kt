package com.april.develop.navigation

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import com.april.develop.fragment.AbsFragment
/**
 * Navigation Fragment 基类
 */
abstract class AbsNavFragment :AbsFragment{

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    //回退操作回调
    private val backPressedCallback = object : OnBackPressedCallback(
        false //默认情况下，不拦截返回操作
    ) {
        override fun handleOnBackPressed() {
            //拦截到了回退操作
            onInterceptBackPressed()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //监听回退操作分发调度
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    /**
     * 设置是否拦截返回操作
     */
    protected fun interceptBackPressed(intercept: Boolean) {
        backPressedCallback.isEnabled = intercept
    }

    /**
     * 这是一个回调函数 ==> 主动调用无意义
     */
    protected open fun onInterceptBackPressed() {}

}