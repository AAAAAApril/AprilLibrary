package com.april.network

import android.content.Context
import android.content.SharedPreferences
import com.april.network.contract.OnCookiesListener

/**
 * 监听、管理 Cookies
 */
object CookiesListenerImpl : OnCookiesListener {

    private const val cookiesSharedPreferencesFileName = "April_Cookies_SharedPreferences_File_Name"
    private const val cookiesKey = "April_Cookies_Key"

    private lateinit var sharedPreferences: SharedPreferences

    // Cookies 缓存 set
    private val cookiesCacheSet = mutableSetOf<String>()

    fun initSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences(
            cookiesSharedPreferencesFileName,
            Context.MODE_PRIVATE
        )

        cookiesCacheSet.clear()
        cookiesCacheSet.addAll(
            sharedPreferences.getStringSet(
                cookiesKey,
                cookiesCacheSet
            )!!
        )
    }

    override fun onCookiesReceivedFromResponse(cookies: MutableSet<String>) {
        cookiesCacheSet.clear()
        cookiesCacheSet.addAll(cookies)
        //持久存储
        sharedPreferences
            .edit()
            .putStringSet(
                cookiesKey,
                cookiesCacheSet
            )
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
            .putStringSet(
                cookiesKey,
                cookiesCacheSet
            )
            .apply()
    }

    override fun isCookiesExist(): Boolean {
        return cookiesCacheSet.isNotEmpty()
    }

    override fun doClearCookies() {
        cookiesCacheSet.clear()
        //持久层清除
        sharedPreferences
            .edit()
            .remove(cookiesKey)
            .apply()
    }
}