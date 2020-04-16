package com.april.develop.helper

import kotlinx.coroutines.delay
import java.util.*

/**
 * 计算出两个日期之间的差异
 */
class DateDiff(private val callBack: DateDiffCallBack) {
    companion object{
        fun getDay(milliTime: Long) = milliTime / 1000 / 60 / 60 / 60 % 24

        fun getHour(milliTime: Long) = milliTime / 1000 / 60 / 60 % 60

        fun getMinute(milliTime: Long) = milliTime / 1000 / 60 % 60

        fun getSecond(milliTime: Long) = milliTime / 1000 % 60

        fun getMilliSecond(milliTime: Long) = milliTime % 1000
    }

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
            val day = getDay(intervalTime)
            val hour = getHour(intervalTime)
            val minute = getMinute(intervalTime)
            val second = getSecond(intervalTime)
            val milliSecond = getMilliSecond(intervalTime)
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
