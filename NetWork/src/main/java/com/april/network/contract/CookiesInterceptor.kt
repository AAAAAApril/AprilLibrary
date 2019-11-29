package com.april.network.contract

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 添加 Cookie
 */
object AddCookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        for (string in CookiesController.onCookiesAddToHeader()) {
            builder.addHeader("Cookie", string)
        }
        return chain.proceed(builder.build())
    }
}

/**
 * 获取 Cookie
 */
object ReceivedCookiesInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val cookiesSet = mutableSetOf<String>()
        for (cookie in response.headers("Set-Cookie")) {
            cookiesSet.add(cookie)
        }
        CookiesController.onCookiesReceivedFromResponse(cookiesSet)
        return response
    }
}

/**
 * Cookie 控制器
 */
object CookiesController : OnCookiesListener {

    var cookiesListener: OnCookiesListener? = null

    override fun onCookiesReceivedFromResponse(cookies: MutableSet<String>) {
        cookiesListener?.onCookiesReceivedFromResponse(cookies)
    }

    override fun onCookiesAddToHeader(): MutableSet<String> {
        return cookiesListener?.onCookiesAddToHeader() ?: mutableSetOf()
    }

    override fun onAddCookiesFromUser(vararg cookies: String) {
        cookiesListener?.onAddCookiesFromUser(*cookies)
    }

    override fun onClearCookies() {
        cookiesListener?.onClearCookies()
    }
}

interface OnCookiesListener {
    fun onCookiesReceivedFromResponse(
        cookies: MutableSet<String>
    )

    /**
     * [MutableSet] cookies
     */
    fun onCookiesAddToHeader(): MutableSet<String>

    fun onAddCookiesFromUser(
        vararg cookies: String
    )

    fun onClearCookies()
}