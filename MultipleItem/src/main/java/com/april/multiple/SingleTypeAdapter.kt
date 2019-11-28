package com.april.multiple

import com.april.multiple.loadmore.LoadMoreAdapter

/**
 * 单类型 Item Adapter
 *
 * 基于 [LoadMoreAdapter]
 *
 * 因此具有：占位视图、头尾布局、加载更多布局
 *
 * 并在此基础上约束为单一数据类型
 */
class SingleTypeAdapter<T : Any>(
    private val dataClass: Class<T>,
    itemDelegate: MultipleItemDelegate<T, *>
) : LoadMoreAdapter() {

    init {
        obtainManager().setItemDelegates(
            itemDelegate,
            recognizer = object : Recognizer<T> {}
        )
    }

    private fun only(itemDelegate: MultipleItemDelegate<T, *>) {
        //do nothing
    }

    private fun many(
        vararg delegate: MultipleItemDelegate<T, *>,
        recognizer: (
            //添加的同数据类型的所有 item 样式代理
            classes: Array<Class<out MultipleItemDelegate<out T, *>>>,
            //数据实例
            bean: T,
            //所在位置
            position: Int
        ) -> Class<out MultipleItemDelegate<out T, *>>
    ) {
        //do nothing
    }

    private fun obtainManager(): Manager<T> {
        return createManager(dataClass.hashCode(), support)
    }

}