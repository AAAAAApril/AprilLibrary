package com.april.multiple

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * 特殊(占位布局、头尾布局等) item 样式代理
 */
abstract class SpecialItemDelegate<T : Any>(
    @LayoutRes private val specialItemLayoutRes: Int
) : MultipleItemDelegate<T, RecyclerView.ViewHolder>() {

    internal var itemView: View? = null

    final override fun isCrossRowWhenGridLayout(): Boolean {
        return true
    }

    final override fun isCrossRowWhenStaggeredGridLayout(): Boolean {
        return true
    }

    override fun onCreateItemView(parent: ViewGroup): View =
        LayoutInflater.from(parent.context).inflate(
            specialItemLayoutRes, parent, false
        )

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        itemView: View
    ): RecyclerView.ViewHolder {
        this.itemView = itemView
        return object : RecyclerView.ViewHolder(itemView) {}
    }

}