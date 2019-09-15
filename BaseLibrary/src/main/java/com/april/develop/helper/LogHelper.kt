package com.april.develop.helper

import android.util.Log
import com.april.develop.BuildConfig

/**
 * 打印日志
 *
 * [tag] TAG
 * [debugOnly] 是否只在 DEBUG 状态显示
 */
fun Any.v(tag: String = BuildConfig.APPLICATION_ID, debugOnly: Boolean = true): Any {
    if (!BuildConfig.DEBUG && debugOnly) {
        return this
    }
    Log.v(tag, this.toString())
    return this
}

fun Any.d(tag: String = BuildConfig.APPLICATION_ID, debugOnly: Boolean = true): Any {
    if (!BuildConfig.DEBUG && debugOnly) {
        return this
    }
    Log.d(tag, this.toString())
    return this
}

fun Any.i(tag: String = BuildConfig.APPLICATION_ID, debugOnly: Boolean = true): Any {
    if (!BuildConfig.DEBUG && debugOnly) {
        return this
    }
    Log.i(tag, this.toString())
    return this
}

fun Any.w(tag: String = BuildConfig.APPLICATION_ID, debugOnly: Boolean = true): Any {
    if (!BuildConfig.DEBUG && debugOnly) {
        return this
    }
    Log.w(tag, this.toString())
    return this
}

fun Any.e(tag: String = BuildConfig.APPLICATION_ID, debugOnly: Boolean = true): Any {
    if (!BuildConfig.DEBUG && debugOnly) {
        return this
    }
    Log.e(tag, this.toString())
    return this
}