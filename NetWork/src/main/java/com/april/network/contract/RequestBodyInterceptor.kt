package com.april.network.contract

import android.util.Log
import com.april.network.BuildConfig
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 请求参数 拦截器
 */
object RequestBodyInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (BuildConfig.DEBUG) {
            Log.e("April_RequestBody", "Url：${request.url}")
            Log.e("April_RequestBody", "是否Https：${request.isHttps}")
            Log.e("April_RequestBody", "Method：${request.method}")
            val headers = request.headers
            for (index in 0 until headers.size) {
                Log.e(
                    "April_RequestBody",
                    "请求头名：${headers.name(index)}，请求头值：${headers.value(index)}"
                )
            }
            (request.body as? FormBody)?.let { body ->
                for (index in 0 until body.size) {
                    Log.e(
                        "April_RequestBody",
                        "请求体名(编码后)：${body.encodedName(index)}，" +
                                "请求头值(编码后)：${body.encodedValue(index)}"
                    )
                    Log.e(
                        "April_RequestBody",
                        "请求体名(解码后)：${body.name(index)}，" +
                                "请求头值(解码后)：${body.value(index)}"
                    )
                }
            }
            //可以考虑打印  MultipartBody  相关信息
//            (request.body() as? MultipartBody)?.let {
//
//            }
        }
        return chain.proceed(request)
    }
}