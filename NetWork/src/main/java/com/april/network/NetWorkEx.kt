package com.april.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 一个 处理 try catch 的扩展函数
 */
fun ViewModel.tryLaunch(
    onBeforeTry: () -> Unit = {},
    onException: (Exception) -> Unit = {},
    onFinally: () -> Unit = {},
    onTry: suspend () -> Unit
): Job {
    return viewModelScope.launch {
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

fun ViewModel.countDown(
    maxCount: Int = 60,
    running: suspend (Int) -> Unit
): Job {
    return viewModelScope.launch {
        repeat(maxCount) {
            running.invoke(maxCount - (it + 1))
            delay(1000)
        }
    }
}