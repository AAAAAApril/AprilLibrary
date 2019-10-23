package com.april.multiple

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

fun MultipleAdapter.getData(position: Int): Any {
    return support.dataList[position]
}

fun MultipleAdapter.addData(any: Any) {
    support.dataList.add(any)
    notifyItemInserted(
        support.dataList.lastIndex + support.headerCount()
    )
}

fun MultipleAdapter.insertData(any: Any, position: Int) {
    support.dataList.add(position, any)
    notifyItemInserted(
        position + support.headerCount()
    )
}

fun MultipleAdapter.removeData(position: Int) {
    support.dataList.removeAt(position)
    notifyItemRemoved(
        position + support.headerCount()
    )
}

fun MultipleAdapter.removeLastData() {
    val index = support.dataList.lastIndex
    support.dataList.removeAt(index)
    notifyItemRemoved(
        index + support.headerCount()
    )
}

fun MultipleAdapter.getDataList(): MutableList<Any> {
    return support.dataList
}

fun MultipleAdapter.addDataList(dataList: MutableList<Any>) {
    if (dataList.isEmpty()) {
        return
    }
    val index = dataList.size
    support.dataList.addAll(dataList)
    notifyItemRangeInserted(
        index + support.headerCount(),
        dataList.size
    )
}

fun MultipleAdapter.resetDataList(dataList: MutableList<Any>) {
    support.dataList.clear()
    support.dataList.addAll(dataList)
    notifyDataSetChanged()
}

fun MultipleAdapter.clearDataList() {
    support.dataList.clear()
    notifyDataSetChanged()
}

//==================================================================================================

/**
 * 从布局资源文件添加
 *
 * @return 创建出的占位布局
 */
fun MultipleAdapter.setPlaceholder(
    targetRecyclerView: RecyclerView,
    @LayoutRes placeholderViewResId: Int
): View {
    val placeholderView = LayoutInflater.from(targetRecyclerView.context).inflate(
        placeholderViewResId, targetRecyclerView, false
    )
    support.placeholderView = placeholderView
    support.placeholderViewType = placeholderView.javaClass.hashCode()
    return placeholderView
}

/**
 * 添加 header
 *
 * @param recyclerView 目标 RecyclerView
 * @return headerView
 */
fun MultipleAdapter.addHeader(
    recyclerView: RecyclerView,
    @LayoutRes headerLayoutRes: Int
): View {
    val headerView = LayoutInflater.from(recyclerView.context)
        .inflate(headerLayoutRes, recyclerView, false)
    support.addHeader(headerView)
    notifyItemInserted(support.headerArray.indexOfValue(headerView))
    return headerView
}

/**
 * 移除 HeaderView
 */
fun MultipleAdapter.removeHeader(headerView: View) {
    val index = support.headerArray.indexOfValue(headerView)
    support.headerArray.removeAt(index)
    notifyItemRemoved(index)
}

/**
 * 添加 footer
 *
 * @param recyclerView 目标 RecyclerView
 * @return footerView
 */
fun MultipleAdapter.addFooter(
    recyclerView: RecyclerView,
    @LayoutRes footerLayoutRes: Int
): View {
    val footerView = LayoutInflater.from(recyclerView.context)
        .inflate(footerLayoutRes, recyclerView, false)
    support.addFooter(footerView)
    notifyItemInserted(support.adapterPositionOfFooter(footerView))
    return footerView
}

/**
 * 移除 FooterView
 */
fun MultipleAdapter.removeFooter(footerView: View) {
    val index = support.adapterPositionOfFooter(footerView)
    support.footerArray.removeAt(support.footerArray.indexOfValue(footerView))
    notifyItemRemoved(index)
}

/**
 * 一对一样式
 *
 * 一个数据实体类型对应 一个 item 样式代理
 */
inline fun <reified T> MultipleAdapter.only(itemDelegate: ItemDelegate<T, *>) {
    createManager<T>(T::class.java.hashCode(), support).setItemDelegates(
        Array(1) { itemDelegate },
        object : Recognizer<T> {
            override fun recognize(
                classes: Array<Class<out ItemDelegate<out T, *>>>,
                t: T,
                position: Int
            ): Class<out ItemDelegate<out T, *>> {
                return classes.first()
            }
        })
}

/**
 * 一对多样式
 *
 * 一个数据实体类型对应 多个 item 样式代理
 */
inline fun <reified T> MultipleAdapter.many(
    vararg delegate: ItemDelegate<T, *>,
    crossinline recognizer: (
        //添加的同数据类型的所有 item 样式代理
        classes: Array<Class<out ItemDelegate<out T, *>>>,
        //数据实例
        t: T,
        //所在位置
        position: Int
    ) -> Class<out ItemDelegate<out T, *>>
) {
    createManager<T>(T::class.java.hashCode(), support).setItemDelegates(
        Array(delegate.size) { index -> delegate[index] },
        object : Recognizer<T> {
            override fun recognize(
                classes: Array<Class<out ItemDelegate<out T, *>>>,
                t: T,
                position: Int
            ): Class<out ItemDelegate<out T, *>> {
                return recognizer.invoke(classes, t, position)
            }
        })
}

/**
 * 获取 Manager
 *
 * 注意：拿到 Manager 之后，只有调用了 setItemDelegates 函数，才可以在 MultipleAdapter 里面使用到
 */
inline fun <reified T> MultipleAdapter.obtainManager(): Manager<T> {
    return createManager(T::class.java.hashCode(), support)
}

//==================================================================================================

/**
 * 获取最小公倍数
 */
fun getLeastCommonMultiple(int: IntArray): Int {
    var max = 0
    int.forEach {
        if (it > max) {
            max = it
        }
    }
    var s = 1
    var i = 2
    while (i <= max) {
        var b = false
        for (j in int.indices) {
            if (int[j] % i == 0) {
                int[j] = int[j] / i
                b = true
            }
        }
        if (b) {
            s *= i
            i--
        }
        i++
    }
    int.forEach {
        s *= it
    }
    return s
}