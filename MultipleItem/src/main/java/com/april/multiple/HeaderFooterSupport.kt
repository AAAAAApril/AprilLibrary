package com.april.multiple

import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * 支持头尾布局
 */
open class HeaderFooterSupport : MultipleSupport() {

    //头布局
    internal val headerArray by lazy { SparseArray<SpecialItemDelegate<*>>() }

    //尾布局
    internal val footerArray by lazy { SparseArray<SpecialItemDelegate<*>>() }

    //以 header 的 key 为 key，缓存对应 header 的数据实体
    private val headerDataArray by lazy { SparseArray<Any?>() }

    //以 footer 的 key 为 key，缓存对应 footer 的数据实体
    private val footerDataArray by lazy { SparseArray<Any?>() }

    //缓存 header 的 key，以保证 header 的显示顺序和添加顺序一致
    private val headerKeyArray by lazy { mutableListOf<Int>() }

    //缓存 footer 的 key，理由同上
    private val footerKeyArray by lazy { mutableListOf<Int>() }

    //==============================================================================================

    /**
     * @param headerItemDelegate 添加 header
     */
    fun <T, D : SpecialItemDelegate<T>> addHeader(headerItemDelegate: D, defaultData: T? = null) {
        val key = headerItemDelegate.hashCode()
        headerArray.put(key, headerItemDelegate)
        headerKeyArray.add(key)
        headerDataArray.put(key, defaultData)
    }

    /**
     * 移除 header
     */
    fun <D : SpecialItemDelegate<*>> removeHeader(headerItemDelegate: D): Boolean {
        val index = headerArray.indexOfValue(headerItemDelegate)
        return if (index < 0) {
            false
        } else {
            headerKeyArray.remove(headerArray.keyAt(index))
            headerArray.removeAt(index)
            true
        }
    }

    /**
     * @param footerItemDelegate 添加 footer
     */
    fun <T, D : SpecialItemDelegate<T>> addFooter(footerItemDelegate: D, defaultData: T? = null) {
        val key = footerItemDelegate.hashCode()
        footerArray.put(key, footerItemDelegate)
        footerKeyArray.add(key)
        footerDataArray.put(key, defaultData)
    }

    /**
     * 移除 footer
     */
    fun <D : SpecialItemDelegate<*>> removeFooter(footerItemDelegate: D): Boolean {
        val index = footerArray.indexOfValue(footerItemDelegate)
        return if (index < 0) {
            false
        } else {
            footerKeyArray.remove(footerArray.keyAt(index))
            footerArray.removeAt(index)
            true
        }
    }

    /**
     * 设置 header 需要的数据
     */
    fun <T, D : SpecialItemDelegate<T>> resetHeaderData(
        headerItemDelegate: D,
        headerData: T
    ): Boolean {
        val index = headerArray.indexOfValue(headerItemDelegate)
        assert(index >= 0) {
            "头布局 ${headerItemDelegate.javaClass} 还没添加过"
        }
        return if (index < 0) {
            false
        } else {
            headerDataArray.put(
                headerArray.keyAt(index),
                headerData
            )
            true
        }
    }

    /**
     * 设置 footer 需要的数据
     */
    fun <T, D : SpecialItemDelegate<T>> resetFooterData(
        footerItemDelegate: D,
        footerData: T
    ): Boolean {
        val index = footerArray.indexOfValue(footerItemDelegate)
        assert(index >= 0) {
            "尾布局 ${footerItemDelegate.javaClass} 还没添加过"
        }
        return if (index < 0) {
            false
        } else {
            footerDataArray.put(
                footerArray.keyAt(index),
                footerData
            )
            true
        }
    }

    //==============================================================================================

    fun headerCount(): Int {
        return headerArray.size()
    }

    fun footerCount(): Int {
        return footerArray.size()
    }

    /**
     * 这个 Header 在 adapter 中的位置
     */
    fun <D : SpecialItemDelegate<*>> adapterPositionOfHeader(headerItemDelegate: D): Int {
        return headerArray.indexOfValue(headerItemDelegate)
    }

    /**
     * 这个 Footer 在 adapter 中的位置
     *
     * @param footerItemDelegate
     */
    fun <D : SpecialItemDelegate<*>> adapterPositionOfFooter(footerItemDelegate: D): Int {
        return headerCount() + dataList.size + footerArray.indexOfValue(footerItemDelegate)
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
            return headerKeyArray[position]
        }
        //中间数据
        val adjPosition = position - headerCount()
        val adapterCount = super.getItemCount()
        return if (adjPosition < adapterCount) {
            super.getItemViewType(adjPosition)
        }
        //尾部
        else {
            footerKeyArray[adjPosition - adapterCount]
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        val viewType = holder.itemViewType
        headerArray.get(viewType)?.viewAttachedToWindow(holder)
        super.onViewAttachedToWindow(holder)
        footerArray.get(viewType)?.viewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        val viewType = holder.itemViewType
        headerArray.get(viewType)?.viewDetachedFromWindow(holder)
        super.onViewDetachedFromWindow(holder)
        footerArray.get(viewType)?.viewDetachedFromWindow(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val headerDelegate = headerArray.get(viewType)
        //头部
        return if (headerDelegate != null) {
            headerDelegate.createViewHolder(parent)
        } else {
            val footerDelegate = footerArray.get(viewType)
            //尾部
            footerDelegate?.createViewHolder(parent)
            // 正常数据列
                ?: super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val type = holder.itemViewType
        //是头部
        if (isHeaderPosition(position)) {
            headerDataArray.get(type)?.let {
                headerArray.get(type)?.bindViewHolder(holder, it, payloads)
            }
            return
        }
        //交给父类处理
        val adjPosition = position - headerCount()
        if (adjPosition < super.getItemCount()) {
            super.onBindViewHolder(holder, adjPosition, payloads)
        }
        //尾部
        else {
            footerDataArray.get(type)?.let {
                footerArray.get(type)?.bindViewHolder(holder, it, payloads)
            }
        }
    }

    /**
     * [position] item 位置
     * [Int] 在 GridLayoutManager 里面的时候，这个位置上的 item 占据的宽度
     */
    override fun getItemSpanSizeInGridLayoutManager(position: Int, spanCount: Int): Int {
        //是头部
        if (isHeaderPosition(position)) {
            return spanCount
        }
        //是尾部
        return if (isFooterPosition(position)) {
            spanCount
        }
        //中间数据
        else {
            /*
                    注意：这里传递给父类的是 position，
                    而不像 getItemViewType() 函数里面那样返回 adjPosition
            */
            super.getItemSpanSizeInGridLayoutManager(position, spanCount)
        }
    }

}