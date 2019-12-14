package com.april.multiple

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * 特殊(占位布局、头尾布局等) item 样式代理
 */
abstract class SpecialItemDelegate<T : Any?>(
    @LayoutRes private val specialItemLayoutRes: Int
) : MultipleItemDelegate<Any, RecyclerView.ViewHolder>() {

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
        return object : RecyclerView.ViewHolder(itemView) {}
    }

    final override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        t: Any,
        payloads: List<Any>
    ) {
        super.onBindViewHolder(holder, t, payloads)
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, bean: Any) {

    }

    internal fun bindSpecialViewHolder(holder: RecyclerView.ViewHolder, bean: Any?) {
        onBindSpecialViewHolder(holder, bean as? T)
    }

    protected abstract fun onBindSpecialViewHolder(holder: RecyclerView.ViewHolder, bean: T?)

}