package com.april.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * 一个 处理 try catch 的扩展函数
 */
fun ViewModel.tryLaunch(
    onBeforeTry: suspend () -> Unit = {},
    onException: suspend (Exception) -> Unit = {},
    onFinally: suspend () -> Unit = {},
    onTry: suspend () -> Unit
) {
    viewModelScope.launch {
        onBeforeTry.invoke()
        try {
            onTry.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
            onException.invoke(e)
        } finally {
            onFinally.invoke()
        }
    }
}