package com.april.multiple

/**
 * 一对一样式
 *
 * 一个数据实体类型对应 一个 item 样式代理
 */
inline fun <reified T : Any> MultipleAdapter.only(itemDelegate: MultipleItemDelegate<T, *>) {
    createManager<T>(T::class.java.hashCode(), support).setItemDelegates(
        itemDelegate,
        recognizer = object : Recognizer<T> {}
    )
}

/**
 * 一对多样式
 *
 * 一个数据实体类型对应 多个 item 样式代理
 */
inline fun <reified T : Any> MultipleAdapter.many(
    vararg delegate: MultipleItemDelegate<T, *>,
    crossinline recognizer: (
        //添加的同数据类型的所有 item 样式代理
        classes: Array<Class<out MultipleItemDelegate<out T, *>>>,
        //数据实例
        bean: T,
        //所在位置
        position: Int
    ) -> Class<out MultipleItemDelegate<out T, *>>
) {
    createManager<T>(T::class.java.hashCode(), support).setItemDelegates(
        *delegate,
        recognizer = object : Recognizer<T> {
            override fun recognize(
                classes: Array<Class<out MultipleItemDelegate<out T, *>>>,
                bean: T,
                position: Int
            ): Class<out MultipleItemDelegate<out T, *>> {
                return recognizer.invoke(classes, bean, position)
            }
        })
}

/**
 * 获取 Manager
 *
 * 注意：拿到 Manager 之后，只有调用了 setItemDelegates 函数，才可以在 MultipleAdapter 里面使用到
 */
inline fun <reified T : Any> MultipleAdapter.obtainManager(): Manager<T> {
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