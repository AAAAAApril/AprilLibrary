package com.april.multiple

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

open class MultipleAdapter(override val support: MultipleSupport = MultipleSupport())
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(), IMultipleSupport {

    override fun getItemViewType(position: Int): Int {
        return super<IMultipleSupport>.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super<RecyclerView.Adapter>.onAttachedToRecyclerView(recyclerView)
        super<IMultipleSupport>.onAttachedToRecyclerView(recyclerView)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super<RecyclerView.Adapter>.onViewAttachedToWindow(holder)
        super<IMultipleSupport>.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super<RecyclerView.Adapter>.onViewDetachedFromWindow(holder)
        super<IMultipleSupport>.onViewDetachedFromWindow(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        super<IMultipleSupport>.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super<IMultipleSupport>.onBindViewHolder(holder, position)
    }

}

abstract class AbsItemDelegate<T>(@LayoutRes private val itemLayoutResId: Int = 0)
    : ItemDelegate<T, DefaultViewHolder>() {

    @LayoutRes
    protected fun setItemLayoutRes(): Int {
        return itemLayoutResId
    }

    override fun onCreateItemView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context)
            .inflate(setItemLayoutRes(), parent, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, itemView: View): DefaultViewHolder {
        return DefaultViewHolder(itemView)
    }

    abstract override fun onBindViewHolder(holder: DefaultViewHolder, t: T)
}

//val RecyclerView.multipleAdapter: MultipleAdapter
//    get() {
//        var adapter = getTag(MultipleAdapter::class.java.hashCode()) as? MultipleAdapter
//        if (adapter == null) {
//            adapter = MultipleAdapter()
//            setAdapter(adapter)
//            setTag(MultipleAdapter::class.java.hashCode(), adapter)
//        }
//        return adapter
//    }
