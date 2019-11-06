package com.april.navigation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


class NavigationController(
    internal val manager: FragmentManager,
    @IdRes
    internal val containerViewID: Int
) {

    //记录最顶层的压栈
    private val pushStackOptionList = mutableListOf<PushOption>()

    /**
     * 压栈 操作
     */
    internal fun pushFragment(option: PushOption) {
        //需要移除当前这个
        if (option.removeNow
            //必须要存在至少一个 Fragment
            && pushStackOptionList.size > 0
        ) {
            val lastOption = pushStackOptionList.last()
            pushStackOptionList.remove(lastOption)
            manager.popBackStack()
            manager.fragments.remove(lastOption.targetFragment)
        }
        //执行压栈任务
        manager.beginTransaction().also { transition ->
            //如果已经有一个 Fragment 了，就隐藏它
            pushStackOptionList.lastOrNull()?.let { lastOption ->
                transition.hide(lastOption.targetFragment)
            }
            //添加新的 Fragment
            transition.add(containerViewID, option.targetFragment)
//            if (option.sharedElementView != null
//                && option.sharedElementName != null
//            ) {
//                transition.addSharedElement(
//                    option.sharedElementView,
//                    option.sharedElementName
//                )
//            }
            //Fragment 出入栈动画
            transition.setCustomAnimations(
                option.customAnimations[0],
                option.customAnimations[1],
                option.customAnimations[2],
                option.customAnimations[3]
            )
            //压入回退栈
            transition.addToBackStack(option.backStackName)
            //记录下这次压栈
            pushStackOptionList.add(option)
        }
            //提交
            .commitAllowingStateLoss()
    }

    /**
     * 出栈 操作
     *
     * [backStackName] 需要被弹出栈的 回退栈名
     */
    internal fun popFragment(
        backStackName: String? = null,
        flag: Int = FragmentManager.POP_BACK_STACK_INCLUSIVE
    ) {
        if (!canPopFragment()) {
            return
        }
        if (backStackName == null) {
            pushStackOptionList.lastOrNull()?.let {
                popFragmentInternal(it)
            }
        } else {
            val option = pushStackOptionList.findLast {
                it.backStackName == backStackName
            } ?: return
            if (option == pushStackOptionList.last()) {
                popFragmentInternal(option)
            } else {
                popFragmentInternalByName(option, backStackName, flag)
            }
        }
    }

    /**
     * 出栈所有的 Fragment
     */
    internal fun popFragmentAll() {
        for (i in 0 until manager.backStackEntryCount) {
            manager.popBackStackImmediate()
        }
        for (option in pushStackOptionList) {
            manager.fragments.remove(option.targetFragment)
        }
        pushStackOptionList.clear()
    }

    /**
     * 回退
     */
    private fun popFragmentInternal(option: PushOption) {
        manager.popBackStackImmediate()
        option.callBack?.onNavigationResult(option.resultCode, option.resultData)
        manager.fragments.remove(option.targetFragment)
        pushStackOptionList.remove(option)
    }

    /**
     * 回退
     *
     * 包含 [backStackName] 所对应的 操作
     * [flag] 0 或者 POP_BACK_STACK_INCLUSIVE，即：不包含 或者 包含。
     * 如果包含，则这个 回退名所对应的 Fragment 会被移除
     */
    private fun popFragmentInternalByName(
        option: PushOption,
        backStackName: String,
        flag: Int = FragmentManager.POP_BACK_STACK_INCLUSIVE
    ) {
        //backStackName 对应的操作以后的所有
        val subList = pushStackOptionList.subList(
            pushStackOptionList.indexOf(option),
            pushStackOptionList.size
        )
        //出栈
        manager.popBackStackImmediate(
            backStackName,
            flag
        )
        for (removeOption in subList) {
            manager.fragments.remove(removeOption.targetFragment)
        }
        //移除以后的所有
        pushStackOptionList.removeAll(subList)
    }

    /**
     * [Boolean] 是否还有 Fragment 可以回退
     */
    internal fun onBackPressed(): Boolean {
        val can = canPopFragment()
        if (can) {
            pushStackOptionList.last().let {
                (it.targetFragment as INavigationFragment).onBackPressed()
            }
        }
        return can
    }

    /**
     * [Boolean] 是否可以弹出 Fragment
     */
    internal fun canPopFragment(): Boolean {
        return pushStackOptionList.size > 1
    }

    /**
     * 设置导航回传数据
     */
    internal fun setNavigationResult(
        //回传数据的 Fragment
        fragment: Fragment,
        //结果码
        resultCode: Int,
        //结果数据
        resultData: Bundle?
    ) {
        pushStackOptionList.findLast {
            it.targetFragment == fragment
        }?.let {
            it.resultCode = resultCode
            it.resultData = resultData
        }
    }

}