package com.april.library.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class AbsNavFragment : Fragment() {

    abstract fun onCreateViewFirst(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    /*
        由于 Navigation 库是使用的 replace 方式加载的 Fragment，
        因此会导致切换 Fragment 的时候 onDestroyView 函数被回调，
        而再次回到这个 Fragment 的时候 onCreateView 仍然会回调，
        所以这里需要缓存一下第一次创建的布局，避免重复创建从而影响性能。
     */
    private var navContentView: View? = null
    //是否是第一次创建布局
    protected open var createNavViewFirstTime: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (navContentView == null) {
            navContentView = onCreateViewFirst(inflater, container, savedInstanceState)
            createNavViewFirstTime = true
        }
        return navContentView
    }

    override fun onStart() {
        createNavViewFirstTime = false
        super.onStart()
    }

}