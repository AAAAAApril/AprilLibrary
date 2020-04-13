package com.april.library.navigation

import android.view.View
import com.april.develop.navigation.AbsNavFragment
import com.april.develop.navigation.pushAction
import com.april.library.R
import kotlinx.android.synthetic.main.fragment_nav_outer_a.*

class NavOuterAFragment : AbsNavFragment(R.layout.fragment_nav_outer_a) {
    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        fnoa_btn.setOnClickListener(pushAction(R.id.nav_a_outer_b))
    }
}