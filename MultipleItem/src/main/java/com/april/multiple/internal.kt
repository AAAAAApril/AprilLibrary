package com.april.multiple

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 一个默认的 ViewHolder
 */
class DefaultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

/**
 * 一个特殊的样式代理
 */
open class CrossItemDelegate : ItemDelegate<Any, DefaultViewHolder> {

    internal var viewType: Int = 0
    private var itemView: View? = null
    private var itemViewResId: Int = 0

    /**
     * [itemViewResId] 通过布局 id 构建
     */
    constructor(@LayoutRes itemViewResId: Int) {
        this.itemViewResId = itemViewResId
        viewType = itemViewResId
    }

    /**
     * 通过视图构建
     */
    constructor(itemView: View) {
        this.itemView = itemView
        viewType = itemView.hashCode()
    }

    override fun onCreateItemView(parent: ViewGroup): View {
        if (itemView != null) {
            return itemView!!
        }
        return LayoutInflater.from(parent.context).inflate(
            itemViewResId, parent, false
        )
    }

    final override fun crossRowWhenGridLayout(): Boolean {
        return true
    }

    final override fun crossRowWhenStaggeredGridLayout(): Boolean {
        return true
    }

    final override fun onCreateViewHolder(parent: ViewGroup, itemView: View): DefaultViewHolder {
        return DefaultViewHolder(itemView)
    }

    final override fun onBindViewHolder(holder: DefaultViewHolder, t: Any) {
        onBindViewHolder(holder)
    }

    protected fun onBindViewHolder(holder: DefaultViewHolder) {

    }
}

/**
 * 在
 * [GridLayoutManager.setSpanSizeLookup(GridLayoutManager.SpanSizeLookup)]
 * 函数调用，为某些位置上的 item 实现跨行或者跨列展示
 */
internal class CrossSpanSizeLookUp(private val support: MultipleSupport,
                                   private val spanCount: Int) : GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return if (support.itemCrossRowOrColumn(position)) {
            spanCount
        } else {
            1
        }
    }
}

