package com.april.fragmentpool

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders

class IActivitySupportImpl : IActivitySupport {

    override fun getSupport(): FragmentActivity = this as FragmentActivity

    override var fragmentPoolRecorder: FragmentPoolRecorder =
        ViewModelProviders.of(getSupport()).get(FragmentPoolRecorder::class.java)

    override var rootFragmentAddedToBackStack: Boolean = true

    override fun <F> loadRootFragment(
        @IdRes
        containerViewId: Int,
        rootFragmentClass: Class<F>,
        savedInstanceState: Bundle?,
        addToBackStack: Boolean,
        whenRootFragmentIsNull: () -> F,
        onRootFragment: ((F) -> Unit)?
    ) where F : Fragment, F : IFragmentSupport {
        rootFragmentAddedToBackStack = addToBackStack
        val manager = getSupport().supportFragmentManager
        var rootFragment: Fragment? = manager.findFragmentByTag(rootFragmentClass.name)
        if (rootFragment == null) {
            rootFragment = whenRootFragmentIsNull.invoke()
            onRootFragment?.invoke(rootFragment)
            manager.beginTransaction().let { transition ->
                transition.add(containerViewId, rootFragment, rootFragmentClass.name)
                transition.addToBackStack(rootFragmentClass.name)
                transition.commitAllowingStateLoss()
            }
        }
    }

    override fun onBackPressedInternal() {
        val suortFragmentList = getSupport().supportFragmentManager.fragments.filter {
            return@filter it is IFragmentSupport
        }
        //TODO
    }

}