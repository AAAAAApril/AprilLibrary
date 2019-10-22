package com.april.multiple.loadmore

/**
 * 加载更多 监听器
 */
interface LoadMoreListener {

    /**
     * 触发了 加载更多 操作
     */
    fun onShouldLoadMore(adapter: LoadMoreAdapter)
}