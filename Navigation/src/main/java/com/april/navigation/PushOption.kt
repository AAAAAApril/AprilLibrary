package com.april.navigation

import android.app.Activity
import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.fragment.app.Fragment

/**
 * 压栈配置
 */
class PushOption(
    //压栈目标（这个 Fragment 需要实现 INavigationFragment 接口！！）
    internal val targetFragment: Fragment,
    //回退栈名
    internal val backStackName: String,
    //回传数据回调接口
    internal val callBack: NavigationResultCallBack? = null,
    //是否移除当前最顶层栈正在显示的这个 Fragment
    internal val removeNow: Boolean = false
) {
    init {
        assert(targetFragment is INavigationFragment) {
            "被压栈的 Fragment 必须实现 INavigationFragment 接口！！！"
        }
    }

    //回传结果数据
    internal var resultCode: Int = Activity.RESULT_CANCELED
    internal var resultData: Bundle? = null

//    val sharedElementView: View? = null
//    val sharedElementName: String? = null

    internal val customAnimations = arrayOf(0, 0, 0, 0)

    /**
     * 设置出入栈动画
     */
    fun setCustomAnimations(
        @AnimatorRes @AnimRes enter: Int,
        @AnimatorRes @AnimRes exit: Int,
        @AnimatorRes @AnimRes popEnter: Int = 0,
        @AnimatorRes @AnimRes popExit: Int = 0
    ) {
        customAnimations[0] = enter
        customAnimations[1] = exit
        customAnimations[2] = popEnter
        customAnimations[3] = popExit
    }

}