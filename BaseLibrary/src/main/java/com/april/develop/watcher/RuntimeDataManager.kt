package com.april.develop.watcher

import android.util.SparseArray

/**
 * 运行时暂存数据观察者管理器
 */
object RuntimeDataManager {

    //监听器包装  以数据类型实体 Class 对象的 hashCode 为 key
    private val wrapperArray = SparseArray<LinkedHashSet<RuntimeDataListener<out Any>?>?>()
    //旧数据集  以数据类型实体 Class 对象的 hashCode 为 key
    private val oldDataArray = SparseArray<Any?>()
    //数据比较器  以数据类型实体 Class 对象的 hashCode 为 key
    private val comparatorArray = SparseArray<RuntimeDataComparator<out Any>>()

    /**
     * 监听数据
     *
     * [notifyExistsData] 是否在设置监听的时候把旧数据回调给接口（此时接口接收到的新旧数据完全相同）
     */
    fun <T : Any> listen(clazz: Class<T>,
                         notifyExistsData: Boolean = false,
                         listener: RuntimeDataListener<T>) {
        val code = clazz.hashCode()
        val index = wrapperArray.indexOfKey(code)
        wrapperArray.put(code, if (index < 0) {
            LinkedHashSet()
        } else {
            wrapperArray.valueAt(index)
        }?.apply {
            add(listener)
            if (notifyExistsData) {
                (oldDataArray.get(code) as? T).let {
                    listener.onDataNotify(it, it)
                }
            }
        })
    }

    /**
     * 停止监听
     */
    fun <T : Any> stopListen(clazz: Class<T>,
                             listener: RuntimeDataListener<T>) {
        wrapperArray.get(clazz.hashCode())?.remove(listener)
    }

    /**
     * 设置比较器
     */
    fun <T : Any> compare(clazz: Class<T>,
                          comparator: RuntimeDataComparator<T>) {
        comparatorArray.put(clazz.hashCode(), comparator)
    }

    /**
     * 获取已经存在的旧数据
     */
    fun <T : Any> getExistData(clazz: Class<T>): T? {
        return oldDataArray.get(clazz.hashCode()) as T?
    }

    /**
     * 更新数据
     */
    fun <T : Any> notifyData(newData: T) {
        notifyData(newData::class.java.hashCode(), newData)
    }

    /**
     * 更新数据为 null
     */
    fun <T : Any> notifyData2Null(clazz: Class<T>) {
        notifyData(clazz.hashCode(), null)
    }

    private fun <T : Any> notifyData(key: Int, newData: T?) {
        (oldDataArray.get(key) as? T).let { oldData ->
            (comparatorArray.get(key) as? RuntimeDataComparator<T>).let { comparator ->
                if (if (comparator == null) {
                            oldData != newData
                        } else {
                            !comparator.compare(newData, oldData)
                        }
                ) {
                    (wrapperArray.get(key) as? LinkedHashSet<RuntimeDataListener<T>?>)?.map { listener ->
                        listener?.apply {
                            onDataNotify(newData, oldData)
                        }
                    }
                }

            }
        }
        oldDataArray.put(key, newData)
    }

}

/**
 * 数据监听
 */
interface RuntimeDataListener<T> {

    /**
     * 通知数据更新了
     */
    fun onDataNotify(new: T?, old: T?)
}

/**
 * 数据比较器
 */
interface RuntimeDataComparator<T> {

    /**
     * 对比新旧两个数据
     *
     * [Boolean] 新旧两个数据是否相同
     */
    fun compare(new: T?, old: T?): Boolean
}

//==================================================================================================

/**
 * 设置比较器
 */
inline fun <reified T : Any> RuntimeDataManager.compare(crossinline compare: (new: T?, old: T?) -> Boolean) {
    compare(T::class.java, object : RuntimeDataComparator<T> {
        override fun compare(new: T?, old: T?): Boolean {
            return compare(new, old)
        }
    })
}

/**
 * 设置监听
 */
inline fun <reified T : Any> RuntimeDataManager.listen(notifyExistsData: Boolean = false,
                                                       listener: RuntimeDataListener<T>) {
    listen(T::class.java, notifyExistsData, listener)
}

/**
 * 取消监听
 */

inline fun <reified T : Any> RuntimeDataManager.stopListen(listener: RuntimeDataListener<T>) {
    stopListen(T::class.java, listener)
}

//
//class DataListenerImpl<T : Any> private constructor(lifecycleOwner: LifecycleOwner) : LifecycleObserver {
//
//    init {
//        lifecycleOwner.lifecycle.addObserver(this)
//    }
//
//    companion object {
//        fun <T : Any> get(lifecycleOwner: LifecycleOwner): DataListenerImpl<T> {
//            return DataListenerImpl(lifecycleOwner)
//        }
//    }
//
//    private var clazz: Class<T>? = null
//    private var listener: DataListener<T>? = null
//
//    fun listen(clazz: Class<T>,
//               notifyExistsData: Boolean = false,
//               listener: DataListener<T>) {
//        this.clazz = clazz
//        this.listener = listener
//        RuntimeDataManager.listen(
//                clazz = clazz,
//                notifyExistsData = notifyExistsData,
//                listener = listener
//        )
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun stopListen() {
//        clazz?.let {
//            RuntimeDataManager.stopListen(it, this.listener!!)
//        }
//    }
//
//}

//inline fun <reified T : Any> DataListenerImpl<T>.listen(notifyExistsData: Boolean = false,
//                                                        crossinline listener: ((new: T?, old: T?) -> Unit)) {
//    listen(
//            T::class.java,
//            notifyExistsData = notifyExistsData,
//            listener = object : RuntimeDataListener<T> {
//                override fun onDataNotify(new: T?, old: T?) {
//                    listener.invoke(new, old)
//                }
//            }
//    )
//}