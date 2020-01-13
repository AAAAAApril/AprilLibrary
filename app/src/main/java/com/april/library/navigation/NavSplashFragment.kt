package com.april.library.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import com.april.library.R
import kotlinx.android.synthetic.main.nav_fragment_splash.*

/**
 * 启动页
 */
class NavSplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.nav_fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nfs_btn_login.setOnClickListener(navigate(R.id.action_splash_go_login))
        nfs_btn_home.setOnClickListener {
            it.navigate(R.id.action_go_home)
        }

        //FIXME  拦截返回功能
//        ((fragmentManager?.primaryNavigationFragment as? NavHostFragment)
//            ?.navController as? NavHostController)
//            ?.setOnBackPressedDispatcher(
//            OnBackPressedDispatcher().addCallback()
//        )

    }
}