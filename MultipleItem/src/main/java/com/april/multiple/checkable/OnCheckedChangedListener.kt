package com.april.multiple.checkable

/**
 * 选中变化监听
 */
interface OnCheckedChangedListener {
    /**
     * 选中变化
     *
     * [checkedPositionList] 所有被选中的位置列表
     */
    fun onCheckedChanged(checkedPositionList: List<Int>)
}