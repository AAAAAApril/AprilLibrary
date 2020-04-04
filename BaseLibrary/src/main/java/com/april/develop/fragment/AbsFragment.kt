package com.april.develop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * Fragment 基类
 */
abstract class AbsFragment : Fragment {

    constructor()

    constructor(@LayoutRes contentLayoutRes: Int) : super(contentLayoutRes)

    //缓存一下，避免重复创建
    private var cacheContentView: View? = null

    //是否是第一次创建
    private var firstCreate: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (cacheContentView == null) {
            cacheContentView = onCreateView(inflater, container)
            firstCreate = true
        }
        //Tips：注意这里的处理
        cacheContentView?.let {
            (it.parent as? ViewGroup)?.removeView(it)
        }
        return cacheContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (firstCreate) {
            firstCreate = false
            onViewCreated(view)
        }
    }

    protected open fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View = super.onCreateView(inflater, container, null) ?: Space(inflater.context)

    /**
     * 第一次创建布局时才会调用
     */
    protected open fun onViewCreated(view: View) {}

}