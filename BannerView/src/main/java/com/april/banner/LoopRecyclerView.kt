package com.april.banner

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 * 处理循环滚动的 RecyclerView
 */
class LoopRecyclerView(
    context: Context,
    attrs: AttributeSet?
) : RecyclerView(context, attrs)