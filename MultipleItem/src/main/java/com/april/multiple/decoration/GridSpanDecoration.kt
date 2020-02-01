package com.april.multiple.decoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 在 网格 展示下，给 item 之间设置间隔
 *
 * 注意：仅适用于 线性布局 或者 标准的网格布局，如果网格布局里面存在某些 Item 拉通展示的情况，则不可用。
 *
 * 新支持：
 * 单独设置横向或者纵向的边距，
 *
 * ——  by  April丶
 */
open class GridSpanDecoration(
    context: Context
) : RecyclerView.ItemDecoration() {

    //换算种子
    private val seed by lazy {
        context.resources.displayMetrics.density + 0.5f
    }

    //网格每行（竖向展示）或者每列（横向展示）展示的个数
    private var mSpanCount: Int = 1
    //布局方向
    @RecyclerView.Orientation
    private var mOrientation: Int = RecyclerView.VERTICAL
    //横向间隔（PX）
    private var mHorizontalOffset = 0
    //纵向间隔（PX）
    private var mVerticalOffset = 0
    //是否包含边缘（如果为 false，则 item 与 RecyclerView 布局接触的地方不会出现边距，否则会出现）
    private var mIncludeEdge: Boolean = true

    /**
     * 设置每一行或者列展示的个数
     */
    fun setSpanCount(spanCount: Int) {
        mSpanCount = spanCount
    }

    /**
     * 设置布局方向
     */
    fun setOrientation(@RecyclerView.Orientation orientation: Int) {
        mOrientation = orientation
    }

    /**
     * 设置横向偏移量
     */
    fun setHorizontalOffsetDP(horizontalOffsetDP: Int) {
        mHorizontalOffset = dp2px(horizontalOffsetDP)
    }

    /**
     * 设置纵向偏移量
     */
    fun setVerticalOffsetDP(verticalOffsetDP: Int) {
        mVerticalOffset = dp2px(verticalOffsetDP)
    }

    /**
     * 设置是否包含边缘
     */
    fun setIncludeEdge(includeEdge: Boolean) {
        mIncludeEdge = includeEdge
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        onItemOffsets(
            mOrientation,
            outRect,
            view,
            parent.getChildAdapterPosition(view),
            mSpanCount,
            mHorizontalOffset,
            mVerticalOffset,
            mIncludeEdge
        )
    }

    protected open fun onItemOffsets(
        //布局方向
        @RecyclerView.Orientation
        mOrientation: Int,
        mOutRect: Rect,
        mItemView: View,
        //item position
        mPosition: Int,
        //每行或者每列的个数
        mSpanCount: Int,
        //横向边距
        mHorizontalOffset: Int,
        //纵向边距
        mVerticalOffset: Int,
        //是否在 RecyclerView 边界位置应用偏移量
        mIncludeEdge: Boolean
    ) {
        createItemOffsets(
            mOrientation,
            mOutRect,
            mPosition,
            mSpanCount,
            mHorizontalOffset,
            mVerticalOffset,
            mIncludeEdge
        )
    }

    /**
     * 绘制偏移量
     */
    private fun createItemOffsets(
        @RecyclerView.Orientation
        orientation: Int,
        outRect: Rect,
        position: Int,
        spanCount: Int,
        horizontalOffset: Int,
        verticalOffset: Int,
        includeEdge: Boolean
    ) {
        val column = position % spanCount
        when (orientation) {
            //纵向
            RecyclerView.VERTICAL -> {
                if (includeEdge) {
                    // spacing - column * ((1f / spanCount) * spacing)
                    outRect.left = horizontalOffset - column * horizontalOffset / spanCount
                    // (column + 1) * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * horizontalOffset / spanCount
                    // top edge
                    if (position < spanCount) {
                        outRect.top = verticalOffset
                    }
                    outRect.bottom = verticalOffset // item bottom
                } else {
                    // column * ((1f / spanCount) * spacing)
                    outRect.left = column * horizontalOffset / spanCount
                    // spacing - (column + 1) * ((1f / spanCount) * spacing)
                    outRect.right = horizontalOffset - (column + 1) * horizontalOffset / spanCount
                    if (position >= spanCount) {
                        outRect.top = verticalOffset // item top
                    }
                }
            }
            //横向
            RecyclerView.HORIZONTAL -> {
                if (includeEdge) {
                    outRect.top = verticalOffset - column * verticalOffset / spanCount
                    outRect.bottom = (column + 1) * verticalOffset / spanCount
                    if (position < spanCount) {
                        outRect.left = horizontalOffset
                    }
                    outRect.right = horizontalOffset
                } else {
                    outRect.top = column * verticalOffset / spanCount
                    outRect.bottom = verticalOffset - (column + 1) * verticalOffset / spanCount
                    if (position >= spanCount) {
                        outRect.left = horizontalOffset
                    }
                }
            }
        }

    }

    protected fun dp2px(dp: Int): Int {
        return (dp * seed).toInt()
    }

}