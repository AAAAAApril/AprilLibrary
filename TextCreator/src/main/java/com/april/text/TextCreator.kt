package com.april.text

import android.graphics.Typeface
import android.text.Spanned
import android.view.View
import androidx.annotation.*
import androidx.annotation.IntRange

/**
 * 文字样式
 */
open class TextCreator(
    //文字
    val text: CharSequence,
    //文字颜色
    @ColorRes
    val textColor: Int? = null,
    //文字背景色
    @ColorRes
    val backgroundColor: Int? = null,
    //文字大小（单位：DP）
    @IntRange(from = 0)
    val textSizeDP: Int? = null,
    //文字大小缩放
    @FloatRange(from = 0.0)
    val textSizeScale: Float? = null,
    //文字样式
    @TextCreatorStyle
    val textStyle: Int? = null,
    //文字字体(内置字体，对英文生效)
    @TextCreatorType
    val textType: String? = null,
    //添加下划线
    val underLine: Boolean? = null,
    //添加删除线
    val deleteLine: Boolean? = null,
    //作为上标显示
    val asSuperscript: Boolean? = null,
    //作为下标显示
    val asSubscript: Boolean? = null,
    /**
     * 该位置上添加点击事件
     *
     * 注意：如果是给单独一行上的文字设置这个点击效果，那么，响应范围将是这一整行，而不仅仅是文字绘制区域
     */
    val onClick: ((View) -> Unit)? = null,
    //编辑框时，在该位置前面插入文字是否应用该样式（如有需要，可以选择公开此属性）
    private val includeStart: Boolean = false,
    //编辑框时，在该位置后面插入文字是否应用该样式（如有需要，可以选择公开此属性）
    private val includeEnd: Boolean = false
) {
    internal fun getSpanFlag(): Int {
        return if (includeStart) {
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
}


/**
 * 文字样式注解
 */
@IntDef(
    TextCreatorStyle.Normal,
    TextCreatorStyle.Bold,
    TextCreatorStyle.Italic,
    TextCreatorStyle.BoldItalic
)
@Retention(AnnotationRetention.SOURCE)
annotation class TextCreatorStyle {
    companion object {
        const val Normal = Typeface.NORMAL
        const val Bold = Typeface.BOLD
        const val Italic = Typeface.ITALIC
        const val BoldItalic = Typeface.BOLD_ITALIC
    }
}

/**
 * 文字字体注解
 */
@StringDef(
    TextCreatorType.Normal,
    TextCreatorType.Monospace,
    TextCreatorType.Serif,
    TextCreatorType.Sans
)
@Retention(AnnotationRetention.SOURCE)
annotation class TextCreatorType {
    companion object {
        const val Monospace = "monospace"
        const val Serif = "serif"
        const val Sans = "sans"
        const val Normal = "normal"
    }
}
