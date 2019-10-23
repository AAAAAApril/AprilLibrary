package com.april.develop.watcher

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/*

        listenTextViewChange()
            .withView(textView0, 9, 31)
            .withView(textView1, -1, 99)
            .withView(button, 8, -1)
            .listen { allWithinConstraints: Boolean ->

            }


        当一堆 TextView 的文字长度可以决定另一个 Button 是否可以操作的时候。这个功能会很好用。

 */

fun Fragment.listenTextViewChange(TAG: String = this.javaClass.name): TextLengthWatcher {
    return obtainWatcher(childFragmentManager, TAG)
}

fun FragmentActivity.listenTextViewChange(TAG: String = this.javaClass.name): TextLengthWatcher {
    return obtainWatcher(supportFragmentManager, TAG)
}

private fun obtainWatcher(manager: FragmentManager, TAG: String): TextLengthWatcher {
    var watcher = (manager.findFragmentByTag(TAG) as? TextLengthWatcher)
    if (watcher == null) {
        watcher = TextLengthWatcher()
        manager.beginTransaction()
            .add(watcher, TAG)
            .commitAllowingStateLoss()
        manager.executePendingTransactions()
    }
    return watcher
}

/**
 * 对 TextView 极其子类的文字长度进行监听
 */
class TextLengthWatcher : Fragment(), TextChange {

    init {
        retainInstance = true
    }

    //被监听的控件
    private val watcherSet = mutableSetOf<Watcher>()
    //监听回调
    private var textChange: ((allWithinConstraints: Boolean) -> Unit)? = null

    /**
     * [minCount] 约束的最小字符长度（包含，默认 -1，表示不限制）
     * [maxCount] 约束的最大字符长度（包含，默认 -1，表示不限制）
     */
    fun withView(
        view: TextView,
        minCount: Int = -1,
        maxCount: Int = -1
    ): TextLengthWatcher {
        watcherSet.add(
            Watcher(
                view, minCount, maxCount, this
            )
        )
        return this
    }

    /**
     * [block] 监听回调
     * [Boolean] 是否全部被监听的文字都满足了设置的字符数条件
     */
    fun listen(block: ((allWithinConstraints: Boolean) -> Unit)) {
        textChange = block
        //手动检测一次
        textChange?.apply {
            var within = true
            for (watch in watcherSet) {
                if (!watch.check()) {
                    within = false
                    break
                }
            }
            this.invoke(within)
        }
    }

    override fun onTextChanged(withinConstraints: Boolean) {
        textChange?.apply {
            if (!withinConstraints) {
                this.invoke(false)
            } else {
                var within = true
                for (watcher in watcherSet) {
                    if (!watcher.withinConstraints) {
                        within = false
                        break
                    }
                }
                this.invoke(within)
            }
        }
    }

}

private class Watcher(
    private val view: TextView,//被监听的文字控件或者其子类
    private val minCount: Int = -1,//约束的最小字符长度（包含，默认 -1，表示不限制）
    private val maxCount: Int = -1,//约束的最大字符长度（包含，默认 -1，表示不限制）
    private val textChange: TextChange
) : TextWatcher {

    init {
        view.addTextChangedListener(this)
    }

    //是否在约束条件内
    internal var withinConstraints: Boolean = false

    override fun afterTextChanged(s: Editable?) {
        textChange.onTextChanged(check())
    }

    internal fun check(): Boolean {
        val length = view.text.length
        //不限制最小
        if (minCount < 0) {
            //判断最大
            withinConstraints = (length <= maxCount)
            return withinConstraints
        }
        //不限制最大
        if (maxCount < 0) {
            //判断最小
            withinConstraints = (length >= minCount)
            return withinConstraints
        }
        //都限制
        withinConstraints = (length in minCount..maxCount)
        return withinConstraints
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}

private interface TextChange {

    /**
     * [withinConstraints] 是否是在约束内，在里面表示符合条件，否则为不符合
     */
    fun onTextChanged(withinConstraints: Boolean)
}