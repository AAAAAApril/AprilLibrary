package com.april.multiple.checkable

import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView

/**
 * 可选 item adapter
 *
 * 没有头尾布局以及占位布局，只有纯粹的数据列
 */
class CheckableAdapter<T : Any>(
    //可同时选中的数量，默认 1，单选
    @IntRange(from = 1)
    private val checkableCount: Int = 1,
    private val delegate: CheckableItemDelegate<T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //选中的 item 的数量变化监听
    var countChangedListener: OnCheckedChangedListener? = null

    //数据包装实体列表
    private val dataWrapperList = mutableListOf<CheckableDataWrapper<T>>()
    //被选中的位置列表
    private val checkedPositionList = ArrayList<Int>(checkableCount)

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
        val bean = dataWrapperList[position].copy()
        //item 绑定数据
        delegate.bindViewHolder(holder, bean, payloads)
        //item 设置监听
        holder.itemView.setOnClickListener {
            //新状态与旧状态不同
            if (bean.checked != delegate.checkableItemClicked(holder, bean)) {
                onNewItemClicked(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    /**
     * 添加一条数据
     */
    fun addData(data: T) {
        dataWrapperList.add(CheckableDataWrapper(data))
        notifyItemInserted(dataWrapperList.lastIndex)
    }

    /**
     * 插入一条数据
     */
    fun insertData(data: T, position: Int) {
        dataWrapperList.add(position, CheckableDataWrapper(data))
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
            checkedPositionList.remove(index)
            dataWrapperList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    /**
     * 重设数据
     */
    fun resetData(newData: T, position: Int) {
        dataWrapperList[position] = dataWrapperList[position].copy(data = newData)
        notifyItemChanged(position)
    }

    /**
     * 添加数据列
     */
    fun addDataList(dataList: List<T>) {
        val startPosition: Int = dataWrapperList.size
        val addWrapperList = mutableListOf<CheckableDataWrapper<T>>()
        dataList.forEach {
            addWrapperList.add(CheckableDataWrapper(it))
        }
        dataWrapperList.addAll(addWrapperList)
        notifyItemRangeInserted(startPosition, addWrapperList.size)
    }

    /**
     * 重设数据列
     */
    fun resetDataList(dataList: List<T>) {
        dataWrapperList.clear()
        dataList.forEach {
            dataWrapperList.add(CheckableDataWrapper(it))
        }
        checkedPositionList.clear()
        notifyItemRangeChanged(0, dataWrapperList.size)
    }

    /**
     * 获取被选中的数据列
     */
    fun getCheckedDataList(): MutableList<T> {
        val dataList = mutableListOf<T>()
        dataWrapperList.forEach {
            if (it.checked) {
                dataList.add(it.data)
            }
        }
        return dataList
    }

    /**
     * 被选中的数量
     */
    fun getCheckedCount(): Int = checkedPositionList.size

    /**
     * 有新的 item 被点击了
     */
    private fun onNewItemClicked(position: Int) {
        //该位置上当前的状态
        val nowChecked: Boolean = dataWrapperList[position].checked
        //当前是选中，则表示要取消
        if (nowChecked) {
            checkedPositionList.remove(position)
            //取消选中该位置
            dataWrapperList[position].checked = false
            notifyItemChanged(position)
        }
        //当前未选中，表示要选中
        else {
            //选中的数量暂时小于可以选中的数量
            if (checkedPositionList.size < checkableCount) {
                checkedPositionList.add(position)
                //选中当前的
                dataWrapperList[position].checked = true
                notifyItemChanged(position)
            }
            //已经达到最大选中数量了，则最早被选中的需要取消选中
            else {
                //最早被选中的位置
                val firstPosition = checkedPositionList.first()
                checkedPositionList.remove(firstPosition)
                checkedPositionList.add(position)
                //取消以前的
                dataWrapperList[firstPosition].checked = false
                notifyItemChanged(firstPosition)
                //选中当前的
                dataWrapperList[position].checked = true
                notifyItemChanged(position)
            }
        }
        //回调选中数量变化监听
        countChangedListener?.onCheckedChanged(checkedPositionList)
    }

}