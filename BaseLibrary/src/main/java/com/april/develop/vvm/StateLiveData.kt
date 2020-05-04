package com.april.develop.vvm

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * 包含数据状态的 LiveData
 */
class StateLiveData<T : Any> : MutableLiveData<T> {

    constructor() : super()
    constructor(t: T) : super(t)

    /**
     * 刷新状态
     */
    private val refreshState = MutableLiveData<Boolean>()

    /**
     * 加载更多状态
     */
    private val loadingMoreState = MutableLiveData<Boolean>()

    /**
     * 请求状态
     *
     * 综合刷新和加载更多两种状态
     */
    private val requestingState = MediatorLiveData<Boolean>().apply {
        addSource(refreshState) {
            value = loadingMoreState.value == true || it
        }
        addSource(loadingMoreState) {
            value = refreshState.value == true || it
        }
    }

    /**
     * 是否正在刷新
     */
    fun isRefreshing() = refreshState.value == true

    /**
     * 是否正在加载更多
     */
    fun isLoadingMore() = loadingMoreState.value == true

    /**
     * 是否正在请求
     */
    fun isRequesting() = requestingState.value == true

    /**
     * 设置是否正在刷新
     */
    fun setRefreshing(refreshing: Boolean) {
        refreshState.value = refreshing
    }

    /**
     * 设置是否正在加载更多
     */
    fun setLoadingMore(loading: Boolean) {
        loadingMoreState.value = loading
    }

    /**
     * 观察刷新状态
     */
    fun refreshing(owner: LifecycleOwner, observer: Observer<Boolean>) {
        refreshState.observe(owner, observer)
    }

    /**
     * 观察加载更多状态
     */
    fun loadingMore(owner: LifecycleOwner, observer: Observer<Boolean>) {
        loadingMoreState.observe(owner, observer)
    }

    /**
     * 观察请求状态
     */
    fun requesting(owner: LifecycleOwner, observer: Observer<Boolean>) {
        requestingState.observe(owner, observer)
    }

}
