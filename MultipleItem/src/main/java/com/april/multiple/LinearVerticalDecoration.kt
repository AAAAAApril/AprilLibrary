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
class LinearVerticalDecoration(
    context: Context,
    //默认纵向偏移量
    defaultVerticalOffsetDP: Float = 0.0f,
    /**
     * 设置横向上的偏移量（单位：DP）
     *
     * 注意：由于这个类只使用于单行的情况，因此，这个值表示左右边缘的偏移距离
     */
    horizontalOffsetDP: Float = 0.0f,
    /**
     * 最后一个item后面的偏移量
     *
     * 注：这个值可以避免当 RecyclerView 高度自适应时，最后一个 item 设置的阴影高度被阻挡的问题
     */
    verticalBottomOffsetDP: Float = 0.0f
) : RecyclerView.ItemDecoration() {

    //换算种子
    private val seed by lazy {
        context.resources.displayMetrics.density + 0.5f
    }

    //默认纵向偏移量
    private val defaultVerticalOffset by lazy {
        dp2px(defaultVerticalOffsetDP)
    }
    //横向偏移量
    private val horizontalOffset by lazy {
        dp2px(horizontalOffsetDP)
    }
    //最后一个item后面的偏移量
    private val verticalBottomOffset by lazy {
        dp2px(verticalBottomOffsetDP)
    }

    private val verticalOffsetArray = SparseIntArray()

    /**
     * @param itemPosition 需要偏移的位置
     * @param offsetDP 该位置上的偏移量（单位：DP）
     */
    fun addVerticalOffset(offsetDP: Float, vararg itemPosition: Int) {
        val offset = dp2px(offsetDP)
        itemPosition.forEach {
            verticalOffsetArray.put(it, offset)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view)
        //item左右的偏移距离
        outRect.left = horizontalOffset
        outRect.right = horizontalOffset
        //item顶上的偏移距离
        outRect.top = verticalOffsetArray.get(
            itemPosition,
            //找不到就用默认的
            defaultVerticalOffset
        )
        //已经是最后一个 item 了
        if ((itemPosition + 1 == parent.adapter?.itemCount)) {
            outRect.bottom = verticalBottomOffset
        }
    }

    private fun dp2px(dp: Float): Int {
        return (dp * seed).toInt()
    }
}