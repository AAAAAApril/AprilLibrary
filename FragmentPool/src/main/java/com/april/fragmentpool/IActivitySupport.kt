package com.april.fragmentpool

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

interface IActivitySupport : ISupport {

    override fun getSupport(): FragmentActivity

    var rootFragmentAddedToBackStack: Boolean

    fun <F> loadRootFragment(
        @IdRes
        containerViewId: Int,
        rootFragmentClass: Class<F>,
        savedInstanceState: Bundle? = null,
        //这个参数不是给 transition 用的，而只是用来做记录的
        addToBackStack: Boolean = true,
        whenRootFragmentIsNull: () -> F,
        onRootFragment: ((F) -> Unit)? = null
    ) where F : Fragment,
            F : IFragmentSupport

}