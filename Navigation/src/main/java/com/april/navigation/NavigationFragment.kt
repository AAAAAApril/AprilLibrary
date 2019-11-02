package com.april.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment

open class NavigationFragment : Fragment(), INavigationFragment {

    final override fun onDispatchBackPressed() {
        activity?.let {
            if (it is INavigationActivity) {
                if (it.navigator.canPopFragment()) {
                    it.navigator.popFragment()
                }
            } else {
                onBackPressed()
            }
        }
    }

    final override fun setNavigationResult(resultCode: Int, resultData: Bundle?) {
        (activity as? INavigationActivity)?.navigator?.setNavigationResult(this, resultCode, resultData)
    }
}