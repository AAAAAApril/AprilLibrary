package com.april.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

/**
 * 一个 处理 try catch 的扩展函数
 */
fun ViewModel.tryLaunch(
    onBeforeTry: () -> Unit = {},
    onException: (Throwable) -> Unit = {},
    onFinally: () -> Unit = {},
    onTry: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launch(
        CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onException.invoke(throwable)
        }
    ) {
        onBeforeTry.invoke()
        try {
            onTry.invoke(this)
        } finally {
            onFinally.invoke()
        }
    }
}

fun ViewModel.tryLaunchDSL(block: ViewModelTryLaunch.() -> Unit): Job {
    return ViewModelTryLaunch().apply(block).let {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
                it.onException.invoke(throwable)
            }
        ) {
            it.onBeforeTry.invoke()
            try {
                it.onTry.invoke(this)
            } finally {
                it.onFinally.invoke()
            }
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

class ViewModelTryLaunch {
    var onBeforeTry: () -> Unit = { }
    var onTry: suspend CoroutineScope.() -> Unit = {}
    var onException: ((Throwable) -> Unit) = {}
    var onFinally: () -> Unit = { }
}