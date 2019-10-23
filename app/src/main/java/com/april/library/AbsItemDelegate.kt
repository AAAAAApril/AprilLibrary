package com.april.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.april.multiple.ItemDelegate

abstract class AbsItemDelegate<T>(@LayoutRes private val itemLayoutRes: Int) :
    ItemDelegate<T, RecyclerView.ViewHolder>() {
    override fun onCreateItemView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(itemLayoutRes, parent, false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, itemView: View): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(itemView) {}
    }

    abstract override fun onBindViewHolder(holder: RecyclerView.ViewHolder, bean: T)

}