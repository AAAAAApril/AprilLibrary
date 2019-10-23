package com.april.multiple

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 支持头尾布局
 */
open class HeaderFooterSupport : MultipleSupport() {

    private var viewType = this.hashCode()

    //头布局
    internal val headerArray by lazy { SparseArray<View>() }
    //尾布局
    internal val footerArray by lazy { SparseArray<View>() }

    //==============================================================================================

    /**
     * @param headerView 添加 header
     */
    fun addHeader(headerView: View) {
        ++viewType
        headerArray.put(viewType, headerView)
    }

    /**
     * @param footerView 添加 footer
     */
    fun addFooter(footerView: View) {
        ++viewType
        footerArray.put(viewType, footerView)
    }

    //==============================================================================================

    fun headerCount(): Int {
        return headerArray.size()
    }

    fun footerCount(): Int {
        return footerArray.size()
    }

    /**
     * 这个 FooterView 在 adapter 中的位置
     *
     * @param footerView
     */
    fun adapterPositionOfFooter(footerView: View): Int {
        return headerCount() + dataList.size + footerArray.indexOfValue(footerView)
    }

    /**
     * 这个位置是否是头部
     */
    fun isHeaderPosition(position: Int): Boolean {
        if (headerArray.size() == 0) {
            return false
        }
        return position < headerCount()
    }

    /**
     * 这个位置是否是尾部
     */
    fun isFooterPosition(position: Int): Boolean {
        if (footerArray.size() == 0) {
            return false
        }
        return (position - headerCount()) >= super.getItemCount()
    }

    //==============================================================================================

    override fun getItemCount(): Int {
        //头部数量
        return (headerCount()
                //正常数据列
                + super.getItemCount()
                //尾部数量
                + footerCount())
    }

    override fun getItemViewType(position: Int): Int {
        //头部
        if (isHeaderPosition(position)) {
            return headerArray.keyAt(position)
        }
        //中间数据
        val adjPosition = position - headerCount()
        val adapterCount = super.getItemCount()
        return if (adjPosition < adapterCount) {
            super.getItemViewType(adjPosition)
        }
        //尾部
        else {
            footerArray.keyAt(adjPosition - adapterCount)
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        val viewType = holder.itemViewType
        if (headerArray.get(viewType) == null && footerArray.get(viewType) == null) {
            super.onViewAttachedToWindow(holder)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        val viewType = holder.itemViewType
        if (headerArray.get(viewType) == null && footerArray.get(viewType) == null) {
            super.onViewDetachedFromWindow(holder)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //头部
        val headerView = headerArray.get(viewType)
        if (headerView != null) {
            return object : RecyclerView.ViewHolder(headerView) {}
        }
        //尾部
        val footerView = footerArray.get(viewType)
        return if (footerView != null) {
            object : RecyclerView.ViewHolder(footerView) {}
        }
        // 正常数据列
        else {
            super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (isHeaderPosition(position)) {
            return
        }
        val adjPosition = position - headerCount()
        if (adjPosition < super.getItemCount()) {
            super.onBindViewHolder(holder, adjPosition, payloads)
        }
    }

    /**
     * 限定头尾布局为拉通展示
     */
    override fun itemCrossRowOrColumn(position: Int): Boolean {
        //是头部
        if (isHeaderPosition(position)) {
            return true
        }
        //是尾部
        return if (isFooterPosition(position)) {
            true
        }
        //中间数据
        else {
            /*
                    注意：这里传递给父类的是 position，
                    而不像 getItemViewType() 函数里面那样返回 adjPosition
            */
            super.itemCrossRowOrColumn(position)
        }
    }

}