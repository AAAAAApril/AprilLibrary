package com.april.develop.navigation

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.april.develop.R
import com.april.develop.activity.AbsActivity

/**
 * Navigation Activity 基类
 */
abstract class AbsNavActivity
    : AbsActivity(R.layout.layout_single_fragment_container_view),
    INavActivity {

    override fun getNavigationContainerViewId(): Int = R.id.lsfcv_fcv

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * 加载 NavHostFragment
         */
        if (savedInstanceState == null) {
            val hostFragment = NavHostFragment.create(getNavigationGraphResId())
            supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(
                    getNavigationContainerViewId(),
                    hostFragment,
                    NavHostFragment::class.java.name
                )
                .setPrimaryNavigationFragment(hostFragment)
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(getNavigationContainerViewId()).navigateUp()
    }

}