package com.april.multiple

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
     * @param headerItemDelegate 添加 header
     */
    fun <T : SpecialItemDelegate<*>> addHeader(headerItemDelegate: T) {
        support.addHeader(headerItemDelegate)
        notifyItemInserted(support.adapterPositionOfHeader(headerItemDelegate))
    }

    /**
     * 移除 header
     */
    fun <T : SpecialItemDelegate<*>> removeHeader(headerItemDelegate: T) {
        val adapterPosition = support.adapterPositionOfHeader(headerItemDelegate)
        if (adapterPosition < 0) {
            return
        }
        if (support.removeHeader(headerItemDelegate)) {
            notifyItemRemoved(adapterPosition)
        }
    }

    /**
     * @param footerItemDelegate 添加 footer
     */
    fun <T : SpecialItemDelegate<*>> addFooter(footerItemDelegate: T) {
        support.addFooter(footerItemDelegate)
        notifyItemInserted(support.adapterPositionOfFooter(footerItemDelegate))
    }

    /**
     * 移除 footer
     */
    fun <T : SpecialItemDelegate<*>> removeFooter(footerItemDelegate: T) {
        val index = support.footerArray.indexOfValue(footerItemDelegate)
        if (index < 0) {
            return
        }
        val adapterPosition = support.adapterPositionOfFooter(footerItemDelegate)
        if (support.removeFooter(footerItemDelegate)) {
            notifyItemRemoved(adapterPosition)
        }
    }

    /**
     * 设置 header 需要的数据
     */
    fun <T : SpecialItemDelegate<*>> resetHeaderData(
        headerItemDelegate: T,
        headerData: Any?
    ) {
        if (support.resetHeaderData(headerItemDelegate, headerData)) {
            notifyItemChanged(support.adapterPositionOfHeader(headerItemDelegate))
        }
    }

    /**
     * 设置 footer 需要的数据
     */
    fun <T : SpecialItemDelegate<*>> resetFooterData(
        footerItemDelegate: T,
        footerData: Any?
    ) {
        if (support.resetFooterData(footerItemDelegate, footerData)) {
            notifyItemChanged(support.adapterPositionOfFooter(footerItemDelegate))
        }
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