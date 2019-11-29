package com.april.network.contract

import com.april.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 请求头拦截器
 */
object HeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (BuildConfig.DEBUG) {
            //TODO
            chain.proceed(chain.request())
        } else {
            chain.proceed(chain.request())
        }
    }
}