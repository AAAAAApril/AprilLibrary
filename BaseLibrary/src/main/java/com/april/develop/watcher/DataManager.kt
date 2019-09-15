package com.april.develop.watcher

import android.util.SparseArray
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

class DataManager {

    companion object {
        val INSTANCE = DataManager()
    }

    //监听器包装  以数据类型实体 Class 对象的 hashCode 为 key
    private val wrapperArray: SparseArray<LinkedHashSet<DataListener<out Any>?>?> = SparseArray()
    //旧数据集  以数据类型实体 Class 对象的 hashCode 为 key
    private val oldDataArray: SparseArray<Any?> = SparseArray()
    //数据比较器  以数据类型实体 Class 对象的 hashCode 为 key
    private val comparatorArray: SparseArray<DataComparator<out Any>> = SparseArray()

    /**
     * 监听数据
     *
     * [notifyExistsData] 是否在设置监听的时候把旧数据回调给接口（此时接口接收到的新旧数据完全相同）
     */
    fun <T : Any> listen(clazz: Class<T>,
                         notifyExistsData: Boolean = false,
                         listener: DataListener<T>) {
        val code = clazz.hashCode()
        val index = wrapperArray.indexOfKey(code)
        wrapperArray.put(code, if (index < 0) {
            LinkedHashSet()
        } else {
            wrapperArray.valueAt(index)
        }?.apply {
            add(listener)
            if (notifyExistsData) {
                val old: T? = oldDataArray.get(code) as? T
                listener.onDataNotify(old, old)
            }
        })
    }

    /**
     * 停止监听
     */
    fun <T : Any> stopListen(clazz: Class<T>,
                             listener: DataListener<T>) {
        wrapperArray.get(clazz.hashCode())?.remove(listener)
    }

    /**
     * 设置比较器
     */
    fun <T : Any> compare(clazz: Class<T>,
                          comparator: DataComparator<T>) {
        comparatorArray.put(clazz.hashCode(), comparator)
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
    fun <T : Any> notifyData(clazz: Class<T>) {
        notifyData(clazz.hashCode(), null)
    }

    private fun <T : Any> notifyData(key: Int, newData: T?) {
        (oldDataArray.get(key) as? T).let { oldData ->
            (comparatorArray.get(key) as? DataComparator<T>).let { comparator ->
                if (if (comparator == null) {
                        oldData != newData
                    } else {
                        !comparator.compare(newData, oldData)
                    }
                ) {
                    (wrapperArray.get(key) as? LinkedHashSet<DataListener<T>?>)?.map { listener ->
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
interface DataListener<T> {

    /**
     * 通知数据更新了
     */
    fun onDataNotify(new: T?, old: T?)
}

/**
 * 数据比较器
 */
interface DataComparator<T> {

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
inline fun <reified T : Any> DataManager.compare(crossinline compare: (new: T?, old: T?) -> Boolean) {
    compare(T::class.java, object : DataComparator<T> {
        override fun compare(new: T?, old: T?): Boolean {
            return compare(new, old)
        }
    })
}

class DataListenerImpl<T : Any> private constructor(lifecycleOwner: LifecycleOwner) : LifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    companion object {
        fun <T : Any> get(lifecycleOwner: LifecycleOwner): DataListenerImpl<T> {
            return DataListenerImpl(lifecycleOwner)
        }
    }

    private var clazz: Class<T>? = null
    private var listener: DataListener<T>? = null

    fun listen(clazz: Class<T>,
               notifyExistsData: Boolean = false,
               listener: DataListener<T>) {
        this.clazz = clazz
        this.listener = listener
        DataManager.INSTANCE.listen(
            clazz = clazz,
            notifyExistsData = notifyExistsData,
            listener = listener
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stopListen() {
        clazz?.let {
            DataManager.INSTANCE.stopListen(it, this.listener!!)
        }
    }

}

inline fun <reified T : Any> DataListenerImpl<T>.listen(notifyExistsData: Boolean = false,
                                                        crossinline listener: ((new: T?, old: T?) -> Unit)) {
    listen(
        T::class.java,
        notifyExistsData = notifyExistsData,
        listener = object : DataListener<T> {
            override fun onDataNotify(new: T?, old: T?) {
                listener.invoke(new, old)
            }
        }
    )
}
