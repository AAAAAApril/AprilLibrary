package com.april.multiple.loadmore

import androidx.core.util.containsValue
import androidx.recyclerview.widget.RecyclerView
import com.april.multiple.HeaderFooterAdapter
import com.april.multiple.SpecialItemDelegate

/**
 * 加载更多 Adapter
 */
open class LoadMoreAdapter : HeaderFooterAdapter() {

    //是否可以加载更多
    private var canLoadMore = false
    //是否正在加载更多
    private var isLoadingMore = false
    //加载更多 布局包装
    private var wrapper: SpecialItemDelegate<*>? = null
    //加载更多监听
    private var loadMoreListener: LoadMoreListener? = null

    //RecyclerView 滚动监听
    private val recyclerViewScrollListener = RecyclerViewLoadMoreListener {
        //正在加载更多，不处理
        if (isLoadingMore()) {
            return@RecyclerViewLoadMoreListener
        }
        //不能加载更多了，不处理
        if (!canLoadMore()) {
            return@RecyclerViewLoadMoreListener
        }
        //空数据列的时候，应该做的操作是刷新，而不是加载更多
        if (support.dataList.isEmpty()) {
            return@RecyclerViewLoadMoreListener
        }
        //已经到底部了
        loadMoreListener?.onShouldLoadMore(this)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(recyclerViewScrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeOnScrollListener(recyclerViewScrollListener)
    }

    /**
     * @return LoadMore View
     */
    fun <T : SpecialItemDelegate<*>> setLoadMoreItemDelegate(delegate: T) {
        this.wrapper = delegate
    }

    /**
     * 设置加载更多监听
     */
    fun setLoadMoreListener(loadMoreListener: LoadMoreListener?) {
        this.loadMoreListener = loadMoreListener
        setCanLoadMore(true)
    }

    /**
     * 显示或者隐藏 加载更多 View
     *
     * 注：在调用 [showLoadMoreView(true)] 之前，必须确保 Footer 已经添加完成，
     * 否则可能导致 Footer 的位置与 [loadMoreView] 的位置错乱
     */
    fun showLoadMoreView(show: Boolean) {
        wrapper?.let {
            //是否有这个 FooterView
            val contains = support.footerArray.containsValue(it)
            //显示 且 不包含
            if (show && !contains) {
                addFooter(it)
            }
            //不显示 且 包含
            if (!show && contains) {
                removeFooter(it)
            }
        }
    }

    /**
     * @return 是否可以加载更多
     */
    fun canLoadMore(): Boolean {
        return canLoadMore
    }

    /**
     * @param can 是否还可以加载更多
     */
    fun setCanLoadMore(can: Boolean) {
        canLoadMore = can
    }

    /**
     * 加载更多
     *
     * @param loading 是否正在加载，否则加载停止
     */
    fun setLoadingMore(loading: Boolean) {
        isLoadingMore = loading
    }

    /**
     * @return 是否正在加载更多
     */
    fun isLoadingMore(): Boolean {
        return isLoadingMore
    }

}
