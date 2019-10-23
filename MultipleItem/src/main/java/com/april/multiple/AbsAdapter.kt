package com.april.multiple

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class AbsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    abstract val support: MultipleSupport

    override fun getItemCount(): Int {
        return support.getItemCount()
    }

    override fun getItemViewType(position: Int): Int {
        return support.getItemViewType(position)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        support.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        support.onViewDetachedFromWindow(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return support.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        support.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

}