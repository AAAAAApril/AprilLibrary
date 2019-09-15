package com.april.develop.helper

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * 和生命周期绑定的倒计时工具
 *
 * 这个功能暂时不写，更好的方式是用 LiveData 结合 Kotlin 协程做
 */
class CountDownHelper : Fragment() {
    companion object {
        private const val TAG = "CountDownHelperFragment"
        private const val WHAT = 0X09

        fun getInstance(manager: FragmentManager): CountDownHelper {
            var helper: CountDownHelper? = manager.findFragmentByTag(TAG) as? CountDownHelper
            if (helper == null) {
                helper = CountDownHelper()
                manager.beginTransaction()
                    .add(helper, TAG)
                    .commit()
                manager.executePendingTransactions()
            }
            return helper
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    //倒计时总时间（默认 60 秒）
    private var countDownTotalSeconds: Int = 60
    //倒计时间隔（单位：毫秒）
    private val interval: Long = 1000
    //正在倒计时的时间（从 0 递增，最大值为 countDownTotalSeconds）
    private var runningSeconds: Int = 0
    //是否正在倒计时
    private var isRunning: Boolean = false

    fun startCountDown(totalSeconds: Int = countDownTotalSeconds) {}

}