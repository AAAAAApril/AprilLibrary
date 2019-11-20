package com.april.fragmentpool

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

class IFragmentSupportImpl : IFragmentSupport {

    override fun getSupport(): Fragment = this as Fragment

    override var fragmentPoolRecorder: FragmentPoolRecorder =
        ViewModelProviders.of(getSupport()).get(FragmentPoolRecorder::class.java)

    override fun <F> startFragment(
        fragment: F,
        removeMyself: Boolean
    ) where F : Fragment, F : IFragmentSupport {
        getSupport().activity?.let { activity ->
            assert(activity is IActivitySupport) {
                "host Activity must implement IActivitySupport ."
            }
            val supportFragmentList =
                activity.supportFragmentManager.fragments.filter {
                    return@filter it is IFragmentSupport
                }
            assert(supportFragmentList.isNotEmpty()) {
                "please invoke function IActivitySupport.loadRootFragment first ."
            }
            var lastRemoved = false
            val lastSupportFragment = supportFragmentList.last()
            if (removeMyself) {
                if (supportFragmentList.size == 1) {
                    if ((activity as IActivitySupport).rootFragmentAddedToBackStack) {
                        activity.supportFragmentManager.popBackStack()
                        lastRemoved = true
                    }
                }
            }
            activity.supportFragmentManager.beginTransaction().let { transition ->
                //FIXME 这个判断考虑一下...
//                if (!lastRemoved) {
                transition.hide(lastSupportFragment!!)
//                }
                transition.add(lastSupportFragment.id, fragment, fragment.javaClass.name)
                transition.addToBackStack(fragment.javaClass.name)
                transition.commitAllowingStateLoss()
            }
        }
    }

    override fun <F> startFragmentForResult(
        fragment: F,
        requestCode: Int
    ) where F : Fragment, F : IFragmentSupport {
        //TODO
    }

    override fun onBackPressedInternal() {
        //TODO
    }

}