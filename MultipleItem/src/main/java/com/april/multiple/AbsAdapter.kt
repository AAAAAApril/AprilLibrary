package com.april.multiple

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.april.multiple.diff.DiffCallBack
import com.april.multiple.diff.MultipleDiffCallBack
import com.april.multiple.diff.MultipleUpdateCallBack

/**
 * 一个抽象的普通 adapter
 */
abstract class AbsAdapter<T : Any>(
    @LayoutRes private val itemLayoutRes: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * 绑定数据
     */
    protected abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder, data: T)

    //数据列
    private val dataList = mutableListOf<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                itemLayoutRes, parent, false
            )
        ) {}
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindViewHolder(holder, dataList[position])
    }

    /**
     * 刷新数据列
     */
    fun notifyDataList(
        newDataList: List<T>,
        diffCallBack: DiffCallBack<T>,
        /**
         * 当新旧两个列表的排序方式相同时，这个值为 false 可以提高性能，
         * 比如聊天消息列表，增加新消息时，两个列表的排序顺序其实是一样的，只是数据条数不同而已
         */
        detectMoves: Boolean = false
    ) {
        if (dataList.isEmpty()) {
            dataList.addAll(newDataList)
            notifyDataSetChanged()
            return
        }
        //取出旧数据列
        val oldDataList = dataList
        //比对结果
        val result = DiffUtil.calculateDiff(object : MultipleDiffCallBack<T>(
            oldDataList, newDataList, diffCallBack
        ) {}, detectMoves)
        //清除旧数列
        dataList.clear()
        //加载新数据列
        dataList.addAll(newDataList)
        //提交对比结果到 adapter，应用变化
        result.dispatchUpdatesTo(MultipleUpdateCallBack(this))
    }

}

/**
 * DSL 写法的 Adapter
 */
open class AbsAdapterDSL<T : Any>(
    @LayoutRes itemLayoutRes: Int,
    private val block: (holder: RecyclerView.ViewHolder, data: T) -> Unit
) : AbsAdapter<T>(itemLayoutRes) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, data: T) {
        block.invoke(holder, data)
    }
}