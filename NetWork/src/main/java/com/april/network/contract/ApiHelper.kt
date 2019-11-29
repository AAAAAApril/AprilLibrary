package com.april.network.contract

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.fastjson.FastJsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 工具类
 */
object ApiHelper {

    private val httpClientBuilder: OkHttpClient.Builder by lazy {
        OkHttpClient.Builder()
            //连接超时
            .connectTimeout(30, TimeUnit.SECONDS)
            //读超时
            .readTimeout(30, TimeUnit.SECONDS)
            //写超时
            .writeTimeout(30, TimeUnit.SECONDS)
            //日志拦截器
            .addInterceptor(
                HttpLoggingInterceptor(HttpLogger).apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            //请求头拦截器
            .addInterceptor(HeaderInterceptor)
            //请求体拦截器
            .addInterceptor(RequestBodyInterceptor)
            //响应拦截器
            .addInterceptor(ResponseInterceptor)
            //添加 cookies 拦截器
            .addInterceptor(AddCookiesInterceptor)
            //获取 cookies 拦截器
            .addInterceptor(ReceivedCookiesInterceptor)
    }

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            //主域名
            .baseUrl(Contract.HostUrl)
            //支持 FastJson 转换
            .addConverterFactory(FastJsonConverterFactory.create())
            //支持解析 String
            .addConverterFactory(ScalarsConverterFactory.create())
    }

    private val retrofit: Retrofit by lazy {
        retrofitBuilder.client(httpClientBuilder.build())
        retrofitBuilder.build()
    }

    private val api: Api by lazy {
        create(Api::class.java)
    }

    /**
     * 构建 接口 代理
     */
    fun <T> create(serverClass: Class<T>): T {
        return retrofit.create(serverClass)
    }

    /**
     * 获取接口代理
     */
    fun api(): Api {
        return api
    }

}