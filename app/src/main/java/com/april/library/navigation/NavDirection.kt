package com.april.library.navigation

import android.net.Uri
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.april.library.R

interface INavDirections : NavDirections {
    fun getDeepLinkUri(): Uri
    fun getNavOptionsBuilder(): NavOptions.Builder {
        return NavOptions.Builder()
//            .setEnterAnim(R.anim.nav_default_enter_anim)
//            .setExitAnim(R.anim.nav_default_exit_anim)
//            .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
//            .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
            .setEnterAnim(R.anim.from_right)
            .setExitAnim(R.anim.to_left)
            .setPopEnterAnim(R.anim.from_left)
            .setPopExitAnim(R.anim.to_right)
    }

    fun getNavigatorExtras(): Navigator.Extras? = null
}

/**
 * 普通行为
 */
open class ActionNavDirection(
    @IdRes private val actionResId: Int,
    private val arguments: Bundle = Bundle()
) : INavDirections {
    override fun getDeepLinkUri(): Uri = Uri.EMPTY

    override fun getArguments(): Bundle = arguments

    override fun getActionId(): Int = actionResId
}

/**
 * 深度链接
 */
open class DeepLinkNavDirection(
    private val deepLink: String
) : INavDirections {
    override fun getDeepLinkUri(): Uri = Uri.parse(deepLink)

    override fun getArguments(): Bundle = Bundle()

    override fun getActionId(): Int = 0
}
