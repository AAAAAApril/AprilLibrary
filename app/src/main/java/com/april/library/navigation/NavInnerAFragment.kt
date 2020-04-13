package com.april.library.navigation

import android.view.View
import androidx.navigation.navGraphViewModels
import com.april.develop.navigation.AbsNavFragment
import com.april.develop.navigation.push
import com.april.library.R
import kotlinx.android.synthetic.main.fragment_nav_inner_a.*

class NavInnerAFragment : AbsNavFragment(R.layout.fragment_nav_inner_a) {

    private val viewModel by navGraphViewModels<InnerViewModel>(R.id.nav_inner)

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        fnia_btnGo.setOnClickListener {
            viewModel.name = fnia_etName.text.toString()
            push(R.id.nav_a_inner_b)
        }
    }
}