package com.april.text

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.*
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat

class SpannableTextCreator internal constructor(internal val context: Context) {
    internal val builder: SpannableStringBuilder by lazy {
        SpannableStringBuilder()
    }

    /**
     * 获取 SpannableStringBuilder
     */
    fun getSpannableStringBuilder(): SpannableStringBuilder = builder

    //是否设置了点击事件
    internal var hasClick = false

    /**
     * 是否设置了点击事件
     */
    fun hasSetClick(): Boolean = hasClick

    /**
     * 根据颜色资源获取颜色值
     */
    fun getColorInt(@ColorRes colorRes: Int): Int = ContextCompat.getColor(context, colorRes)

    /**
     * 根据 Drawable 资源获取 Drawable
     */
    fun getDrawable(@DrawableRes drawableRes: Int): Drawable? =
        ContextCompat.getDrawable(context, drawableRes)
}

/**
 * 文字
 */
fun SpannableTextCreator.text(value: String? = null, block: TextBuilder.() -> Unit) {
    TextBuilder().apply(block).let {
        //将传入的字符设置给构建器
        if (it.value.isEmpty()
            && value != null
        ) {
            it.value = value
        }
        val start = builder.length
        builder.append(it.value)
        val end = builder.length
        val spanFlag = it.spanFlag
        //文字颜色
        it.textColor?.let { color ->
            builder.setSpan(
                ForegroundColorSpan(color),
                start,
                end,
                spanFlag
            )
        }
        //文字背景色
        it.textBackgroundColor?.let { color ->
            builder.setSpan(
                BackgroundColorSpan(color),
                start,
                end,
                spanFlag
            )
        }
        //文字大小
        it.textSize?.let { size ->
            builder.setSpan(
                AbsoluteSizeSpan(size, true),
                start,
                end,
                spanFlag
            )
        }
        //文字大小缩放
        it.textSizeScale?.let { scale ->
            builder.setSpan(
                RelativeSizeSpan(scale),
                start,
                end,
                spanFlag
            )
        }
        //文字样式
        it.textStyle?.let { style ->
            builder.setSpan(
                StyleSpan(style.value),
                start,
                end,
                spanFlag
            )
        }
        //文字字体
        it.textType?.let { type ->
            builder.setSpan(
                TypefaceSpan(type.value),
                start,
                end,
                spanFlag
            )
        }
        //文字下划线
        if (it.addUnderLine) {
            builder.setSpan(
                UnderlineSpan(),
                start,
                end,
                spanFlag
            )
        }
        //文字删除线
        if (it.addDeleteLine) {
            builder.setSpan(
                StrikethroughSpan(),
                start,
                end,
                spanFlag
            )
        }
        //文字作为上标
        if (it.asSuperscript) {
            builder.setSpan(
                SuperscriptSpan(),
                start,
                end,
                spanFlag
            )
        }
        //文字作为下标
        if (it.asSubscript) {
            builder.setSpan(
                SubscriptSpan(),
                start,
                end,
                spanFlag
            )
        }
        //添加点击事件
        it.onClick?.let { click ->
            hasClick = true
            builder.setSpan(
                object : ClickableSpan() {
                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = it.addUnderLine
                        ds.isStrikeThruText = it.addDeleteLine
                    }

                    override fun onClick(widget: View) {
                        click.invoke(widget as TextView, it.value)
                    }
                },
                start,
                end,
                spanFlag
            )
        }
    }
}

/**
 * 资源图片
 */
fun SpannableTextCreator.image(@DrawableRes value: Int = 0, block: ImageBuilder.() -> Unit) {
    ImageBuilder().apply(block).let {
        //将设置的值赋值到构建器
        if (it.value == 0
            && value != 0
        ) {
            it.value = value
        }
        val start = builder.length
        builder.append(it.placementTextValue)
        val end = builder.length
        //资源
        builder.setSpan(
            ImageStyleSpan(context, it.value, it.centerVertical),
            start,
            end,
            it.spanFlag
        )
        // 给图片添加点击事件
        it.onClick?.let { onClick ->
            hasClick = true
            builder.setSpan(
                object : ClickableSpan() {
                    override fun updateDrawState(ds: TextPaint) {
                    }

                    override fun onClick(widget: View) {
                        onClick.invoke(widget as TextView, it.value)
                    }
                },
                start,
                end,
                it.spanFlag
            )
        }
    }
}

/**
 * Drawable 图片
 */
fun SpannableTextCreator.drawable(block: DrawableBuilder.() -> Unit) {
    DrawableBuilder().apply(block).let {
        if (it.value == null) {
            return@let
        }
        val start = builder.length
        builder.append(it.placementTextValue)
        val end = builder.length
        //drawable
        builder.setSpan(
            ImageStyleSpan(it.value!!, it.centerVertical),
            start,
            end,
            it.spanFlag
        )
        // 给图片添加点击事件
        it.onClick?.let { onClick ->
            hasClick = true
            builder.setSpan(
                object : ClickableSpan() {
                    override fun updateDrawState(ds: TextPaint) {
                    }

                    override fun onClick(widget: View) {
                        onClick.invoke(widget as TextView)
                    }
                },
                start,
                end,
                it.spanFlag
            )
        }
    }
}

/**
 * 行（hang）
 */
fun SpannableTextCreator.line(
    //表示空白行的数量（默认只是换行，不增加空白行）
    @IntRange(from = 0) blankLineCount: Int = 0
) {
    builder.append(StringBuilder().apply {
        for (i in 0 until (blankLineCount + 1)) {
            append("\n")
        }
    }.toString())
}

/**
 * 空白字符
 */
fun SpannableTextCreator.space(
    //空白字符的数量
    @IntRange(from = 1) spaceCount: Int = 1
) {
    builder.append(StringBuilder().apply {
        for (i in 0 until spaceCount) {
            append(" ")
        }
    }.toString())
}