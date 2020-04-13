package com.april.library.navigation

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import com.april.develop.navigation.AbsNavFragment
import com.april.develop.navigation.popAction
import com.april.develop.navigation.pushAction
import com.april.library.R
import kotlinx.android.synthetic.main.fragment_nav_outer_b.*

class NavOuterBFragment : AbsNavFragment(R.layout.fragment_nav_outer_b) {

    private val outerViewModel by navGraphViewModels<OuterViewModel>(R.id.nav_outer)

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        fnob_btnBack.setOnClickListener(popAction(force = true))
        fnob_btnGo.setOnClickListener(pushAction(R.id.nav_a_inner))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        outerViewModel.nameLiveData.observe(viewLifecycleOwner, Observer {
            fnob_tvName.text = it
        })
        outerViewModel.numberLiveData.observe(viewLifecycleOwner, Observer {
            fnob_tvNumber.text = it
        })
    }
}