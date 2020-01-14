package com.april.library.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.april.library.R
import kotlinx.android.synthetic.main.nav_fragment_splash.*

/**
 * 启动页
 */
class NavSplashFragment : AbsNavFragment() {
    override fun onCreateViewFirst(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.nav_fragment_splash, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nfs_btn_login.setOnClickListener(navigate(R.id.action_splash_go_login))
        nfs_btn_home.setOnClickListener {
            it.navigate(R.id.action_go_home)
        }
    }
}