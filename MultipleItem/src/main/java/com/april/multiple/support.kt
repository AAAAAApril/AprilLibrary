package com.april.multiple

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

interface IMultipleSupport {
    val support: MultipleSupport

    fun getItemCount(): Int {
        return support.getItemCount()
    }

    fun getItemViewType(position: Int): Int {
        return support.getItemViewType(position)
    }

    fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        support.onAttachedToRecyclerView(recyclerView)
    }

    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        support.onViewAttachedToWindow(holder)
    }

    fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        support.onViewDetachedFromWindow(holder)
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return support.onCreateViewHolder(parent, viewType)
    }

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        return support.onBindViewHolder(holder, position, payloads)
    }

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    //==============================================================================================

    fun placeholder(targetRecyclerView: RecyclerView,
                    @LayoutRes placeholderViewResId: Int): View {
        return support.placeholder(targetRecyclerView, placeholderViewResId)
    }

    fun placeholder(placeholderItemDelegate: CrossItemDelegate) {
        support.placeholder(placeholderItemDelegate)
    }

    fun clearPlaceholder() {
        support.clearPlaceholder()
    }

    fun <T> only(itemBeanClass: Class<T>,
                 itemDelegate: ItemDelegate<T, *>) {
        support.only(itemBeanClass, itemDelegate)
    }

    fun <T> many(itemBeanClass: Class<T>,
                 vararg delegate: ItemDelegate<T, *>,
                 recognizer: Recognizer<T>) {
        val hashCode = itemBeanClass.hashCode()
        val manager = Manager<T>(hashCode, support)
        manager.setItemDelegates(
            Array(delegate.size) { index ->
                delegate[index]
            },
            recognizer
        )
        support.managerArray.put(hashCode, manager)
    }

    fun <T> manager(itemBeanClass: Class<T>): Manager<T> {
        return support.manager(itemBeanClass)
    }

}

/**
 * 支持多样式的 Adapter
 *
 * 默认内置空列表占位视图支持
 */
open class MultipleSupport {

    //数据列
    val dataList = mutableListOf<Any>()
    //管理器
    val managerArray = SparseArray<Manager<*>>()
    // item 样式代理列表，
    // 以 itemViewType 为 key，以 item 样式代理为 value
    val itemDelegateArray = SparseArray<ItemDelegate<*, *>>()

    //空视图占位布局
    var placeholderItemDelegate: CrossItemDelegate? = null

    //==============================================================================================

    internal fun getItemCount(): Int {
        return if (dataList.isEmpty() && placeholderItemDelegate != null) {
            1
        } else {
            dataList.size
        }
    }

    internal fun getItemViewType(position: Int): Int {
        //占位视图
        if (dataList.isEmpty() && placeholderItemDelegate != null) {
            return placeholderItemDelegate!!.viewType
        }
        val itemBean = dataList[position]
        return managerArray.valueAt(
            managerArray.indexOfKey(itemBean.javaClass.hashCode())
        ).getItemViewType(itemBean, position)
    }

    internal fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        (recyclerView.layoutManager as? GridLayoutManager)?.let {
            it.spanSizeLookup = CrossSpanSizeLookUp(this, it.spanCount)
        }
    }

    internal fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (placeholderItemDelegate != null
            && holder.itemViewType == placeholderItemDelegate!!.viewType) {
            placeholderItemDelegate!!.viewAttachedToWindow(holder)
        } else {
            itemDelegateArray.get(holder.itemViewType).viewAttachedToWindow(holder)
        }
    }

    internal fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if (placeholderItemDelegate != null
            && holder.itemViewType == placeholderItemDelegate!!.viewType) {
            placeholderItemDelegate!!.viewDetachedFromWindow(holder)
        } else {
            itemDelegateArray.get(holder.itemViewType).viewDetachedFromWindow(holder)
        }
    }

    internal fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (placeholderItemDelegate != null
            && viewType == placeholderItemDelegate!!.viewType) {
            placeholderItemDelegate!!.createViewHolder(parent)
        } else {
            itemDelegateArray.get(viewType).createViewHolder(parent)
        }
    }

    internal fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (placeholderItemDelegate != null
            && holder.itemViewType == placeholderItemDelegate!!.viewType) {
            placeholderItemDelegate!!.bindViewHolder(
                holder, position, 0, payloads
            )
        } else {
            itemDelegateArray.get(holder.itemViewType).bindViewHolder(
                holder, position, dataList[position], payloads
            )
        }
    }

    //==============================================================================================

    /**
     * [position] item 位置
     * [Boolean] item 是否跨行或者跨列
     */
    internal open fun itemCrossRowOrColumn(position: Int): Boolean {
        return if (dataList.isEmpty() && placeholderItemDelegate != null) {
            placeholderItemDelegate!!.crossRowWhenGridLayout()
        } else {
            itemDelegateArray.get(getItemViewType(position)).crossRowWhenGridLayout()
        }
    }

    //==============================================================================================

    internal fun placeholder(targetRecyclerView: RecyclerView,
                             @LayoutRes placeholderViewResId: Int): View {
        val placeholderView = LayoutInflater.from(targetRecyclerView.context)
            .inflate(placeholderViewResId, targetRecyclerView, false)
        placeholderItemDelegate = CrossItemDelegate(placeholderView)
        return placeholderView
    }

    internal fun placeholder(placeholderItemDelegate: CrossItemDelegate) {
        this.placeholderItemDelegate = placeholderItemDelegate
    }

    internal fun clearPlaceholder() {
        placeholderItemDelegate = null
    }

    internal fun <T> only(itemBeanClass: Class<T>,
                          itemDelegate: ItemDelegate<T, *>) {
        val hashCode = itemBeanClass.hashCode()
        val manager = Manager<T>(hashCode, this)
        manager.only(itemDelegate)
        managerArray.put(hashCode, manager)
    }

    internal fun <T> manager(itemBeanClass: Class<T>): Manager<T> {
        val hashCode = itemBeanClass.hashCode()
        val manager = Manager<T>(hashCode, this)
        managerArray.put(hashCode, manager)
        return manager
    }

    //==============================================================================================

    internal fun getData(position: Int): Any {
        return dataList[position]
    }

    /**
     * [Int] 数据插入的实际下标
     */
    internal fun addData(any: Any): Int {
        dataList.add(any)
        return dataList.lastIndex
    }

    /**
     * [Int] 数据插入的实际下标
     */
    internal fun insertData(any: Any, position: Int): Int {
        dataList.add(position, any)
        return position
    }

    /**
     * [Int] 数据删除的实际下标
     */
    internal fun removeData(position: Int): Int {
        dataList.removeAt(position)
        return position
    }

    /**
     * [Int] 数据删除的实际下标
     */
    internal fun removeLastData(): Int {
        val index = dataList.lastIndex
        dataList.removeAt(index)
        return index
    }

    /**
     * [Int] 数据添加的实际下标起点
     */
    internal fun addDataList(dataList: MutableList<Any>): Int {
        val index = dataList.size
        this.dataList.addAll(dataList)
        return index
    }

    internal fun resetDataList(dataList: MutableList<Any>) {
        this.dataList.clear()
        this.dataList.addAll(dataList)
    }

    internal fun clearDataList() {
        dataList.clear()
    }
}

/**
 * item 样式代理
 *
 * [T] 绑定的数据实体
 * [VH] 绑定的 ViewHolder
 */
abstract class ItemDelegate<T, VH : RecyclerView.ViewHolder> {

    /**
     * 内部使用，用于转换类型
     */
    internal fun viewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        onViewAttachedToWindow(holder as VH)
    }

    /**
     * 内部使用，用于转换类型
     */
    internal fun viewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        onViewDetachedFromWindow(holder as VH)
    }

    /**
     * 内部使用，用于转换类型
     *
     * [holder]   viewHolder
     * [position] 位置
     * [any]      item数据实体（在某些情况下，传递过来的数据可能为 null）
     */
    internal fun bindViewHolder(holder: RecyclerView.ViewHolder, position: Int, any: Any, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder as VH, any as T)
        } else {
            onBindViewHolder(holder as VH, any as T, payloads)
        }
    }

    /**
     * [itemView] 给 itemView 设置瀑布流布局时通行(或者通列)展示
     */
    private fun setCrossRowWhenStaggeredGridLayout(itemView: View, crossRowWhenStaggeredGridLayout: Boolean) {
        //由于默认是不会拉通展示，所以如果和默认值相同，则不继续执行，可以稍微提升那么一点点的性能……
        if (!crossRowWhenStaggeredGridLayout) {
            return
        }
        val layoutParams = itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams
            ?: return
        layoutParams.isFullSpan = true
        itemView.layoutParams = layoutParams
    }

    //==============================================================================================

    /**
     * [Boolean] 在 [androidx.recyclerview.widget.GridLayoutManager]
     * 的布局管理器下，此类型的 item 是否通行展示
     */
    open fun crossRowWhenGridLayout(): Boolean {
        return false
    }

    /**
     * [Boolean] 在 [androidx.recyclerview.widget.StaggeredGridLayoutManager] 布局管理器模式下，此类型的 item 是否通行展示
     */
    open fun crossRowWhenStaggeredGridLayout(): Boolean {
        return false
    }

    /**
     * [holder] item view 绑定到窗口上时回调
     */
    protected fun onViewAttachedToWindow(holder: VH) {

    }

    /**
     * [holder] item view 从窗口上解绑时回调
     */
    protected fun onViewDetachedFromWindow(holder: VH) {

    }

    /**
     * 当使用了 [androidx.recyclerview.widget.DiffUtil] 工具类时，可能需要用到此函数
     */
    protected fun onBindViewHolder(holder: VH, t: T, payloads: List<Any>) {

    }

    /**
     * 创建 viewHolder
     *
     * [parent] 来源于 [RecyclerView.Adapter.onCreateViewHolder]
     * 注：源码中可以看到，传入的 parent 其实就是绑定的 RecyclerView 本身。
     * [VH] viewHolder
     */
    fun createViewHolder(parent: ViewGroup): VH {
        val itemView = onCreateItemView(parent)
        setCrossRowWhenStaggeredGridLayout(itemView, crossRowWhenStaggeredGridLayout())
        return onCreateViewHolder(parent, itemView)
    }

    /**
     * [parent] [createViewHolder]
     * [View] 创建 itemView
     */
    protected abstract fun onCreateItemView(parent: ViewGroup): View

    /**
     * [parent]   [createViewHolder]
     * [itemView] [onCreateItemView] 函数返回的 itemView
     * [VH] 创建出一个 ViewHolder
     */
    protected abstract fun onCreateViewHolder(parent: ViewGroup, itemView: View): VH

    /**
     * [holder] viewHolder
     * [t] item 数据实体
     */
    protected abstract fun onBindViewHolder(holder: VH, t: T)

}

//==============================================================================================

fun <T> T.getData(position: Int): Any
    where T : RecyclerView.Adapter<*>,
          T : IMultipleSupport {
    return support.getData(position)
}

fun <T> T.addData(any: Any)
    where T : RecyclerView.Adapter<*>,
          T : IMultipleSupport {
    notifyItemInserted(support.addData(any))
}

fun <T> T.insertData(any: Any, position: Int)
    where T : RecyclerView.Adapter<*>,
          T : IMultipleSupport {
    notifyItemInserted(support.insertData(any, position))
}

fun <T> T.removeData(position: Int)
    where T : RecyclerView.Adapter<*>,
          T : IMultipleSupport {
    notifyItemRemoved(support.removeData(position))
}

fun <T> T.removeLastData()
    where T : RecyclerView.Adapter<*>,
          T : IMultipleSupport {
    notifyItemRemoved(support.removeLastData())
}

fun <T> T.getDataList(): MutableList<Any>
    where T : RecyclerView.Adapter<*>,
          T : IMultipleSupport {
    return support.dataList
}

fun <T> T.addDataList(dataList: MutableList<Any>)
    where T : RecyclerView.Adapter<*>,
          T : IMultipleSupport {
    if (dataList.isEmpty()) {
        return
    }
    notifyItemRangeInserted(support.addDataList(dataList), dataList.size)
}

fun <T> T.resetDataList(dataList: MutableList<Any>)
    where T : RecyclerView.Adapter<*>,
          T : IMultipleSupport {
    support.resetDataList(dataList)
    notifyDataSetChanged()
}

fun <T> T.clearDataList()
    where T : RecyclerView.Adapter<*>,
          T : IMultipleSupport {
    support.clearDataList()
    notifyDataSetChanged()
}