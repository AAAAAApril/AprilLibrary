package com.april.library.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.april.library.R
import kotlinx.android.synthetic.main.nav_fragment_register.*

class NavRegisterFragment : AbsNavFragment() {
    override fun onCreateViewFirst(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.nav_fragment_register, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nfr_btn_go_login.setOnClickListener(navigateUp())
    }
}