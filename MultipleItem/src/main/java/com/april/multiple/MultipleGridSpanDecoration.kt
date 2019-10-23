package com.april.multiple

import android.content.Context
import android.graphics.Rect
import android.view.View

/**
 * 使用 MultipleAdapter 时，最好是使用这个  Decoration
 *
 * 它可以避免因为 header 和 footer 以及 placeholder 的特殊性而导致边距设置异常
 */
class MultipleGridSpanDecoration(
    context: Context,
    private val support: HeaderFooterSupport
) : GridSpanDecoration(context) {

    private var mSpecialItemHorizontalSpacing: Int? = null
    private var mSpecialItemVerticalSpacing: Int? = null
    private var mSpecialItemNeedOffsets = false

    /**
     * 设置特殊 item 横向上的偏移量 （单位：DP）
     *
     * 如果没有设置偏移量，但是又需要做偏移，则使用默认的值
     */
    fun setSpecialItemHorizontalSpacing(specialItemHorizontalSpacingDP: Int) {
        mSpecialItemHorizontalSpacing = dp2px(specialItemHorizontalSpacingDP)
    }

    /**
     * 设置特殊 item 纵向上的偏移量 （单位：DP）
     *
     * 如果没有设置偏移量，但是又需要做偏移，则使用默认的值
     */
    fun setSpecialItemVerticalSpacing(specialItemVerticalSpacingDP: Int) {
        mSpecialItemVerticalSpacing = dp2px(specialItemVerticalSpacingDP)
    }

    /**
     * 设置特殊 item 布局是否应用偏移量
     */
    fun setSpecialItemNeedOffsets(specialItemNeedOffsets: Boolean) {
        mSpecialItemNeedOffsets = specialItemNeedOffsets
    }

    override fun onItemOffsets(
        mOrientation: Int,
        mOutRect: Rect,
        mItemView: View,
        mPosition: Int,
        mSpanCount: Int,
        mHorizontalSpacing: Int,
        mVerticalSpacing: Int,
        mIncludeEdge: Boolean
    ) {

        var spanCount = mSpanCount
        var horizontalSpacing = mHorizontalSpacing
        var verticalSpacing = mVerticalSpacing

        //头尾、占位布局单独设置
        if (support.isHeaderPosition(mPosition)
            || support.isFooterPosition(mPosition)
            || mItemView == support.placeholderView
        ) {
            // 头部、尾部，以及占位布局，都返回 1，这样在给它们做偏移时，就正常了
            spanCount = 1
            //这些不需要做偏移
            if (!mSpecialItemNeedOffsets) {
                horizontalSpacing = 0
                verticalSpacing = 0
            }
            //需要做偏移
            else {
                /*
                    如果没有设置偏移量，但是又需要做偏移，则使用默认的值
                 */
                horizontalSpacing = mSpecialItemHorizontalSpacing ?: mHorizontalSpacing
                verticalSpacing = mSpecialItemVerticalSpacing ?: mVerticalSpacing
            }
        }

        super.onItemOffsets(
            mOrientation,
            mOutRect,
            mItemView,
            mPosition,
            spanCount,
            horizontalSpacing,
            verticalSpacing,
            mIncludeEdge
        )
    }

}
