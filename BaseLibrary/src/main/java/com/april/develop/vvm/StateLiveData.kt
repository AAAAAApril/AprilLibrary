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
    private val refreshingState = MutableLiveData<Boolean>()

    /**
     * 加载更多状态
     */
    private val loadingMoreState = MutableLiveData<Boolean>()

    /**
     * 请求状态
     *
     * 综合刷新和加载更多两种状态，满足任意一种即为正在请求
     */
    private val requestingState = MediatorLiveData<Boolean>().apply {
        addSource(refreshingState) {
            value = isLoadingMore() || it
        }
        addSource(loadingMoreState) {
            value = isRefreshing() || it
        }
    }

    /**
     * 是否正在刷新
     */
    fun isRefreshing() = refreshingState.value == true

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
        refreshingState.postValue(refreshing)
    }

    /**
     * 设置是否正在加载更多
     */
    fun setLoadingMore(loading: Boolean) {
        loadingMoreState.postValue(loading)
    }

    /**
     * 观察刷新状态
     */
    fun observerRefreshing(owner: LifecycleOwner, observer: Observer<Boolean>) {
        refreshingState.observe(owner, observer)
    }

    /**
     * 观察加载更多状态
     */
    fun observerLoadingMore(owner: LifecycleOwner, observer: Observer<Boolean>) {
        loadingMoreState.observe(owner, observer)
    }

    /**
     * 观察请求状态
     */
    fun observerRequesting(owner: LifecycleOwner, observer: Observer<Boolean>) {
        requestingState.observe(owner, observer)
    }

    /**
     * 移除刷新状态观察者
     */
    fun removeRefreshingObserver(observer: Observer<Boolean>) {
        refreshingState.removeObserver(observer)
    }

    /**
     * 移除加载更多状态观察者
     */
    fun removeLoadingMoreObserver(observer: Observer<Boolean>) {
        loadingMoreState.removeObserver(observer)
    }

    /**
     * 移除请求状态观察者
     */
    fun removeRequestingObserver(observer: Observer<Boolean>) {
        requestingState.removeObserver(observer)
    }

}
