package com.april.multiple

import android.util.SparseArray
import android.view.ViewGroup
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
    internal val itemDelegateArray = SparseArray<MultipleItemDelegate<*, *>>()

    //空视图占位布局
    private var placeholderBean: Any? = null
    private var placeholderItemDelegate: SpecialItemDelegate<*>? = null
    private var placeholderItemType = -1

    //==============================================================================================

    /**
     * 添加占位布局
     */
    fun <T, D : SpecialItemDelegate<T>> setPlaceHolder(
        placeholderItemDelegate: D,
        placeHolderData: T? = null
    ) {
        this.placeholderBean = placeHolderData
        this.placeholderItemType = placeholderItemDelegate.hashCode()
        this.placeholderItemDelegate = placeholderItemDelegate
    }

    /**
     * 重设占位布局的数据
     */
    fun <T, D : SpecialItemDelegate<T>> resetPlaceHolderData(
        //这个参数只是用来做数据类型约束，并没有其他作用
        placeholderItemDelegate: D,
        placeHolderData: T
    ) {
        this.placeholderBean = placeHolderData
    }

    /**
     * 移除占位布局
     */
    fun removePlaceHolder() {
        this.placeholderBean = null
        this.placeholderItemDelegate = null
        this.placeholderItemType = -1
    }

    internal open fun getItemCount(): Int {
        return if (dataList.isEmpty() && placeholderItemDelegate != null) {
            1
        } else {
            dataList.size
        }
    }

    internal open fun getItemViewType(position: Int): Int {
        //占位视图
        if (dataList.isEmpty() && placeholderItemDelegate != null) {
            return placeholderItemType
        }
        val itemBean = dataList[position]
        var clazz: Class<in Any>? = itemBean.javaClass
        var index: Int = managerArray.indexOfKey(clazz.hashCode())
        /*
            这里的循环，是实现数据实体父类型约束的关键。
            A 作为父类被添加到约束队列之后，其子类 A1、A2 如果未单独添加约束，那么在传入 A1、A2 数据类型时，
            会查找并使用 A 对应的 item 样式代理。
            如果在此处出现报错，表示该位置上的数据类型未作为多样式 item 的约束数据类型
         */
        while (index < 0) {
            clazz = clazz?.superclass
            index = managerArray.indexOfKey(clazz.hashCode())
        }
        return managerArray.valueAt(index).getItemViewType(itemBean, position)
    }

    internal open fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        val viewType = holder.itemViewType
        if (viewType == placeholderItemType) {
            placeholderItemDelegate?.viewAttachedToWindow(holder)
        } else {
            itemDelegateArray.get(viewType)?.viewAttachedToWindow(holder)
        }
    }

    internal open fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        val viewType = holder.itemViewType
        if (viewType == placeholderItemType) {
            placeholderItemDelegate?.viewDetachedFromWindow(holder)
        } else {
            itemDelegateArray.get(viewType)?.viewDetachedFromWindow(holder)
        }
    }

    internal open fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (
            viewType == placeholderItemType
            && placeholderItemDelegate != null
        ) {
            placeholderItemDelegate!!.createViewHolder(parent)
        } else {
            itemDelegateArray.get(viewType).createViewHolder(parent)
        }
    }

    internal open fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val type = holder.itemViewType
        if (type == placeholderItemType) {
            placeholderBean?.let {
                placeholderItemDelegate?.bindViewHolder(holder, it, payloads)
            }
        } else {
            itemDelegateArray.get(type)?.bindViewHolder(
                holder, dataList[position], payloads
            )
        }
    }

    //==============================================================================================

    /**
     * 适配 GridLayoutManager
     *
     * FIXME  实现还有问题，需要重新设计
     *
     * 注意：请在 adapter 注册完所有 MultipleItemDelegate 之后，并且在
     * MultipleItemDelegate 调用过 setSpanSizeInGridLayoutManager(Int) 函数之后，再调用此函数
     *
     * @param manager （已经，或者将要）设置给 RecyclerView 的 网格布局管理器
     */
    fun adaptGridLayoutManager(manager: GridLayoutManager) {
//        assert(itemDelegateArray.isNotEmpty()) {
//            "请在注册完所有的 MultipleItemDelegate 之后再调用此函数"
//        }
//        val spanSizeArray = IntArray(itemDelegateArray.size()) {
//            itemDelegateArray.valueAt(it).getSpanSizeInGridLayoutManager()
//        }
//        //求出最小公倍数
//        val leastCommonMultiple = getLeastCommonMultiple(spanSizeArray)
//        //变化的倍数
//        val times = (leastCommonMultiple / spanSizeArray.max()!!)
//        itemDelegateArray.forEach { _, delegate ->
//            //  给每个 item 设置这个倍数
//            delegate.setAdaptedTimesInGridLayoutManager(times)
//        }
//        //重新给 GridLayoutManager 的 spanSizeLookup 赋值
//        manager.spanSizeLookup = CrossSpanSizeLookUp(this, leastCommonMultiple)
        manager.spanSizeLookup = CrossSpanSizeLookUp(this, manager.spanCount)
    }

    /**
     * [position] item 位置
     * [Int] 在 GridLayoutManager 里面的时候，这个位置上的 item 占据的宽度
     */
    internal open fun getItemSpanSizeInGridLayoutManager(position: Int, spanCount: Int): Int {
        return if (dataList.isEmpty() && placeholderItemDelegate != null) {
            //限定占位布局为拉通展示
            spanCount
        } else {
            val delegate = itemDelegateArray.get(getItemViewType(position))
            if (delegate.isCrossRowWhenGridLayout()) {
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
 * 这时候，就需要求出这些数的最小公倍数，并将其赋值给 spanCount，
 * 同时，除了最大的那个数，其他所有的数都需要乘以最小公倍数和最大那个数的比值
 */
private class CrossSpanSizeLookUp(
    private val support: MultipleSupport,
    private val spanCount: Int
) : GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return support.getItemSpanSizeInGridLayoutManager(position, spanCount)
    }
}
