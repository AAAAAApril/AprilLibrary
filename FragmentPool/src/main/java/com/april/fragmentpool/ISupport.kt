package com.april.fragmentpool

interface ISupport {

    fun getSupport(): Any

    var fragmentPoolRecorder: FragmentPoolRecorder

    fun onBackPressed() {
        onBackPressedInternal()
    }

    fun onBackPressedInternal()
}