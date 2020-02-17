package com.april.library.navigation

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

/**
 * 自定义导航扩展
 */

//普通行为

fun Fragment.navigate(@IdRes actionResId: Int): View.OnClickListener {
    return navigate(ActionNavDirection(actionResId))
}

fun Fragment.navigate(directions: ActionNavDirection): View.OnClickListener {
    return View.OnClickListener {
        it.navigate(directions)
    }
}

fun View.navigate(@IdRes actionResId: Int) {
    navigate(ActionNavDirection(actionResId))
}

fun View.navigate(directions: ActionNavDirection) {
    findNavController().navigate(
        directions.actionId,
        directions.arguments,
        directions.getNavOptionsBuilder().build(),
        directions.getNavigatorExtras()
    )
}

fun Fragment.navigateUp(): View.OnClickListener {
    return View.OnClickListener {
        it.navigateUp()
    }
}

fun View.navigateUp() {
    findNavController().navigateUp()
}

// 深度链接

fun Fragment.deepLink(deepLink: String): View.OnClickListener {
    return deepLink(DeepLinkNavDirection(deepLink))
}

fun Fragment.deepLink(directions: DeepLinkNavDirection): View.OnClickListener {
    return View.OnClickListener {
        it.deepLink(directions)
    }
}

fun View.deepLink(deepLink: String) {
    deepLink(DeepLinkNavDirection(deepLink))
}

fun View.deepLink(directions: DeepLinkNavDirection) {
    findNavController().navigate(
        directions.getDeepLinkUri(),
        directions.getNavOptionsBuilder().build(),
        directions.getNavigatorExtras()
    )
}