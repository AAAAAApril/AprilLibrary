package com.april.multiple.loadmore

import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView 监听器
 */
internal class RecyclerViewListener(private val block: (RecyclerView) -> Unit) :
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
            block.invoke(recyclerView)
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        isSwipeUp = dy > 0
    }

}