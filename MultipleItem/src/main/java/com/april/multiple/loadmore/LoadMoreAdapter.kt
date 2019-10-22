package com.april.multiple.loadmore

import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.util.containsValue
import androidx.recyclerview.widget.RecyclerView
import com.april.multiple.MultipleAdapter
import com.april.multiple.removeFooter

/**
 * 加载更多 Adapter
 */
class LoadMoreAdapter : MultipleAdapter() {

    //是否可以加载更多
    private var canLoadMore = false
    //是否正在加载更多
    private var isLoadingMore = false
    //加载更多 布局包装
    private var wrapper: LoadMoreWrapper? = null
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
    fun setLoadMoreLayoutRes(
        targetRecyclerView: RecyclerView,
        @LayoutRes loadMoreLayoutRes: Int
    ): View {
        return setLoadMoreView(LoadMoreWrapper(targetRecyclerView, loadMoreLayoutRes))
    }

    /**
     * @return LoadMore View
     */
    fun setLoadMoreView(wrapper: LoadMoreWrapper): View {
        wrapper.createLoadMoreView()
        this.wrapper = wrapper
        return wrapper.loadMoreView
    }

    /**
     * 设置加载更多监听
     */
    fun setLoadMoreListener(loadMoreListener: LoadMoreListener?) {
        this.loadMoreListener = loadMoreListener
    }

    /**
     * 显示或者隐藏 加载更多 View
     */
    fun showLoadMoreView(show: Boolean) {
        wrapper?.let {
            //是否有这个 FooterView
            val contains = support.footerArray.containsValue(it.loadMoreView)
            //显示 且 不包含
            if (show && !contains) {
                support.addFooter(it.loadMoreView)
                notifyItemInserted(support.adapterPositionOfFooter(it.loadMoreView))
                it.onViewAttachedToWindow()
            }
            //不显示 且 包含
            if (!show && contains) {
                it.onViewDetachedFromWindow()
                removeFooter(it.loadMoreView)
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
