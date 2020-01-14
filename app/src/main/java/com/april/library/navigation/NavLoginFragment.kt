package com.april.library.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.april.library.R
import kotlinx.android.synthetic.main.nav_fragment_login.*

/**
 * 登录页
 */
class NavLoginFragment : AbsNavFragment() {
    override fun onCreateViewFirst(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.nav_fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nfl_go_register.setOnClickListener(navigate(R.id.action_login_go_register))
        nfl_go_home.setOnClickListener {
            it.navigate(R.id.action_go_home)
        }
    }
}