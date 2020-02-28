package com.april.multiple

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.april.multiple.diff.DiffCallBack
import com.april.multiple.diff.MultipleDiffCallBack
import com.april.multiple.diff.MultipleUpdateCallBack

/**
 * 多样式 Adapter
 */
open class MultipleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    open val support = MultipleSupport()

    /**
     * 设置占位布局 item 样式代理
     */
    fun <T : SpecialItemDelegate<*>> setPlaceholder(placeholderItemDelegate: T?) {
        support.placeholderItemDelegate = placeholderItemDelegate
    }

    /**
     * 设置占位布局所需的数据
     */
    fun <T : Any> resetPlaceholderData(placeholderData: T?) {
        support.placeholderBean = placeholderData
        //如果数据列是空的，并且设置了占位布局
        if (support.dataList.isEmpty()
            && support.placeholderItemDelegate != null
        ) {
            //就刷新一下
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return support.getItemCount()
    }

    override fun getItemViewType(position: Int): Int {
        return support.getItemViewType(position)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        support.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        support.onViewDetachedFromWindow(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return support.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        support.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    open fun <T : Any> getData(dataPosition: Int): T {
        return support.dataList[dataPosition] as T
    }

    open fun <T : Any> getDataList(): MutableList<T> {
        return support.dataList as MutableList<T>
    }

    open fun <T : Any> resetData(any: T, dataPosition: Int) {
        support.dataList[dataPosition] = any
        notifyItemChanged(dataPosition)
    }

    open fun <T : Any> resetDataList(dataList: List<T>) {
        support.dataList.clear()
        support.dataList.addAll(dataList)
        notifyDataSetChanged()
    }

    open fun clearDataList() {
        val count = support.dataList.size
        support.dataList.clear()
        notifyItemRangeRemoved(0, count)
    }

    open fun <T : Any> addData(any: T) {
        support.dataList.add(any)
        notifyItemInserted(support.dataList.lastIndex)
    }

    open fun <T : Any> addDataList(dataList: List<T>) {
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
            index,
            dataList.size
        )
    }

    open fun <T : Any> insertData(any: T, dataPosition: Int) {
        support.dataList.add(dataPosition, any)
        notifyItemInserted(dataPosition)
    }

    open fun <T : Any> insertDataList(dataPosition: Int, dataList: List<T>) {
        if (dataList.isEmpty()) {
            return
        }
        support.dataList.addAll(dataPosition, dataList)
        notifyItemRangeInserted(dataPosition, dataList.size)
    }

    open fun removeData(dataPosition: Int) {
        support.dataList.removeAt(dataPosition)
        notifyItemRemoved(dataPosition)
    }

    open fun <T : Any> removeDataList(dataList: List<T>) {
        support.dataList.removeAll(dataList)
        notifyDataSetChanged()
    }

    open fun <T : Any> notifyDataList(newDataList: List<T>, diffCallBack: DiffCallBack<T>) {
        if (support.dataList.isEmpty()) {
            resetDataList(newDataList)
            return
        }
        val oldDataList = support.dataList as List<T>
        DiffUtil.calculateDiff(object : MultipleDiffCallBack<T>(
            oldDataList, newDataList, diffCallBack
        ) {}).dispatchUpdatesTo(getUpdateCallBack())
    }

    protected open fun getUpdateCallBack(): ListUpdateCallback {
        return MultipleUpdateCallBack(this, 0)
    }

}