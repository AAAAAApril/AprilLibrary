package com.april.develop.helper

import kotlinx.coroutines.delay
import java.util.*

/**
 * 计算出两个日期之间的差异
 */
class DateDiff(private val callBack: DateDiffCallBack) {

    //停止标记
    var stopTag: Boolean = false

    /**
     * 比较
     */
    suspend fun compare(
        startDate: Date,
        endDate: Date
    ) {
        stopTag = true
        stopTag = false
        val startTime = startDate.time
        val endTime = endDate.time
        var intervalTime = endTime - startTime
        while (!stopTag) {
            intervalTime--
            if (intervalTime < 0) {
                stopTag = true
                callBack.onEndTimeBeforeStartTime()
                return
            }
            val day = intervalTime / 1000 / 60 / 60 / 60 % 24
            val hour = intervalTime / 1000 / 60 / 60 % 60
            val minute = intervalTime / 1000 / 60 % 60
            val second = intervalTime / 1000 % 60
            val milliSecond = intervalTime % 1000
            callBack.onDateDiff(
                day.toInt(),
                hour.toInt(),
                minute.toInt(),
                second.toInt(),
                milliSecond.toInt()
            )
            delay(1)
        }
    }

}

interface DateDiffCallBack {

    /**
     * 结束时间在开始时间之前了
     */
    fun onEndTimeBeforeStartTime()

    /**
     * 日期差异回调
     */
    fun onDateDiff(
        //天
        day: Int,
        //小时
        hour: Int,
        //分钟
        minute: Int,
        //秒
        second: Int,
        //毫秒
        milliSecond: Int
    )
}
