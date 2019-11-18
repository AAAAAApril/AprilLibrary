package com.april.text

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


/*

        tv?.richText(
            TextCreator("hello world !", textColor = R.color.colorAccent),
            LineCreator(1),
            TextCreator("hello world !", backgroundColor = R.color.colorPrimary),
            LineCreator(),
            TextCreator("hello ", textSizeDP = 18),
            ImageCreator(
                R.drawable.ic_launcher_foreground,
                vertical = true,
                onClick = {
                    toast("picture clicked !")
                }),
            TextCreator(" world !", textSizeDP = 18),
            LineCreator(),
            TextCreator("hello world !",
                textType = TextCreatorType.Serif,
                highLightColor = R.color.colorAccent,
                onClick = {
                    toast("hello world !")
                }
            )
        )


        有时候会需要在同一个 TextView 内展示一些花里胡哨的东西，
        比如不一样的颜色的字，比如插入一个图标等

        注意：给某一段文字设置的点击事件会被 TextView 整个的点击事件覆盖掉！

 */

/**
 * TextView 设置富文本样式
 */
fun <T : TextCreator> TextView.richText(
    vararg creators: T,
    //是否是设置给 hint
    forHint: Boolean = false,
    //是否是增加模式
    appendMode: Boolean = false,
    //点击时的高亮背景
    @ColorRes highLightColor: Int = android.R.color.transparent
): TextView {
    var set = false
    val builder = context.richCharSequence(*creators) {
        if (this !is EditText) {
            if (!set) {
                set = true
                movementMethod = LinkMovementMethod.getInstance()
                highlightColor = ContextCompat.getColor(context, highLightColor)
            }
        }
    }
    if (forHint) {
        hint = builder
    } else {
        if (appendMode) {
            append(builder)
        } else {
            text = builder
        }
    }
    return this
}

/**
 * 构建富文本字符格式
 */
fun <T : TextCreator> Context.richCharSequence(
    vararg creators: T,
    hasClick: (() -> Unit)? = null
): CharSequence = SpannableStringBuilder().also { builder ->
    creators.forEach {
        val start = builder.length
        builder.append(it.text)
        when (it) {
            //换行
            is LineCreator -> {

            }
            //添加图片资源
            is ImageCreator -> {
                if (it.onClick != null) {
                    hasClick?.invoke()
                }
                //添加点击事件
                it.onClick?.let { click ->
                    builder.setSpan(
                        OnClickSpan(
                            it.underLine ?: false,
                            "",
                            click
                        ),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }

                when {
                    it.imageRes != null -> builder.setSpan(
                        ImageStyleSpan(this@richCharSequence, it.imageRes, it.centerVertical),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                    it.imageDrawable != null -> builder.setSpan(
                        ImageStyleSpan(it.imageDrawable, it.centerVertical),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                    else -> throw IllegalArgumentException("")
                }
            }
            //其他
            else -> {
                //文字颜色
                it.textColor?.run {
                    builder.setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                this@richCharSequence,
                                this
                            )
                        ),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }

                //文字背景色
                it.backgroundColor?.run {
                    builder.setSpan(
                        BackgroundColorSpan(
                            ContextCompat.getColor(
                                this@richCharSequence,
                                this
                            )
                        ),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }

                //文字大小
                it.textSizeDP?.run {
                    builder.setSpan(
                        AbsoluteSizeSpan(this, true),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }

                //文字大小缩放
                it.textSizeScale?.run {
                    builder.setSpan(
                        RelativeSizeSpan(this),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }

                //文字样式
                it.textStyle?.run {
                    builder.setSpan(
                        StyleSpan(this),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }

                //文字字体
                it.textType?.run {
                    builder.setSpan(
                        TypefaceSpan(this),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }

                //文字下划线
                it.underLine?.run {
                    builder.setSpan(
                        UnderlineSpan(),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }

                //文字删除线
                it.deleteLine?.run {
                    builder.setSpan(
                        StrikethroughSpan(),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }

                //文字作为上标
                it.asSuperscript?.run {
                    builder.setSpan(
                        SuperscriptSpan(),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }

                //文字作为下标
                it.asSubscript?.run {
                    builder.setSpan(
                        SubscriptSpan(),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }

                if (it.onClick != null) {
                    hasClick?.invoke()
                }
                //添加点击事件
                it.onClick?.let { click ->
                    builder.setSpan(
                        OnClickSpan(
                            it.underLine ?: false,
                            it.text,
                            click
                        ),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }
            }
        }
    }
}