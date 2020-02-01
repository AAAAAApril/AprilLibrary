package com.april.multiple.decoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import com.april.multiple.HeaderFooterSupport

/**
 * 使用 MultipleAdapter 时，最好是使用这个  Decoration
 *
 * 它可以避免因为 header 和 footer 以及 placeholder 的特殊性而导致边距设置异常
 */
class MultipleGridSpanDecoration(
    context: Context,
    private val support: HeaderFooterSupport
) : GridSpanDecoration(context) {

    private var mSpecialItemHorizontalOffset: Int? = null
    private var mSpecialItemVerticalOffset: Int? = null
    private var mSpecialItemNeedOffset = false

    /**
     * 设置特殊 item 横向上的偏移量 （单位：DP）
     *
     * 如果没有设置偏移量，但是又需要做偏移，则使用默认的值
     */
    fun setSpecialItemHorizontalOffsetDP(specialItemHorizontalOffsetDP: Int) {
        mSpecialItemHorizontalOffset = dp2px(specialItemHorizontalOffsetDP)
    }

    /**
     * 设置特殊 item 纵向上的偏移量 （单位：DP）
     *
     * 如果没有设置偏移量，但是又需要做偏移，则使用默认的值
     */
    fun setSpecialItemVerticalOffsetDP(specialItemVerticalOffsetDP: Int) {
        mSpecialItemVerticalOffset = dp2px(specialItemVerticalOffsetDP)
    }

    /**
     * 设置特殊 item 布局是否应用偏移量
     */
    fun setSpecialItemNeedOffset(specialItemNeedOffset: Boolean) {
        mSpecialItemNeedOffset = specialItemNeedOffset
    }

    override fun onItemOffsets(
        mOrientation: Int,
        mOutRect: Rect,
        mItemView: View,
        mPosition: Int,
        mSpanCount: Int,
        mHorizontalOffset: Int,
        mVerticalOffset: Int,
        mIncludeEdge: Boolean
    ) {

        var spanCount = mSpanCount
        var horizontalOffset = mHorizontalOffset
        var verticalOffset = mVerticalOffset
        var position = mPosition

        //头尾、占位布局单独设置
        if (support.isHeaderPosition(mPosition)
            || support.isFooterPosition(mPosition)
            || mItemView == support.placeholderItemDelegate?.itemView
        ) {
            position = 0
            // 头部、尾部，以及占位布局，都返回 1，这样在给它们做偏移时，就正常了
            spanCount = 1
            //这些不需要做偏移
            if (!mSpecialItemNeedOffset) {
                horizontalOffset = 0
                verticalOffset = 0
            }
            //需要做偏移
            else {
                /*
                    如果没有设置偏移量，但是又需要做偏移，则使用默认的值
                 */
                horizontalOffset = mSpecialItemHorizontalOffset ?: mHorizontalOffset
                verticalOffset = mSpecialItemVerticalOffset ?: mVerticalOffset
            }
        } else {
            position = (position - support.headerCount())
        }

        super.onItemOffsets(
            mOrientation,
            mOutRect,
            mItemView,
            position,
            spanCount,
            horizontalOffset,
            verticalOffset,
            mIncludeEdge
        )
    }

}
