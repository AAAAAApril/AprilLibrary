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

    open fun getData(adapterPosition: Int): Any {
        return support.dataList[adapterPosition]
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

    open fun insertData(any: Any, adapterPosition: Int) {
        support.dataList.add(adapterPosition, any)
        notifyItemInserted(adapterPosition)
    }

    open fun insertDataList(adapterPosition: Int, dataList: MutableList<Any>) {
        if (dataList.isEmpty()) {
            return
        }
        support.dataList.addAll(adapterPosition, dataList)
        notifyItemRangeInserted(adapterPosition, dataList.size)
    }

    open fun resetData(any: Any, adapterPosition: Int) {
        support.dataList[adapterPosition] = any
        notifyItemChanged(adapterPosition)
    }

    open fun removeData(adapterPosition: Int) {
        support.dataList.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
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
        val index = support.dataList.size
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

    override fun insertData(any: Any, adapterPosition: Int) {
        support.dataList.add(adapterPosition, any)
        notifyItemInserted(
            adapterPosition + headerCount()
        )
    }

    override fun insertDataList(adapterPosition: Int, dataList: MutableList<Any>) {
        if (dataList.isEmpty()) {
            return
        }
        support.dataList.addAll(adapterPosition, dataList)
        notifyItemRangeInserted(
            adapterPosition + support.headerCount(),
            dataList.size
        )
    }

    override fun resetData(any: Any, adapterPosition: Int) {
        support.dataList[adapterPosition - headerCount()] = any
        notifyItemChanged(
            adapterPosition
        )
    }

    override fun removeData(adapterPosition: Int) {
        support.dataList.removeAt(adapterPosition)
        notifyItemRemoved(
            adapterPosition + headerCount()
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
        val index = support.dataList.size
        support.dataList.addAll(dataList)
        notifyItemRangeInserted(
            index + headerCount(),
            dataList.size
        )
    }

}