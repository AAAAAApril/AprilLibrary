package com.april.network.contract

import com.april.network.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor

/**
 * http 日志
 */
object HttpLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        if (BuildConfig.DEBUG) {
//            Log.e("April_HttpLogger", message)
        }
    }
}