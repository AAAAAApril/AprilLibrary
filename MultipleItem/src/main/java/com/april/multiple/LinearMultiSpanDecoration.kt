package com.april.multiple

import android.content.Context
import android.graphics.Rect
import android.util.SparseIntArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 只适用于
 *
 * 有限个
 *
 * 纵向
 *
 * 线性布局
 *
 * 的 多类型 偏移 装饰器
 *
 *
 *
 * 0    偏移单位 2
 * 0    item
 * 1    偏移单位 1
 * 1    item
 * 2    item
 * 3    item
 * 4    偏移单位 1
 * 4    item
 * 5    偏移单位 2
 * 5    item
 *
 */
class LinearMultiSpanDecoration(context: Context) : RecyclerView.ItemDecoration() {

    //换算种子
    private val seed by lazy {
        context.resources.displayMetrics.density + 0.5f
    }

    private val offsetArray = SparseIntArray()
    private var horizontalSpacing = 0

    /**
     * @param itemPosition 需要偏移的位置
     * @param offsetDP 该位置上的偏移量（单位：DP）
     */
    fun addOffsetIndex(itemPosition: Int, offsetDP: Int) {
        offsetArray.put(itemPosition, dp2px(offsetDP))
    }

    /**
     * 设置横向上的偏移量（单位：DP）
     *
     * 注意：由于这个类只使用于单行的情况，因此，这个值表示边缘的偏移距离
     */
    fun setHorizontalSpacingDP(horizontalSpacingDP: Int) {
        horizontalSpacing = dp2px(horizontalSpacingDP)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = horizontalSpacing
        outRect.right = horizontalSpacing
        outRect.top = offsetArray.get(parent.getChildAdapterPosition(view))
    }

    private fun dp2px(dp: Int): Int {
        return (dp * seed).toInt()
    }
}