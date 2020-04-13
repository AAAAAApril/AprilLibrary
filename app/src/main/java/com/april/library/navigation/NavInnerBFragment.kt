package com.april.library.navigation

import android.os.Bundle
import android.view.View
import androidx.navigation.navGraphViewModels
import com.april.develop.navigation.AbsNavFragment
import com.april.develop.navigation.pop
import com.april.develop.navigation.popAction
import com.april.library.R
import kotlinx.android.synthetic.main.fragment_nav_inner_b.*

class NavInnerBFragment : AbsNavFragment(R.layout.fragment_nav_inner_b) {

    private val viewModel by navGraphViewModels<InnerViewModel>(R.id.nav_inner)
    private val outerViewModel by navGraphViewModels<OuterViewModel>(R.id.nav_outer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        interceptBackPressed(true)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        fnib_btnBack.setOnClickListener(popAction(force = true))
        fnib_btnGoOut.setOnClickListener {
            viewModel.number = fnib_etName.text.toString()

            //回到外部图表
            outerViewModel.nameLiveData.postValue(viewModel.name)
            outerViewModel.numberLiveData.postValue(viewModel.number)
            pop(R.id.nav_f_outer_b)
        }
    }
}