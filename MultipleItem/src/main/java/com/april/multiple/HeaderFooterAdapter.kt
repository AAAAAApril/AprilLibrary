package com.april.multiple

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * 支持头尾布局的 Adapter
 *
 * 注意：数据操作相关方法中，
 * dataPosition 是指数据列下标，并不包含头尾布局，
 * adapterPosition 是包含头尾布局的下标，
 * [headerCount] 和 [footerCount] 函数可以分别获取到头尾布局的个数
 */
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
    open fun addFooter(
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
    open fun removeFooter(footerView: View) {
        val index = support.adapterPositionOfFooter(footerView)
        support.footerArray.removeAt(support.footerArray.indexOfValue(footerView))
        notifyItemRemoved(index)
    }

    fun headerCount(): Int = support.headerCount()

    fun footerCount(): Int = support.footerCount()

    override fun clearDataList() {
        val count = support.dataList.size
        support.dataList.clear()
        notifyItemRangeRemoved(headerCount(), count)
    }

    override fun <T : Any> addData(any: T) {
        support.dataList.add(any)
        notifyItemInserted(
            support.dataList.lastIndex + headerCount()
        )
    }

    override fun <T : Any> insertData(any: T, dataPosition: Int) {
        support.dataList.add(dataPosition, any)
        notifyItemInserted(
            dataPosition + headerCount()
        )
    }

    override fun <T : Any> insertDataList(dataPosition: Int, dataList: MutableList<T>) {
        if (dataList.isEmpty()) {
            return
        }
        support.dataList.addAll(dataPosition, dataList)
        notifyItemRangeInserted(
            dataPosition + headerCount(),
            dataList.size
        )
    }

    override fun <T : Any> resetData(any: T, dataPosition: Int) {
        support.dataList[dataPosition] = any
        notifyItemChanged(
            dataPosition + headerCount()
        )
    }

    override fun removeData(dataPosition: Int) {
        support.dataList.removeAt(dataPosition)
        notifyItemRemoved(
            dataPosition + headerCount()
        )
    }

    override fun <T : Any> addDataList(dataList: MutableList<T>) {
        if (support.dataList.isEmpty()) {
            resetDataList(dataList)
            return
        }
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