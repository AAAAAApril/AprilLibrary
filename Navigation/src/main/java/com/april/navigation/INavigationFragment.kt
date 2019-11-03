package com.april.navigation

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment

interface INavigationFragment {

    fun getNavigator(): Navigator?

    /**
     * 返回操作，可以通过这个函数出栈
     */
    fun onBackPressed() {
        onDispatchBackPressed()
    }

    /**
     * 出栈的真正操作函数，可以利用它来强制出栈
     *
     * 调用 [NavigationController.popFragment]
     */
    fun onDispatchBackPressed()

    /**
     * 设置回传数据
     */
    fun setNavigationResult(
        //结果码
        resultCode: Int,
        //结果数据
        resultData: Bundle?
    )

}

//==================================================================================================

fun <T> T.setNavigationResult(
    //结果数据
    resultData: Bundle,
    //结果码
    resultCode: Int = Activity.RESULT_OK
) where T : Fragment,
        T : INavigationFragment {
    getNavigator()?.setNavigationResult(
        this,
        resultCode,
        resultData
    )
}
