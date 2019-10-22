package com.april.multiple.loadmore

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * loadMore 加载更多
 */
open class LoadMoreWrapper(
    private val targetRecyclerView: RecyclerView,
    @LayoutRes private val loadMoreLayoutRes: Int
) {

    internal lateinit var loadMoreView: View

    internal fun createLoadMoreView() {
        loadMoreView = LayoutInflater.from(targetRecyclerView.context)
            .inflate(loadMoreLayoutRes, targetRecyclerView, false)
    }

    internal open fun onViewAttachedToWindow() {

    }

    internal open fun onViewDetachedFromWindow() {

    }

}