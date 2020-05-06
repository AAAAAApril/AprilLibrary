package com.april.multiple.diff

import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView

/**
 * 更新回调
 */
class MultipleUpdateCallBack(
    private val adapter: RecyclerView.Adapter<*>,
    private val headerCount: Int = 0
) : ListUpdateCallback {

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        adapter.notifyItemRangeChanged(position + headerCount, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(
            fromPosition + headerCount,
            toPosition + headerCount
        )
    }

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position + headerCount, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(position + headerCount, count)
    }

}