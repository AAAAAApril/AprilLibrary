package com.april.multiple.select

import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView

/**
 * 可选 item adapter
 *
 * 没有头尾布局以及占位布局，只有纯粹的数据列
 *
 * TODO  完善多 item 支持、完善数据刷新方式(要使用DiffUtil)、提供更多的操作函数
 */
class SelectableAdapter<T : Any>(
    //可同时选中的数量，默认 1，单选
    @IntRange(from = 1)
    private val selectableCount: Int = 1,
    private val delegate: SelectableItemDelegate<T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //选中的 item 的数量变化监听
    var countChangedListener: OnSelectedChangedListener? = null

    //数据包装实体列表
    private val dataWrapperList = mutableListOf<SelectableDataWrapper<T>>()

    //被选中的位置列表
    private val selectedPositionList = ArrayList<Int>(selectableCount)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegate.createViewHolder(parent)
    }

    override fun getItemCount(): Int = dataWrapperList.size

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        delegate.viewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        delegate.viewDetachedFromWindow(holder)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val wrapper = dataWrapperList[position].copy()
        //item 绑定数据
        delegate.bindViewHolder(holder, wrapper, payloads)
        //item 设置监听
        holder.itemView.apply {
            isEnabled = wrapper.enable
        }.setOnClickListener {
            val bean = dataWrapperList[position].copy()
            //新状态与旧状态不同
            if (bean.selected != delegate.checkableItemClicked(holder, bean)) {
                onNewItemClicked(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    /**
     * 添加一条数据
     */
    fun addData(data: T, enable: Boolean = true) {
        dataWrapperList.add(SelectableDataWrapper(data, enable = enable))
        notifyItemInserted(dataWrapperList.lastIndex)
    }

    /**
     * 插入一条数据
     */
    fun insertData(data: T, position: Int, enable: Boolean = true) {
        dataWrapperList.add(position, SelectableDataWrapper(data, enable = enable))
        notifyItemInserted(position)
    }

    /**
     * 移除一条数据
     */
    fun removeData(data: T) {
        val index = dataWrapperList.indexOfFirst {
            it.data == data
        }
        if (index >= 0) {
            val result = selectedPositionList.remove(index)
            dataWrapperList.removeAt(index)
            notifyItemRemoved(index)
            //移除成功
            if (result) {
                //回调选中数量变化监听
                countChangedListener?.onSelectedChanged(this, selectedPositionList)
            }
        }
    }

    /**
     * 重设数据
     */
    fun resetData(newData: T, position: Int, enable: Boolean = true) {
        dataWrapperList[position] = dataWrapperList[position].copy(data = newData, enable = enable)
        notifyItemChanged(position)
    }

    /**
     * 选中某个位置
     *
     * [Boolean] 是否成功
     */
    fun selectPosition(position: Int): Boolean {
        val wrapper = dataWrapperList[position].copy()
        val oldStatus = wrapper.selected
        //不可选中
        if (!wrapper.enable) {
            return false
        }
        //已经选中
        if (oldStatus) {
            return false
        }
        //提交位置
        onNewItemClicked(position)
        return true
    }

    /**
     * 选中某条数据
     */
    fun selectData(data: T): Boolean {
        val index = dataWrapperList.indexOfFirst {
            it.data == data
        }
        if (index < 0) {
            return false
        }
        return selectPosition(index)
    }

    /**
     * 添加数据列
     */
    fun addDataList(dataList: List<T>) {
        val startPosition: Int = dataWrapperList.size
        val addWrapperList = mutableListOf<SelectableDataWrapper<T>>()
        dataList.forEach {
            addWrapperList.add(SelectableDataWrapper(it))
        }
        dataWrapperList.addAll(addWrapperList)
        notifyItemRangeInserted(startPosition, addWrapperList.size)
    }

    /**
     * 重设数据列
     */
    fun resetDataList(dataList: List<T>) {
        //移除旧的
        val oldSize = dataWrapperList.size
        dataWrapperList.clear()
        selectedPositionList.clear()
        notifyItemRangeRemoved(0, oldSize)
        //添加新的
        val wrapperList = mutableListOf<SelectableDataWrapper<T>>()
        dataList.forEach { data ->
            wrapperList.add(SelectableDataWrapper(data))
        }
        dataWrapperList.addAll(wrapperList)
        notifyItemRangeChanged(0, dataWrapperList.size)
        //回调选中数量变化监听
        countChangedListener?.onSelectedChanged(this, selectedPositionList)
    }

    /**
     * 获取被选中的数据列
     */
    fun getSelectedDataList(): List<T> {
        val dataList = mutableListOf<T>()
        dataWrapperList.forEach {
            if (it.selected) {
                dataList.add(it.data)
            }
        }
        return dataList
    }

    /**
     * 被选中的数量
     */
    fun getSelectedCount(): Int = selectedPositionList.size

    /**
     * 取消所有已选中
     */
    fun clearAllSelected() {
        dataWrapperList.forEach {
            it.selected = false
        }
        selectedPositionList.clear()
        notifyDataSetChanged()
        //回调选中数量变化监听
        countChangedListener?.onSelectedChanged(this, selectedPositionList)
    }

    /**
     * 有新的 item 被点击了
     */
    private fun onNewItemClicked(position: Int) {
        //该位置上当前的状态
        val nowChecked: Boolean = dataWrapperList[position].selected
        //当前是选中，则表示要取消
        if (nowChecked) {
            selectedPositionList.remove(position)
            //取消选中该位置
            dataWrapperList[position].selected = false
            notifyItemChanged(position)
        }
        //当前未选中，表示要选中
        else {
            //选中的数量暂时小于可以选中的数量
            if (selectedPositionList.size < selectableCount) {
                selectedPositionList.add(position)
                //选中当前的
                dataWrapperList[position].selected = true
                notifyItemChanged(position)
            }
            //已经达到最大选中数量了，则最早被选中的需要取消选中
            else {
                //最早被选中的位置
                val firstPosition = selectedPositionList.first()
                selectedPositionList.remove(firstPosition)
                selectedPositionList.add(position)
                //取消以前的
                dataWrapperList[firstPosition].selected = false
                notifyItemChanged(firstPosition)
                //选中当前的
                dataWrapperList[position].selected = true
                notifyItemChanged(position)
            }
        }
        //回调选中数量变化监听
        countChangedListener?.onSelectedChanged(this, selectedPositionList)
    }

}