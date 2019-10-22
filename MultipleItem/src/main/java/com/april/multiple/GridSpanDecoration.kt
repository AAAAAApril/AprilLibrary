package com.april.multiple

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 在 网格 展示下，给 item 之间设置间隔
 *
 * 注意：仅适用于 线性布局 或者 网格布局，如果网格布局里面存在某些 Item 拉通展示的情况，则不可用。
 *
 * 新支持：
 * 单独设置横向或者纵向的边距，
 *
 * ——  by  April丶
 */
open class GridSpanDecoration(
    private val context: Context
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
    private var mHorizontalSpacing = 0
    //纵向间隔（PX）
    private var mVerticalSpacing = 0
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
    fun setHorizontalSpacing(horizontalSpacingDP: Int) {
        mHorizontalSpacing = dp2px(horizontalSpacingDP)
    }

    /**
     * 设置纵向偏移量
     */
    fun setVerticalSpacing(verticalSpacingDP: Int) {
        mVerticalSpacing = dp2px(verticalSpacingDP)
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
        // item position
        val position = parent.getChildAdapterPosition(view)
        val spanCount = getSpanCount(view, position) ?: return
        // item column
        val column = position % spanCount
        // orientation
        when (mOrientation) {
            RecyclerView.VERTICAL -> {
                verticalOrientation(
                    outRect,
                    position,
                    column,
                    spanCount,
                    getHorizontalSpacing(view, position),
                    getVerticalSpacing(view, position),
                    getIncludeEdge(view, position)
                )
            }
            RecyclerView.HORIZONTAL -> {
                horizontalOrientation(
                    outRect,
                    position,
                    column,
                    spanCount,
                    getHorizontalSpacing(view, position),
                    getVerticalSpacing(view, position),
                    getIncludeEdge(view, position)
                )
            }
        }
    }

    /**
     * 返回 SpanCount，如果为 null，则不处理偏移
     */
    protected open fun getSpanCount(itemView: View, position: Int): Int? {
        return mSpanCount
    }

    protected open fun getHorizontalSpacing(itemView: View, position: Int): Int {
        return mHorizontalSpacing
    }

    protected open fun getVerticalSpacing(itemView: View, position: Int): Int {
        return mVerticalSpacing
    }

    protected open fun getIncludeEdge(itemView: View, position: Int): Boolean {
        return mIncludeEdge
    }

    /**
     * 纵向
     */
    private fun verticalOrientation(
        outRect: Rect,
        position: Int,
        column: Int,
        mSpanCount: Int,
        mHorizontalSpacing: Int,
        mVerticalSpacing: Int,
        mIncludeEdge: Boolean
    ) {
        if (mIncludeEdge) {
            // spacing - column * ((1f / spanCount) * spacing)
            outRect.left = mHorizontalSpacing - column * mHorizontalSpacing / mSpanCount
            // (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * mHorizontalSpacing / mSpanCount

            if (position < mSpanCount) { // top edge
                outRect.top = mVerticalSpacing
            }
            outRect.bottom = mVerticalSpacing // item bottom
        } else {
            // column * ((1f / spanCount) * spacing)
            outRect.left = column * mHorizontalSpacing / mSpanCount
            // spacing - (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = mHorizontalSpacing - (column + 1) * mHorizontalSpacing / mSpanCount
            if (position >= mSpanCount) {
                outRect.top = mVerticalSpacing // item top
            }
        }
    }

    /**
     * 横向
     */
    private fun horizontalOrientation(
        outRect: Rect,
        position: Int,
        column: Int,
        mSpanCount: Int,
        mHorizontalSpacing: Int,
        mVerticalSpacing: Int,
        mIncludeEdge: Boolean
    ) {
        if (mIncludeEdge) {
            outRect.top = mVerticalSpacing - column * mVerticalSpacing / mSpanCount
            outRect.bottom = (column + 1) * mVerticalSpacing / mSpanCount
            if (position < mSpanCount) {
                outRect.left = mHorizontalSpacing
            }
            outRect.right = mHorizontalSpacing
        } else {
            outRect.top = column * mVerticalSpacing / mSpanCount
            outRect.bottom = mVerticalSpacing - (column + 1) * mVerticalSpacing / mSpanCount
            if (position >= mSpanCount) {
                outRect.left = mHorizontalSpacing
            }
        }
    }

    protected fun dp2px(dp: Int): Int {
        return (dp * seed).toInt()
    }

}