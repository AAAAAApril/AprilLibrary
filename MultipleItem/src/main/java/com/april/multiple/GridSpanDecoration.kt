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
class GridSpanDecoration(

    context: Context,
    //横向上的间隔（单位 dp）
    mHorizontalSpacingDP: Int = 0,
    //纵向上的间隔（单位 dp）
    mVerticalSpacingDP: Int = 0,
    //网格每行（竖向展示）或者每列（横向展示）展示的个数
    private val mSpanCount: Int = 1,
    //布局方向
    @RecyclerView.Orientation
    private val mOrientation: Int = RecyclerView.VERTICAL,
    //是否包含边缘（如果为 false，则 item 与 RecyclerView 布局接触的地方不会出现边距，否则会出现）
    private val mIncludeEdge: Boolean = true

) : RecyclerView.ItemDecoration() {

    //横向间隔（PX）
    private val mHorizontalSpacing by lazy {
        context.dp2px(mHorizontalSpacingDP)
    }
    //纵向间隔（PX）
    private val mVerticalSpacing by lazy {
        context.dp2px(mVerticalSpacingDP)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // item position
        val position = parent.getChildAdapterPosition(view)
        // item column
        val column = position % mSpanCount
        // orientation
        when (mOrientation) {
            RecyclerView.VERTICAL -> {
                verticalOrientation(outRect, position, column)
            }
            RecyclerView.HORIZONTAL -> {
                horizontalOrientation(outRect, position, column)
            }
        }
    }

    /**
     * 纵向
     */
    private fun verticalOrientation(
        outRect: Rect,
        position: Int,
        column: Int
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
        column: Int
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

}

private fun Context.dp2px(dp: Int): Int {
    return (dp * resources.displayMetrics.density + 0.5f).toInt()
}