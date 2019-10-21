/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.april.multiple

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 在 网格 展示下，给 item 之间设置间隔
 *
 * 注：仅适用于 > 纵向 < 排列的线性布局或者网格布局，对于存在在网格布局下通行展示的 item，是否可用暂时未知。
 *
 * 这代码不是自己写的，而是从网上抄来的……
 * （支持 单独设置横向或者纵向的边距，但是暂不支持对应方向上的包含边缘设置 ——  by  April丶）
 */
class GridSpanDecoration(
    private val context: Context,
    //网格每行（竖向展示）或者每列（横向展示）展示的个数
    private val mSpanCount: Int,
    //横向间隔（单位 dp）
    private val horizontalSpacingDP: Int,
    //纵向间隔（单位 dp）
    private val verticalSpacingDP: Int,
    //是否包含边缘（如果为 false，则 item 与 RecyclerView 布局接触的地方不会出现边距，否则会出现）
    private val mIncludeEdge: Boolean = false
) : RecyclerView.ItemDecoration() {

    //横向间隔（单位 px）
    private val mHorizontalSpacing by lazy {
        (horizontalSpacingDP * context.resources.displayMetrics.density + 0.5f).toInt()
    }
    //纵向间隔（单位 px）
    private val mVerticalSpacing by lazy {
        (verticalSpacingDP * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % mSpanCount // item column

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
}
