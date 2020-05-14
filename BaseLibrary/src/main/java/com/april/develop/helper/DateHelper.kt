package com.april.develop.helper

import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间相关格式化
 *
 * 参考文章：
 * http://c.biancheng.net/view/878.html
 *
 *  Tips：
 *
 *  某些时候，我们会把时间格式化为比较复杂的字符串，比如： on Monday,May 23, at 9:50
 *  其中 at 包含了格式化保留字符 a (表示上午下午)，从而导致在格式化的时候无法识别和它之后的 t ，
 *  所以要想正确显示 at 这个单词，需要用 '' 单引号将其包裹，例如：'at'
 *
 */
object DateHelper {

    //年份，比如 2019
    //短的，2019 => 19
    const val YEAR = "yyyy"
    const val YEAR_SHORT = "yy"

    //月份 数字，比如 01、12
    //短的，09 => 9，11 => 11
    const val MONTH_NUMBER = "MM"
    const val MONTH_NUMBER_SHORT = "M"

    //月份 文字，比如 中：四月，英：April
    //短的，中文：四月 => 4月，英文：April => Apr
    const val MONTH_TEXT = "MMMM"
    const val MONTH_TEXT_SHORT = "MMM"

    //天数，在月份中。比如：29
    //短的，08 => 8，17 => 17
    const val DAY_IN_MONTH = "dd"
    const val DAY_IN_MONTH_SHORT = "d"

    //天数，在年中。比如：132
    const val DAY_IN_YEAR = "D"

    //星期，比如 中：星期一，英：Monday
    //短的，中文：星期一 => 周一，英文：Monday => Mon
    const val WEEK_DAY = "EEEE"
    const val WEEK_DAY_SHORT = "E"

    //一个月中的第几个星期
    const val WEEK_IN_MONTH = "W"

    //一年中的第几个星期
    const val WEEK_IN_YEAR = "w"

    //24小时制，从 0 开始
    const val HOUR_IN_DAY_24_0 = "HH"
    const val HOUR_IN_DAY_24_0_SHORT = "H"

    //12小时制，从 0 开始
    const val HOUR_IN_DAY_12_0 = "KK"
    const val HOUR_IN_DAY_12_0_SHORT = "K"

    //12小时制，从 1 开始
    const val HOUR_IN_DAY_12_1 = "hh"
    const val HOUR_IN_DAY_12_1_SHORT = "h"

    //24小时制，从 1 开始
    const val HOUR_IN_DAY_24_1 = "kk"
    const val HOUR_IN_DAY_24_1_SHORT = "k"

    //一天中的时间段标记，中文：上午、下午，英文：AM、PM
    const val AM_PM = "a"

    //分钟
    const val MINUTE = "mm"
    const val MINUTE_SHORT = "m"

    //秒
    const val SECOND = "ss"
    const val SECOND_SHORT = "s"

    //毫秒
    const val MILLI_SECOND = "SSS"

    /**
     * 默认时间格式样式：2019-12-03 18:32:55
     */
    const val FORMAT_STRING_STYLE_DEFAULT_LONG =
        "${YEAR}-${MONTH_NUMBER}-${DAY_IN_MONTH} ${HOUR_IN_DAY_24_0}:${MINUTE}:${SECOND}"

    /**
     * 只保留 年月日：2019-12-03
     */
    const val FORMAT_STRING_STYLE_DEFAULT_DAY = "${YEAR}-${MONTH_NUMBER}-${DAY_IN_MONTH}"

    /**
     * 只保留 时分秒：18:32:55
     */
    const val FORMAT_STRING_STYLE_DEFAULT_HOUR = "${HOUR_IN_DAY_24_0}:${MINUTE}:${SECOND}"

    /**
     * 创建 SimpleDateFormat
     *
     * @param formatStringStyle 格式化时间字符串的模板，
     * 可以使用 [DateType] 里面的值，
     * 参考 [FORMAT_STRING_STYLE_DEFAULT_LONG]
     * 例如：
     *
     *      val format:SimpleDateFormat = DateType.createSimpleDateFormat(
     *          FORMAT_STRING_STYLE_DEFAULT_LONG,
     *          Locale.CHINA
     *      )
     *
     */
    @JvmOverloads
    @JvmStatic
    fun createSimpleDateFormat(
        formatStringStyle: String,
        locale: Locale = Locale.getDefault()
    ): SimpleDateFormat = SimpleDateFormat(formatStringStyle, locale)

    /**
     * 根据开始日期和日期数量，获取对应的日期数据列
     */
    fun obtainDateListByCount(
        //开始的日期
        startDate: Date,
        //开始日期往后移动多少天（不包含开始日期）
        dateCount: Int
    ): List<Date> {
        val startCalender: Calendar = Calendar.getInstance().apply {
            //取当前时间进行赋值
            time = startDate
            //初始化时间到当前
            add(Calendar.DATE, 0)
        }
        val endCalender: Calendar = Calendar.getInstance().apply {
            //取当前时间进行赋值
            time = startDate
            //按当前时间进行推移，正数为向后移，负数为向前移
            add(Calendar.DATE, dateCount + 1)
        }
        val dateList = mutableListOf<Date>()
        while (startCalender.before(endCalender)) {
            dateList.add(startCalender.time)
            startCalender.add(Calendar.DATE, 1)
        }
        return dateList
    }

}
