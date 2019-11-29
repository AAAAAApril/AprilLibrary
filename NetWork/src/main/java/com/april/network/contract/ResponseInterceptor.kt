package com.april.network.contract

import android.util.Log
import com.april.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

/**
 * 响应拦截器
 */
object ResponseInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (BuildConfig.DEBUG) {
            val response = chain.proceed(chain.request())
            val body = response.body
            val content = body?.string()
            Log.e("April_请求响应体", content ?: "null")
            response.newBuilder()
                .body(content?.toResponseBody(body.contentType()))
                .build()
        } else {
            chain.proceed(chain.request())
        }
    }
}