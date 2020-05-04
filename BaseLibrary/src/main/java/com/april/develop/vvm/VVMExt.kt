package com.april.develop.vvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 由 页面组件 实现
 */
interface IContractView {
    /**
     * 显示 提示文字
     */
    fun onShowToast(message: CharSequence?)

    /**
     * 是否显示加载提示，和提示文字
     */
    fun onShowLoading(state: LoadingState)
}

/**
 * 加载状态
 */
sealed class LoadingState(val tips: CharSequence?) {
    class LoadingStart(tips: CharSequence? = null) : LoadingState(tips)
    class LoadingEnd(tips: CharSequence? = null) : LoadingState(tips)
}

//==================================================================================================

/**
 * 快捷启动任务
 */
fun ViewModel.launch(
    onBefore: () -> Unit = { },
    onException: (Throwable) -> Unit = {
        it.printStackTrace()
    },
    onFinally: () -> Unit = { },
    block: suspend CoroutineScope.() -> Unit
) = viewModelScope.launch(
    CoroutineExceptionHandler { _, throwable ->
        onException.invoke(throwable)
    }
) {
    onBefore.invoke()
    try {
        block.invoke(this)
    } finally {
        onFinally.invoke()
    }
}

/**
 * 刷新任务
 */
fun <T : Any> ViewModel.refresh(
    liveData: StateLiveData<T>,
    onBefore: () -> Unit = {
        liveData.setRefreshing(true)
    },
    onException: (Throwable) -> Unit = {
        it.printStackTrace()
    },
    onFinally: () -> Unit = {
        liveData.setRefreshing(false)
    },
    block: suspend CoroutineScope.() -> T
) = viewModelScope.launch(
    CoroutineExceptionHandler { _, throwable ->
        onException.invoke(throwable)
    }
) {
    onBefore.invoke()
    try {
        val result = block.invoke(this)
        liveData.postValue(result)
    } finally {
        onFinally.invoke()
    }
}

/**
 * 加载更多任务
 */
fun <T : Any> ViewModel.loadMore(
    liveData: StateLiveData<T>,
    onBefore: () -> Unit = {
        liveData.setLoadingMore(true)
    },
    onException: (Throwable) -> Unit = {
        it.printStackTrace()
    },
    onFinally: () -> Unit = {
        liveData.setLoadingMore(false)
    },
    block: suspend CoroutineScope.() -> T
) = viewModelScope.launch(
    CoroutineExceptionHandler { _, throwable ->
        onException.invoke(throwable)
    }
) {
    onBefore.invoke()
    try {
        val result = block.invoke(this)
        liveData.postValue(result)
    } finally {
        onFinally.invoke()
    }
}