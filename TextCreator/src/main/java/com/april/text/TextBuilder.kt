package com.april.text

import android.text.Spanned
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange

/**
 * 构建文字
 */
class TextBuilder internal constructor() {
    /**
     * 文字
     */
    var value: String = ""
    /**
     * 文字颜色
     */
    @ColorInt
    var textColor: Int? = null
    /**
     * 文字背景色
     */
    @ColorInt
    var textBackgroundColor: Int? = null
    /**
     * 文字大小：dp
     */
    @IntRange(from = 0)
    var textSize: Int? = null
    /**
     * 文字大小缩放
     */
    @FloatRange(from = 0.0)
    var textSizeScale: Float? = null
    /**
     * 文字样式
     */
    var textStyle: TextStyle? = null
    /**
     * 文字字体
     */
    var textType: TextType? = null
    /**
     * 添加下划线
     */
    var addUnderLine: Boolean = false
    /**
     * 添加删除线
     */
    var addDeleteLine: Boolean = false
    /**
     * 作为上标显示
     */
    var asSuperscript: Boolean = false
    /**
     * 作为下标显示
     */
    var asSubscript: Boolean = false
    /**
     * 该段文字的点击事件
     */
    var onClick: ((view: TextView, text: String) -> Unit)? = null
    /**
     * 编辑框时，在该位置前面插入文字是否应用该样式（如有需要，可以选择公开此属性）
     */
    private val includeStart: Boolean = false
    /**
     * 编辑框时，在该位置后面插入文字是否应用该样式（如有需要，可以选择公开此属性）
     */
    private val includeEnd: Boolean = false

    internal var spanFlag: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        private set
        get() = if (includeStart) {
            if (includeEnd) {
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            } else {
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            }
        } else {
            if (includeEnd) {
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            } else {
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            }
        }
}