package com.april.fragmentpool

import androidx.fragment.app.Fragment

interface IFragmentSupport :ISupport{

    override fun getSupport(): Fragment

    fun <F> startFragment(
        fragment: F,
        removeMyself: Boolean = false
    ) where F : Fragment,
            F : IFragmentSupport

    fun <F> startFragmentForResult(
        fragment: F,
        requestCode: Int = 0
    ) where F : Fragment,
            F : IFragmentSupport

}