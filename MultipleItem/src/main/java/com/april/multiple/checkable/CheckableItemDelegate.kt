package com.april.multiple.checkable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.april.multiple.MultipleItemDelegate

/**
 * item delegate
 */
abstract class CheckableItemDelegate<T : Any>(
    @LayoutRes
    private val itemLayoutRes: Int
) : MultipleItemDelegate<CheckableDataWrapper<T>, RecyclerView.ViewHolder>() {

    final override fun onCreateItemView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(itemLayoutRes, parent, false)
    }

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        itemView: View
    ): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(itemView) {}
    }

    internal fun checkableItemClicked(
        holder: RecyclerView.ViewHolder,
        bean: CheckableDataWrapper<T>
    ): Boolean = onCheckableItemClicked(holder, bean.checked)

    /**
     * 当 item 被点击了
     *
     *  [oldCheckedStatus] 旧状态
     *
     * @return newCheckedStatus
     */
    protected open fun onCheckableItemClicked(
        holder: RecyclerView.ViewHolder,
        oldCheckedStatus: Boolean
    ): Boolean = !oldCheckedStatus

}