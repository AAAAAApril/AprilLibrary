package com.april.fragmentpool.test

import androidx.fragment.app.Fragment
import com.april.fragmentpool.IFragmentSupport
import com.april.fragmentpool.IFragmentSupportImpl

class ExampleFragment : Fragment(), IFragmentSupport by IFragmentSupportImpl() {
    override fun getSupport(): Fragment = this
}