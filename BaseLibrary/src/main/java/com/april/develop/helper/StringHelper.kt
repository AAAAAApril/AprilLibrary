package com.april.develop.helper

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * 32 位 MD5 加密
 */
fun String.md5_32(): String {
    try {
        val md = MessageDigest.getInstance("MD5")
        md.update(toByteArray())
        val b = md.digest()
        var i: Int
        val buf = StringBuffer("")
        for (offset in b.indices) {
            i = b[offset].toInt()
            if (i < 0) i += 256
            if (i < 16) buf.append("0")
            buf.append(Integer.toHexString(i))
        }
        return buf.toString()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}

/**
 * 身份证号码
 *
 * 1、号码的结构
 * 公民身份号码是特征组合码，由十七位数字本体码和一位校验码组成。
 *      排列顺序从左至右依次为：
 *          六位数字地址码，
 *          八位数字出生日期码，
 *          三位数字顺序码
 *          和一位数字校验码。
 *
 * 2、地址码（前六位数）
 * 表示编码对象常住户口所在县（市、旗、区）的行政区划代码，按 GB/T2260 的规定执行。
 *
 * 3、出生日期码（第七位至十四位）
 * 表示编码对象出生的年、月、日，按 GB/T7408 的规定执行，年、月、日代码之间不用分隔符。
 *
 * 4、顺序码（第十五位至十七位）
 * 表示在同一地址码所标识的区域范围内，对同年、同月、同日出生的人编定的顺序号， 顺序码的奇数分配给男性，偶数分配给女性。
 *
 * 5、校验码（第十八位数）
 * （1）十七位数字本体码加权求和公式
 *      S = Sum(Ai * Wi),
 *      i = 0, ... , 16 ，
 *      （先对前 17 位数字的权求和）
 *          Ai:表示第 i 位置上的身份证号码数字值
 *          Wi:表示第 i 位置上的加权因子
 *          Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
 * （2）计算模
 *      Y = mod(S, 11)
 * （3）通过模得到对应的校验码
 *      Y: 0 1 2 3 4 5 6 7 8 9 10
 *      校验码: 1 0 X 9 8 7 6 5 4 3 2
 */

/**
 * 判断是否是身份证号码（仅限18位身份证号）
 *
 * [needCheckFormat] 是否需要检测字符串格式是否属于身份证格式，默认不需要检测
 * （对于编辑框限定了输入格式的情况，就不需要检测，对于未知来源的字符串，则最好是检测一下）
 * [onError] 错误信息回调
 *
 */
fun String.isIDCardNum(
    needCheckFormat: Boolean = false,
    error: String? = null,//统一错误信息
    lengthError: String = "身份证号码长度应该为18位",
    contentError: String = "身份证号码除了最后一位，其余的都应该为数字",
    lastCharError: String = "身份证号码最后一位应该为数字或者大、小写的X",
    birthDayError: String = "身份证生日无效",
    birthDayInvalidError: String = "身份证生日不在有效范围内",
    birthDayMonthInvalidError: String = "身份证月份无效",
    birthDayDayInvalidError: String = "身份证日期无效",
    areaError: String = "身份证地区编码错误",
    idCardInvalidError: String = "身份证号不合法",
    onError: ((String) -> Unit)? = null
): Boolean {
    if (needCheckFormat) {
        //长度不满足18位
        if (length != 18) {
            onError?.invoke(error ?: lengthError)
            return false
        }
        val lastChar = last()
        //最后一位 纯数字和大小写 x 都不满足
        if ((lastChar.toInt() !in 0..9) && (lastChar != 'x' || lastChar != 'X')) {
            onError?.invoke(error ?: lastCharError)
            return false
        }
        //检测前17位
        substring(0, 17).map {
            //不是数字
            if (it.toInt() !in 0..9) {
                onError?.invoke(error ?: contentError)
                return false
            }
        }
    }

    //截取前面17位
    val ai = substring(0, 17)
    //年
    val year = ai.substring(6, 10)
    //月
    val month = ai.substring(10, 12)
    //日
    val day = ai.substring(12, 14)
    val birthDay = "$year-$month-$day"

    //检测生日
    if (!Pattern.compile(
            "^((\\d{2}(([02468][048])|([13579][26]))[\\-/\\s]?((((0?[13578])|(1[02]))[\\-/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-/\\s]?((((0?[13578])|(1[02]))[\\-/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3])):([0-5]?[0-9])((\\s)|(:([0-5]?[0-9])))))?\$"
        ).matcher(birthDay).matches()
    ) {
        onError?.invoke(error ?: birthDayError)
        return false
    }
    val calendar = GregorianCalendar()
    if ((calendar.get(Calendar.YEAR) - year.toInt()) > 150 ||
        (calendar.time.time - SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(birthDay).time) < 0
    ) {
        onError?.invoke(error ?: birthDayInvalidError)
        return false
    }
    if (month.toInt() !in 1..12) {
        onError?.invoke(error ?: birthDayMonthInvalidError)
        return false
    }
    if (day.toInt() !in 1..31) {
        onError?.invoke(error ?: birthDayDayInvalidError)
        return false
    }

    //检测地区编码
    if (getAreaCode()[ai.substring(0, 2)] == null) {
        onError?.invoke(error ?: areaError)
        return false
    }

    //检测最后一位
    val wi = arrayOf(
        "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"
    )
    var totalNum = 0
    for (index in 0 until 17) {
        totalNum += ai[index].toInt() * wi[index].toInt()
    }
    if ("$ai${arrayOf(
            "1",
            "0",
            "x",
            "9",
            "8",
            "7",
            "6",
            "5",
            "4",
            "3",
            "2"
        )[totalNum % 11]}" != toLowerCase()
    ) {
        onError?.invoke(error ?: idCardInvalidError)
        return false
    }
    return true
}

/**
 * [Hashtable] 获取地区编码
 */
private fun getAreaCode(): Hashtable<String, String> {
    return Hashtable<String, String>().apply {
        put("11", "北京")
        put("12", "天津")
        put("13", "河北")
        put("14", "山西")
        put("15", "内蒙古")
        put("21", "辽宁")
        put("22", "吉林")
        put("23", "黑龙江")
        put("31", "上海")
        put("32", "江苏")
        put("33", "浙江")
        put("34", "安徽")
        put("35", "福建")
        put("36", "江西")
        put("37", "山东")
        put("41", "河南")
        put("42", "湖北")
        put("43", "湖南")
        put("44", "广东")
        put("45", "广西")
        put("46", "海南")
        put("50", "重庆")
        put("51", "四川")
        put("52", "贵州")
        put("53", "云南")
        put("54", "西藏")
        put("61", "陕西")
        put("62", "甘肃")
        put("63", "青海")
        put("64", "宁夏")
        put("65", "新疆")
        put("71", "台湾")
        put("81", "香港")
        put("82", "澳门")
        put("91", "国外")
    }
}

/**
 * https://blog.csdn.net/luozhuang/article/details/47000715
 * https://blog.csdn.net/gaoqiao1988/article/details/77852715
 * https://jingzhongwen.iteye.com/blog/2209540?utm_source=tuicool&utm_medium=referral
 * https://blog.csdn.net/weianlai/article/details/82328996
 *
 *
 * 银行卡校验
 *
 * 1、从卡号最后一位数字开始，逆向将奇数位 ( 1、3、5 等等 ) 相加。
 * 2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以 2（如果乘积为两位数，则将其减去 9），再求和。
 * 3、将奇数位总和加上偶数位总和，结果应该可以被 10 整除。
 *
 * 例如，卡号是：5432123456788881
 * 奇数位和 = 35
 * 偶数位乘以 2（有些要减去 9 ）的结果：1 6 2 6 1 5 7 7，求和 = 35。
 * 最后 35 + 35 = 70 可以被 10 整除，认定校验通过。
 */
//fun String.isBankCard(needCheckFormat: Boolean = false,): Boolean {}

/**
 * 清除字符串中的空白
 */
fun CharSequence.clearBlank(): String {
    return this.toString().replace(" ", "")
}