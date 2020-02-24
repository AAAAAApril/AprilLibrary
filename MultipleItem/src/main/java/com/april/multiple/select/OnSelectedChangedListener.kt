package com.april.multiple.select

/**
 * 选中变化监听
 */
interface OnSelectedChangedListener {
    /**
     * 选中变化
     *
     * [selectedPositionList] 所有被选中的位置列表
     */
    fun onSelectedChanged(selectedPositionList: List<Int>)
}