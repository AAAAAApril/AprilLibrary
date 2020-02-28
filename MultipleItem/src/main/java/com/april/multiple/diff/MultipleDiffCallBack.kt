package com.april.multiple.diff

import androidx.recyclerview.widget.DiffUtil

/**
 * 比对回调
 */
abstract class MultipleDiffCallBack<T : Any>(
    private val oldDataList: List<T>,
    private val newDataList: List<T>,
    private val diffCallBack: DiffCallBack<T>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return diffCallBack.areItemsTheSame(
            oldDataList[oldItemPosition],
            newDataList[newItemPosition]
        )
    }

    override fun getOldListSize(): Int = oldDataList.size

    override fun getNewListSize(): Int = newDataList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return diffCallBack.areContentsTheSame(
            oldDataList[oldItemPosition],
            newDataList[newItemPosition]
        )
    }
}

interface DiffCallBack<T : Any> {

    /**
     * item 是否相同
     */
    fun areItemsTheSame(
        oldData: T, newData: T
    ): Boolean

    /**
     * 内容是否相同
     */
    fun areContentsTheSame(
        oldData: T, newData: T
    ): Boolean
}