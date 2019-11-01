package com.april.develop.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * 支持懒加载的 Fragment
 *
 * 注意：由于官方最新的默认处理方式是 setMaxLifecycle，
 * 因此，如果要使用这种老方法做懒加载，在 ViewPager 里面的时候，需要给 FragmentPagerAdapter
 * 的 behavior 设置 FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT  这个值。
 */
abstract class LazyFragment : Fragment() {

    @LayoutRes
    protected abstract fun setLayoutRes(): Int

    protected abstract fun onShouldLoadView(contentView: View)

    private var lazyFragmentContentView: View? = null

    private var loaded: Boolean = false

    private var viewCreated: Boolean = false
    private var hidden: Boolean? = null
    private var visibleToUser: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (lazyFragmentContentView == null) {
            lazyFragmentContentView = inflater.inflate(setLayoutRes(), container, false)
        }
        return lazyFragmentContentView
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCreated = true
        check()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        visibleToUser = isVisibleToUser
        check()
    }

    @CallSuper
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        this.hidden = hidden
        check()
    }

    private fun check() {
        if (loaded) {
            return
        }
        if (visibleToUser != null) {
            if (viewCreated && visibleToUser!! && lazyFragmentContentView != null) {
                loaded = true
                onShouldLoadView(lazyFragmentContentView!!)
            }
        } else if (hidden != null) {
            if (viewCreated && !hidden!! && lazyFragmentContentView != null) {
                loaded = true
                onShouldLoadView(lazyFragmentContentView!!)
            }
        }
    }

    override fun onDestroyView() {
        viewCreated = false
        super.onDestroyView()
    }

}