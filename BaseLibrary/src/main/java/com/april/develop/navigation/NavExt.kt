package com.april.develop.navigation

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.april.develop.R

/**
 * 导航到新页面
 */
fun Fragment.push(
    @IdRes actionId: Int,
    arguments: Bundle = Bundle()
) {
    pushAndPop(actionId, -1, arguments)
}

fun Fragment.pushAction(
    @IdRes actionId: Int,
    arguments: Bundle = Bundle()
): View.OnClickListener = pushAndPopAction(actionId, -1, arguments)

/**
 * 导航到新页面，并回退到对应的页面
 */
fun Fragment.pushAndPop(
    @IdRes pushActionId: Int,
    @IdRes popToDestinationId: Int,
    pushArguments: Bundle = Bundle(),
    popInclusive: Boolean = false
) {
    findNavController().navigate(
        pushActionId,
        pushArguments,
        createNavOptionsBuilder()
            .setPopUpTo(popToDestinationId, popInclusive)
            .build()
    )
}

fun Fragment.pushAndPopAction(
    @IdRes pushActionId: Int,
    @IdRes popToDestinationId: Int,
    pushArguments: Bundle = Bundle(),
    popInclusive: Boolean = false
): View.OnClickListener = View.OnClickListener {
    pushAndPop(pushActionId, popToDestinationId, pushArguments, popInclusive)
}

/**
 * 回退到对应的页面
 *
 * [destinationId] 回退到的目标导航 id，即：navigation 文件内，fragment 标签对应的 id
 * [inclusive] 是否也将对应位置上的 fragment 移除掉
 * [force] 是否强制回退(强制回退不会考虑拦截回退功能的相关逻辑)，这个值只在 [destinationId] 为 null 时才有用
 */
fun Fragment.pop(
    @IdRes destinationId: Int? = null,
    inclusive: Boolean = false,
    force: Boolean = false
) {
    if (destinationId == null) {
        if (force) {
            findNavController().popBackStack()
        } else {
            activity?.onBackPressed()
        }
    } else {
        findNavController().popBackStack(destinationId, inclusive)
    }
}

fun Fragment.popAction(
    @IdRes destinationId: Int? = null,
    inclusive: Boolean = false,
    force: Boolean = false
): View.OnClickListener = View.OnClickListener {
    pop(destinationId, inclusive, force)
}

/**
 * 对 Activity 添加的一个导航扩展
 */
fun <A> A.push(
    @IdRes actionId: Int,
    arguments: Bundle = Bundle()
) where A : ComponentActivity,
        A : INavActivity {
    findNavController(getNavigationContainerViewId()).navigate(
        actionId,
        arguments,
        createNavOptionsBuilder().build()
    )
}

/**
 * 创建导航配置 builder
 */
private fun createNavOptionsBuilder(): NavOptions.Builder {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.nav_default_enter_anim)
        .setExitAnim(R.anim.nav_default_exit_anim)
        .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
        .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
}
