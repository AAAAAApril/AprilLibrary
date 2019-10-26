package com.april.develop.helper

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

/**
 * [dp] [Float] dp 转 px
 */
fun Context.dp2px(dp: Float): Int {
    if (dp <= 0) {
        return 0
    }
    return (dp * (resources.displayMetrics.density) + 0.5f).toInt()
}

/**
 * [px] [Float] px 转 dp
 */
fun Context.px2dp(px: Int): Float {
    if (px <= 0) {
        return 0.0f
    }
    return px / (resources.displayMetrics.density) + 0.5f
}

/**
 * [Int] 获取系统状态栏高度
 */
fun Context.statusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier(
        "status_bar_height",
        "dimen",
        "android"
    )
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

/**
 * [url] 用系统浏览器加载链接
 */
fun Context.loadWebUrl(url: String) {
    startActivity(
        Intent(
            Intent.ACTION_VIEW, Uri.parse(url)
        )
    )
}

/**
 * [packageName] 检查该安装包是否存在
 */
fun Context.isPackageAvailable(packageName: String): Boolean {
    //获取所有已安装程序的包信息
    val packageInfoList = packageManager.getInstalledPackages(0)
    if (packageInfoList != null) {
        for (info in packageInfoList) {
            if (packageName == info.packageName) {
                return true
            }
        }
    }
    return false
}

/**
 * [phone] 系统拨号盘，拨打电话
 */
fun Context.dial(phone: String) {
    startActivity(
        Intent(
            Intent.ACTION_DIAL, Uri.parse("tel:$phone")
        )
    )
}

/**
 * 应用的 versionName
 * [prefix] 前缀
 * [default] 出错时默认值
 */
fun Context.appVersionName(prefix: String = "V", default: String = "1.0.0"): String {
    val version: String
    version = try {
        val info = packageManager.getPackageInfo(packageName, 0)
        info.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        default
    }
    return "$prefix$version"
}