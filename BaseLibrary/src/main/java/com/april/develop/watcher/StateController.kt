package com.april.develop.watcher

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * 对 TextView 极其子类的文字进行监听
 */
class TextStateWatcher : Fragment(), TextChange {

    companion object {
        fun newInstance(manager: FragmentManager, TAG: String = "StateControllerTAG"): TextStateWatcher {
            var controller = manager.findFragmentByTag(TAG) as? TextStateWatcher
            if (controller == null) {
                controller = TextStateWatcher()
                manager.beginTransaction()
                    .add(controller, TAG)
                    .commitAllowingStateLoss()
                manager.executePendingTransactions()
            }
            return controller
        }
    }

    //被监听的控件
    private val watcherSet = mutableSetOf<Watcher>()
    //监听回调
    private var textChange: ((allWithinConstraints: Boolean) -> Unit)? = null

    /**
     * [minCount] 约束的最小字符长度（包含，默认 -1，表示不限制）
     * [maxCount] 约束的最大字符长度（包含，默认 -1，表示不限制）
     */
    fun addView(view: TextView,
                minCount: Int = -1,
                maxCount: Int = -1): TextStateWatcher {
        watcherSet.add(Watcher(
            view, minCount, maxCount, this
        ))
        return this
    }

    /**
     * [block] 监听回调
     */
    fun watch(block: ((allWithinConstraints: Boolean) -> Unit)) {
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

private class Watcher(private val view: TextView,//被监听的文字控件或者其子类
                      private val minCount: Int = -1,//约束的最小字符长度（包含，默认 -1，表示不限制）
                      private val maxCount: Int = -1,//约束的最大字符长度（包含，默认 -1，表示不限制）
                      private val textChange: TextChange) : TextWatcher {
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