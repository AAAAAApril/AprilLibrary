package com.april.multiple

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * 内置的一个默认的 ItemDelegate
 */
abstract class DefaultItemDelegate<T>(@LayoutRes private val itemLayoutRes: Int) :
    ItemDelegate<T, RecyclerView.ViewHolder>() {

    override fun onCreateItemView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(itemLayoutRes, parent, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, itemView: View): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(itemView) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, bean: T) {
        onBindItemView(holder.itemView, bean, holder.adapterPosition)
    }

    protected open fun onBindItemView(itemView: View, bean: T, itemAdapterPosition: Int) {

    }

}