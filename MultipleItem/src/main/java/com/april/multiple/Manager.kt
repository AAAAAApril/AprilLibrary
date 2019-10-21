package com.april.multiple

import android.util.SparseArray

fun <T> createManager(
    itemBeanClassHashCode: Int,
    support: MultipleSupport
): Manager<T> {
    return Manager(itemBeanClassHashCode, support)
}

/**
 * item 样式代理 管理器
 *
 * [T] 绑定的数据类型
 *
 * [itemBeanClassHashCode] [T]对应 Class 的 hash code
 */
class Manager<T> internal constructor(
    private val itemBeanClassHashCode: Int,
    private val support: MultipleSupport
) {

    private val delegateClassArray: SparseArray<Class<out ItemDelegate<out T, *>>> = SparseArray()
    private lateinit var delegateClasses: Array<Class<out ItemDelegate<out T, *>>>
    // item 样式识别器
    private lateinit var recognizer: Recognizer<T>

    /**
     * 获取该位置的 item 类型
     *
     * [any] 数据实例
     * [position] 所在列表中的位置
     *
     * @return ItemViewType
     */
    internal fun getItemViewType(any: Any, position: Int): Int {
        //多这个判断也许能稍微快一点点？？？
        return if (delegateClassArray.size() == 1) {
            delegateClassArray.keyAt(0)
        } else {
            delegateClassArray.keyAt(
                delegateClassArray.indexOfValue(
                    recognizer.recognize(delegateClasses, any as T, position)
                )
            )
        }
    }

    //========================================================================================

    /**
     * 设置 样式代理
     *
     * [itemDelegate] 多个 item 样式代理实例
     * [recognizer] 识别器
     */
    fun setItemDelegates(
        itemDelegate: Array<ItemDelegate<out T, *>>,
        recognizer: Recognizer<T>
    ) {
        this.recognizer = recognizer
        this.delegateClasses = Array(itemDelegate.size) { index ->
            itemDelegate[index].javaClass
        }
        for (index in itemDelegate.indices) {
            val key = itemBeanClassHashCode + delegateClassArray.size()
            delegateClassArray.put(key, itemDelegate[index].javaClass)
            support.itemDelegateArray.put(key, itemDelegate[index])
        }
        support.managerArray.put(itemBeanClassHashCode, this)
    }

}

/**
 * item 样式识别器
 */
interface Recognizer<T> {
    fun recognize(
        //添加的同数据类型的所有 item 样式代理
        classes: Array<Class<out ItemDelegate<out T, *>>>,
        //数据实例
        t: T,
        //所在位置
        position: Int
    ):
    //根据数据或者 item 位置识别出的 item 代理类的 class
            Class<out ItemDelegate<out T, *>>
}
