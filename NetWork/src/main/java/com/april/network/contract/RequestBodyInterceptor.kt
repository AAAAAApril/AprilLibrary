package com.april.network.contract

import android.util.Log
import com.april.network.BuildConfig
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.Response
import java.io.IOException

/**
 * 请求参数 拦截器
 */
object RequestBodyInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var newRequest = chain.request()

        /*
            添加统一的 loginToken
         */
        if (newRequest.method == "POST") {
            //文件类型
            if (newRequest.body is MultipartBody) {
                //do nothing
            }
            //字符类型
            else {
//                if (loginToken.isNotEmpty()) {
//                    val builder = FormBody.Builder()
//                        .add(LoginTokenKey, loginToken)
//                    //纯字符类型
//                    (newRequest.body as? FormBody)?.let { body ->
//                        for (index in 0 until body.size) {
//                            //这里不使用编码之后的值
//                            builder.add(
//                                body.name(index),
//                                body.value(index)
//                            )
//                        }
//                    }
//                    newRequest = newRequest
//                        .newBuilder()
//                        .post(builder.build())
//                        .build()
//                }
            }
        }

        if (BuildConfig.DEBUG) {
            Log.e("April_RequestBody", "Url：${newRequest.url}")
            Log.e("April_RequestBody", "是否Https：${newRequest.isHttps}")
            Log.e("April_RequestBody", "Method：${newRequest.method}")
            val headers = newRequest.headers
            for (index in 0 until headers.size) {
                Log.e(
                    "April_RequestBody",
                    "请求头名：${headers.name(index)}，请求头值：${headers.value(index)}"
                )
            }
            //表单数据
            (newRequest.body as? FormBody)?.let { body ->
                for (index in 0 until body.size) {
                    Log.e(
                        "April_FormBody",
                        "请求体名(编码后)：${body.encodedName(index)}，" +
                                "请求体值(编码后)：${body.encodedValue(index)}"
                    )
                    Log.e(
                        "April_FormBody",
                        "请求体名(解码后)：${body.name(index)}，" +
                                "请求体值(解码后)：${body.value(index)}"
                    )
                }
            }
            //  MultipartBody
            (newRequest.body as? MultipartBody)?.let {
                Log.e(
                    "April_MultipartBody",
                    "文件数量：${it.size}"
                )
                Log.e(
                    "April_MultipartBody",
                    "contentType：${it.contentType()}"
                )
                Log.e(
                    "April_MultipartBody",
                    "contentLength：${it.contentLength()}"
                )
            }
        }
        return chain.proceed(newRequest)
    }
}