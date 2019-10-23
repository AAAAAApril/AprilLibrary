package com.april.multiple

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.core.util.isNotEmpty
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 支持多样式的 Adapter
 *
 * 默认内置空列表占位视图支持
 */
open class MultipleSupport {

    //数据列
    internal val dataList = mutableListOf<Any>()
    //管理器
    internal val managerArray = SparseArray<Manager<*>>()
    // item 样式代理列表，
    // 以 itemViewType 为 key，以 item 样式代理为 value
    internal val itemDelegateArray = SparseArray<ItemDelegate<*, *>>()

    //空视图占位布局
    internal var placeholderView: View? = null
    internal var placeholderViewType = -1

    //==============================================================================================

    internal open fun getItemCount(): Int {
        return if (dataList.isEmpty() && placeholderView != null) {
            1
        } else {
            dataList.size
        }
    }

    internal open fun getItemViewType(position: Int): Int {
        //占位视图
        if (dataList.isEmpty() && placeholderView != null) {
            return placeholderViewType
        }
        val itemBean = dataList[position]
        return managerArray.valueAt(
            managerArray.indexOfKey(itemBean.javaClass.hashCode())
        ).getItemViewType(itemBean, position)
    }

    internal open fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        itemDelegateArray.get(holder.itemViewType)?.viewAttachedToWindow(holder)
    }

    internal open fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        itemDelegateArray.get(holder.itemViewType)?.viewDetachedFromWindow(holder)
    }

    internal open fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (placeholderView != null
            && viewType == placeholderViewType
        ) {
            object : RecyclerView.ViewHolder(placeholderView!!) {}
        } else {
            itemDelegateArray.get(viewType).createViewHolder(parent)
        }
    }

    internal open fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        itemDelegateArray.get(holder.itemViewType)?.bindViewHolder(
            holder, dataList[position], payloads
        )
    }

    //==============================================================================================

    /**
     * 适配 GridLayoutManager
     *
     * 注意：请在 adapter 注册完所有 ItemDelegate 之后，并且在
     * ItemDelegate 调用过 setSpanSizeInGridLayoutManager(Int) 函数之后，再调用此函数
     *
     * @param manager （已经，或者将要）设置给 RecyclerView 的 网格布局管理器
     */
    fun adapteGridLayoutManager(
        support: MultipleSupport,
        manager: GridLayoutManager
    ) {
        assert(support.itemDelegateArray.isNotEmpty()) {
            "请在注册完所有的 ItemDelegate 之后再调用此函数"
        }
        val spanSizeArray = IntArray(support.itemDelegateArray.size()) {
            support.itemDelegateArray[it].getSpanSizeInGridLayoutManager()
        }
        //求出最小公倍数
        val leastCommonMultiple = getLeastCommonMultiple(spanSizeArray)
        //变化的倍数
        val times = (leastCommonMultiple / spanSizeArray.max()!!)
        support.itemDelegateArray.forEach { _, delegate ->
            //  给每个 item 设置这个倍数
            delegate.setAdaptedTimesInGridLayoutManager(times)
        }
        //重新给 GridLayoutManager 的 spanSizeLookup 赋值
        manager.spanSizeLookup = CrossSpanSizeLookUp(support, leastCommonMultiple)
    }

    /**
     * [position] item 位置
     * [Int] 在 GridLayoutManager 里面的时候，这个位置上的 item 占据的宽度
     */
    internal open fun getItemSpanSizeInGridLayoutManager(position: Int, spanCount: Int): Int {
        return if (dataList.isEmpty() && placeholderView != null) {
            //限定占位布局为拉通展示
            spanCount
        } else {
            val delegate = itemDelegateArray.get(getItemViewType(position))
            if (delegate.crossRowWhenGridLayout()) {
                spanCount
            } else {
                //获取适配过的 spanSize
                delegate.getAdaptedSpanSizeInGridLayoutManager()
            }
        }
    }
}

/**
 * 在
 * [GridLayoutManager.setSpanSizeLookup(GridLayoutManager.SpanSizeLookup)]
 * 函数调用，为某些位置上的 item 实现跨行或者跨列展示
 *
 * 为什么是这样处理，举个例子：
 * GridLayoutManager 设置每 1 行展示 3 列
 * 如果想老老实实每一行展示 3 个的话，就默认返回 1 就行了，
 * 如果你想独占一行，那就要返回 3，表示我要占据 3 个位置，
 * 如果想占两个位置，那就返回 2
 *
 * 如果是希望总个数为单位 1，那么，想象一个特殊情况：
 * GridLayoutManager 规定了每一行展示 7 个，
 * 第一种 item 想占 1 个位置
 * 第二种 item 想占 2 个位置
 * 第三种 item 想占 3 个位置
 * 第三种 item 想占 5 个位置
 * 第四种 item 想占 7 个位置
 *
 * 这时候，就需要求出这些数的最小公倍数，并将其赋值给 spanCount
 */
private class CrossSpanSizeLookUp(
    private val support: MultipleSupport,
    private val spanCount: Int
) : GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return support.getItemSpanSizeInGridLayoutManager(position, spanCount)
    }
}
