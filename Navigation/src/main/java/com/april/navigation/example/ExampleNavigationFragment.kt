package com.april.navigation.example

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.april.navigation.INavigationActivity
import com.april.navigation.INavigationFragment
import com.april.navigation.Navigator

open class ExampleNavigationFragment : Fragment(), INavigationFragment {

    override fun getNavigator(): Navigator? = (activity as? INavigationActivity)?.navigator

    final override fun onDispatchBackPressed() {
        activity?.let {
            if (it is INavigationActivity
                && getNavigator()?.canPopFragment() == true
            ) {
                getNavigator()?.popFragment()
            } else {
                it.onBackPressed()
            }
        }
    }

    final override fun setNavigationResult(resultCode: Int, resultData: Bundle?) {
        (activity as? INavigationActivity)?.navigator?.setNavigationResult(
            this,
            resultCode,
            resultData
        )
    }
}