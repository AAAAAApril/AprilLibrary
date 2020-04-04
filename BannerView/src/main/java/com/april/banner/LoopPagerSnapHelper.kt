package com.april.banner

import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * 循环 PaperSnapHelper
 */
class LoopPagerSnapHelper : PagerSnapHelper() {
    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager,
        velocityX: Int,
        velocityY: Int
    ): Int {
        if (layoutManager.itemCount < 1) {
            return 0
        }
        val position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
        return if (position >= layoutManager.itemCount) {
            0
        } else {
            position
        }
    }
}