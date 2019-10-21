package com.april.multiple

import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 支持多样式的 Adapter
 *
 * 默认内置空列表占位视图支持
 */
open class MultipleSupport {

    //数据列
    internal val dataList = mutableListOf<Any>()
    //管理器
    internal val managerArray = SparseArray<Manager<*>>()
    // item 样式代理列表，
    // 以 itemViewType 为 key，以 item 样式代理为 value
    internal val itemDelegateArray = SparseArray<ItemDelegate<*, *>>()

    //空视图占位布局
    internal var placeholderItemDelegate: CrossLineItemDelegate? = null

    //==============================================================================================

    internal open fun getItemCount(): Int {
        return if (dataList.isEmpty() && placeholderItemDelegate != null) {
            1
        } else {
            dataList.size
        }
    }

    internal open fun getItemViewType(position: Int): Int {
        //占位视图
        if (dataList.isEmpty() && placeholderItemDelegate != null) {
            return placeholderItemDelegate!!.viewType
        }
        val itemBean = dataList[position]
        return managerArray.valueAt(
            managerArray.indexOfKey(itemBean.javaClass.hashCode())
        ).getItemViewType(itemBean, position)
    }

    internal open fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        itemDelegateArray.get(holder.itemViewType)?.viewAttachedToWindow(holder)
    }

    internal open fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        itemDelegateArray.get(holder.itemViewType)?.viewDetachedFromWindow(holder)
    }

    internal open fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (placeholderItemDelegate != null
            && viewType == placeholderItemDelegate!!.viewType
        ) {
            placeholderItemDelegate!!.createViewHolder(parent)
        } else {
            itemDelegateArray.get(viewType).createViewHolder(parent)
        }
    }

    internal open fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        itemDelegateArray.get(holder.itemViewType)?.bindViewHolder(
            holder, dataList[position], payloads
        )
    }

    //==============================================================================================

    /**
     * [position] item 位置
     * [Boolean] item 是否跨行或者跨列
     */
    internal open fun itemCrossRowOrColumn(position: Int): Boolean {
        return if (dataList.isEmpty() && placeholderItemDelegate != null) {
            placeholderItemDelegate!!.crossRowWhenGridLayout()
        } else {
            itemDelegateArray.get(getItemViewType(position)).crossRowWhenGridLayout()
        }
    }
}
