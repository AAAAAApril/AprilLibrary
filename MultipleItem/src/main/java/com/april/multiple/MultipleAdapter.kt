package com.april.multiple

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * 多样式 Adapter
 */
open class MultipleAdapter : AbsAdapter() {

    override val support = MultipleSupport()

    /**
     * 从布局资源文件添加
     *
     * @return 创建出的占位布局
     */
    fun setPlaceholder(
        targetRecyclerView: RecyclerView,
        @LayoutRes placeholderViewResId: Int
    ): View {
        val placeholderView = LayoutInflater.from(targetRecyclerView.context).inflate(
            placeholderViewResId, targetRecyclerView, false
        )
        support.placeholderView = placeholderView
        support.placeholderViewType = placeholderView.javaClass.hashCode()
        return placeholderView
    }

    open fun getData(position: Int): Any {
        return support.dataList[position]
    }

    fun getDataList(): MutableList<Any> {
        return support.dataList
    }

    fun resetDataList(dataList: MutableList<Any>) {
        support.dataList.clear()
        support.dataList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun clearDataList() {
        support.dataList.clear()
        notifyDataSetChanged()
    }

    open fun addData(any: Any) {
        support.dataList.add(any)
        notifyItemInserted(support.dataList.lastIndex)
    }

    open fun insertData(any: Any, position: Int) {
        support.dataList.add(position, any)
        notifyItemInserted(position)
    }

    open fun removeData(position: Int) {
        support.dataList.removeAt(position)
        notifyItemRemoved(position)
    }

    open fun removeLastData() {
        val index = support.dataList.lastIndex
        support.dataList.removeAt(index)
        notifyItemRemoved(index)
    }

    open fun addDataList(dataList: MutableList<Any>) {
        if (dataList.isEmpty()) {
            return
        }
        val index = dataList.size
        support.dataList.addAll(dataList)
        notifyItemRangeInserted(
            index,
            dataList.size
        )
    }

}

open class HeaderFooterAdapter : MultipleAdapter() {

    override val support: HeaderFooterSupport = HeaderFooterSupport()

    /**
     * 添加 header
     *
     * @param recyclerView 目标 RecyclerView
     * @return headerView
     */
    fun addHeader(
        recyclerView: RecyclerView,
        @LayoutRes headerLayoutRes: Int
    ): View {
        val headerView = LayoutInflater.from(recyclerView.context)
            .inflate(headerLayoutRes, recyclerView, false)
        support.addHeader(headerView)
        notifyItemInserted(support.headerArray.indexOfValue(headerView))
        return headerView
    }

    /**
     * 移除 HeaderView
     */
    fun removeHeader(headerView: View) {
        val index = support.headerArray.indexOfValue(headerView)
        support.headerArray.removeAt(index)
        notifyItemRemoved(index)
    }

    /**
     * 添加 footer
     *
     * @param recyclerView 目标 RecyclerView
     * @return footerView
     */
    fun addFooter(
        recyclerView: RecyclerView,
        @LayoutRes footerLayoutRes: Int
    ): View {
        val footerView = LayoutInflater.from(recyclerView.context)
            .inflate(footerLayoutRes, recyclerView, false)
        support.addFooter(footerView)
        notifyItemInserted(support.adapterPositionOfFooter(footerView))
        return footerView
    }

    /**
     * 移除 FooterView
     */
    fun removeFooter(footerView: View) {
        val index = support.adapterPositionOfFooter(footerView)
        support.footerArray.removeAt(support.footerArray.indexOfValue(footerView))
        notifyItemRemoved(index)
    }

    fun headerCount(): Int = support.headerCount()

    fun footerCount(): Int = support.footerCount()

    override fun addData(any: Any) {
        support.dataList.add(any)
        notifyItemInserted(
            support.dataList.lastIndex + headerCount()
        )
    }

    override fun insertData(any: Any, position: Int) {
        support.dataList.add(position, any)
        notifyItemInserted(
            position + headerCount()
        )
    }

    override fun removeData(position: Int) {
        support.dataList.removeAt(position)
        notifyItemRemoved(
            position + headerCount()
        )
    }

    override fun removeLastData() {
        val index = support.dataList.lastIndex
        support.dataList.removeAt(index)
        notifyItemRemoved(
            index + headerCount()
        )
    }

    override fun addDataList(dataList: MutableList<Any>) {
        if (dataList.isEmpty()) {
            return
        }
        val index = dataList.size
        support.dataList.addAll(dataList)
        notifyItemRangeInserted(
            index + headerCount(),
            dataList.size
        )
    }

}