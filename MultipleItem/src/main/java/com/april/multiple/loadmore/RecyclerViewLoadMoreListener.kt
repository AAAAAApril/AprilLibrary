package com.april.multiple.loadmore

import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView 滚动监听器
 */
class RecyclerViewLoadMoreListener(private val loadMoreAction: (RecyclerView) -> Unit) :
    RecyclerView.OnScrollListener() {

    private var isSwipeUp = false

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        //滚动已经停止了
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            //不是向上拉操作，不处理
            if (!isSwipeUp) {
                return
            }
            /*
                recyclerView.canScrollVertically(1) ，true 表示还可以向上滚动，还没到 底部，false 表示 已经到底部了
                recyclerView.canScrollVertically(-1) ，true 表示还可以向下滚动，还没到 顶部，false 表示 已经到顶部了
            */
            //还能再向上滚动，表示还没到底部
            if (recyclerView.canScrollVertically(1)) {
                return
            }
            loadMoreAction.invoke(recyclerView)
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        isSwipeUp = dy > 0
    }

}