package com.april.multiple

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * 一个特殊的样式代理（可以在网格或者瀑布流布局中 拉通展示 的类型）
 */
open class CrossLineItemDelegate(@LayoutRes private val itemViewResId: Int) :
    ItemDelegate<Any, RecyclerView.ViewHolder>() {

    internal var viewType: Int = itemViewResId
    private var itemView: View? = null

    internal fun createItemView(parent: ViewGroup): View {
        if (itemView == null) {
            itemView = LayoutInflater.from(parent.context).inflate(
                itemViewResId, parent, false
            )
        }
        return itemView!!
    }

    override fun onCreateItemView(parent: ViewGroup): View {
        return createItemView(parent)
    }

    final override fun crossRowWhenGridLayout(): Boolean {
        return true
    }

    final override fun crossRowWhenStaggeredGridLayout(): Boolean {
        return true
    }

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        itemView: View
    ): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(itemView) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, t: Any) {
    }

}
