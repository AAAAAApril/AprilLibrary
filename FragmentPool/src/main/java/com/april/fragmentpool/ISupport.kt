package com.april.fragmentpool

interface ISupport {

    fun getSupport(): Any

    var recorder: FragmentPoolRecorder

    fun onBackPressed() {
        onBackPressedInternal()
    }

    fun onBackPressedInternal()
}