package com.april.fragmentpool

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders

class IActivitySupportImpl : IActivitySupport {

    override fun getSupport(): FragmentActivity = this as FragmentActivity

    override var recorder: FragmentPoolRecorder =
        ViewModelProviders.of(getSupport()).get(FragmentPoolRecorder::class.java)

    override var rootFragmentAddedToBackStack: Boolean = true

    override fun <F> loadRootFragment(
        manager: FragmentManager,
        @IdRes
        containerViewId: Int,
        rootFragmentClass: Class<F>,
        savedInstanceState: Bundle?,
        addToBackStack: Boolean,
        whenRootFragmentIsNull: () -> Fragment,
        onRootFragment: ((F) -> Unit)?
    ) where F : Fragment, F : IFragmentSupport {
        rootFragmentAddedToBackStack = addToBackStack
        var rootFragment: Fragment? = manager.findFragmentByTag(rootFragmentClass.name)
        if (rootFragment == null) {
            rootFragment = whenRootFragmentIsNull.invoke()
            onRootFragment?.invoke(rootFragment as F)
            manager.beginTransaction().let { transition ->
                transition.add(containerViewId, rootFragment, rootFragmentClass.name)
                transition.addToBackStack(rootFragmentClass.name)
                transition.commitAllowingStateLoss()
            }
        }
    }

    override fun onBackPressedInternal() {

    }

}