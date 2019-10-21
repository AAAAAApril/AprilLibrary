package com.april.multiple

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

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
     * [any]      item数据实体（在某些情况下，传递过来的数据可能为 null）
     */
    internal fun bindViewHolder(holder: RecyclerView.ViewHolder, any: Any, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder as VH, any as T)
        } else {
            onBindViewHolder(holder as VH, any as T, payloads)
        }
    }

    /**
     * [itemView] 给 itemView 设置瀑布流布局时通行(或者通列)展示
     */
    private fun setCrossRowWhenStaggeredGridLayout(
        itemView: View,
        crossRowWhenStaggeredGridLayout: Boolean
    ) {
        //由于默认是不会拉通展示，所以如果和默认值相同，则不继续执行，可以稍微提升那么一点点的性能……
        if (!crossRowWhenStaggeredGridLayout) {
            return
        }
        val layoutParams =
            itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams
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
    protected open fun onViewAttachedToWindow(holder: VH) {

    }

    /**
     * [holder] item view 从窗口上解绑时回调
     */
    protected open fun onViewDetachedFromWindow(holder: VH) {

    }

    /**
     * 当使用了 [androidx.recyclerview.widget.DiffUtil] 工具类时，可能需要用到此函数
     */
    protected open fun onBindViewHolder(holder: VH, t: T, payloads: List<Any>) {

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
