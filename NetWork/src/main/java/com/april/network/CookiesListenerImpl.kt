package com.april.network

import android.content.Context
import com.april.network.contract.OnCookiesListener

/**
 * 监听、管理 Cookies
 */
class CookiesListenerImpl(
    context: Context
) : OnCookiesListener {

    private val preferencesFileName = "April_${context.packageName}"
    private val cookiesKey = "${context.packageName}_Cookies_Key"

    private val sharedPreferences by lazy {
        context.getSharedPreferences(
            preferencesFileName,
            Context.MODE_PRIVATE
        )
    }

    // Cookies 缓存 set
    private val cookiesCacheSet = mutableSetOf<String>()

    override fun onCookiesReceivedFromResponse(cookies: MutableSet<String>) {
        cookiesCacheSet.clear()
        cookiesCacheSet.addAll(cookies)
        //持久存储
        sharedPreferences
            .edit()
            .putStringSet(cookiesKey, cookiesCacheSet)
            .apply()
    }

    override fun onCookiesAddToHeader(): MutableSet<String> {
        return cookiesCacheSet
    }

    override fun onAddCookiesFromUser(vararg cookies: String) {
        cookiesCacheSet.addAll(cookies)
        //持久层更新
        sharedPreferences
            .edit()
            .putStringSet(cookiesKey, cookiesCacheSet)
            .apply()
    }

    override fun onClearCookies() {
        cookiesCacheSet.clear()
        //持久层清除
        sharedPreferences
            .edit()
            .remove(cookiesKey)
            .apply()
    }
}