package com.april.text

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.widget.TextView
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat

/*

        tv?.multiText(
            TextCreator("hello world !", textColor = R.color.colorAccent),
            NewLine(),
            TextCreator("hello world !", backgroundColor = R.color.colorPrimary),
            NewLine(),
            TextCreator("hello ", textSizeDP = 18),
            ImageCreator(
                R.drawable.ic_launcher_foreground,
                vertical = true,
                onClick = {
                    toast("picture clicked !")
                }),
            TextCreator(" world !", textSizeDP = 18),
            NewLine(),
            TextCreator("hello world !",
                textType = TextCreatorType.Serif,
                highLightColor = R.color.colorAccent,
                onClick = {
                    toast("hello world !")
                }
            )
        )

 */

/**
 * TextView 设置富文本样式
 */
fun <T : TextCreator> TextView.multiText(vararg creators: T) {
    val builder = SpannableStringBuilder()
    creators.forEach {
        val start = builder.length
        builder.append(it.text)
        when (it) {
            //换行
            is NewLine -> {

            }
            //添加图片
            is ImageCreator -> {
                //添加点击事件
                it.onClick?.let { click ->
                    builder.setSpan(
                        OnClickSpan(
                            this,
                            it.highLightColor,
                            it.underLine ?: false,
                            click
                        ),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }

                builder.setSpan(
                    ImageStyleSpan(context, it.image, it.vertical),
                    start,
                    builder.length,
                    it.getSpanFlag()
                )
            }
            //其他
            else -> {
                //文字颜色
                it.textColor?.run {
                    builder.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(context, this)),
                        start,
                        builder.length,
                        it.getSpanFlag()
                    )
                }

                //文字背景色
                it.backgroundColor?.run {
                    builder.setSpan(
                        BackgroundColorSpan(ContextCompat.getColor(context, this)),
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

                //添加点击事件
                it.onClick?.let { click ->
                    builder.setSpan(
                        OnClickSpan(
                            this,
                            it.highLightColor,
                            it.underLine ?: false,
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
    text = builder
}

/**
 * 文字样式
 */
open class TextCreator(
    //文字
    val text: CharSequence,
    //文字颜色
    @ColorRes
    val textColor: Int? = null,
    //TextView 被点击时，响应范围内的高亮背景色。
    //这个值对整个 TextView 生效。
    //如果设置为 null，则默认颜色为全透明。
    @ColorRes
    val highLightColor: Int? = null,
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
    //该位置上添加点击事件
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
 * 图片样式
 */
class ImageCreator(
    //图片资源文件
    @DrawableRes
    val image: Int,
    //图片是否垂直居中于文字，否则为底部对齐
    val vertical: Boolean = false,
    //点击事件
    onClick: ((View) -> Unit)? = null
//图片样式为占位类型，所以需要给个字符用来占位替换，这里用了空白字符 " "
) : TextCreator(text = " ", onClick = onClick)

/**
 * 换行
 */
class NewLine : TextCreator(text = "\n")

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

/**
 * 自定义点击
 */
private class OnClickSpan(
    textView: TextView,
    @ColorRes
    val highLightColor: Int?,
    val showUnderLine: Boolean,
    val onClick: ((View) -> Unit)?
) : ClickableSpan() {

    init {
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = if (highLightColor == null) {
            0X00000000
        } else {
            ContextCompat.getColor(textView.context, highLightColor)
        }
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = showUnderLine
    }

    override fun onClick(widget: View) {
        onClick?.invoke(widget)
    }
}

/**
 * 图片样式处理
 *
 * 主要处理图片显示的位置
 */
private class ImageStyleSpan(
    context: Context,
    @DrawableRes image: Int,
    val vertical: Boolean
) : ImageSpan(context, image) {
    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        if (vertical) {
            val rect = drawable.bounds
            fm?.run {
                val fmPaint = paint.fontMetricsInt
                val fontHeight = fmPaint.bottom - fmPaint.top
                val drHeight = rect.bottom - rect.top

                val top = drHeight / 2 - fontHeight / 4
                val bottom = drHeight / 2 + fontHeight / 4

                ascent = -bottom
                this.top = -bottom
                this.bottom = top
                descent = top
            }
            return rect.right
        } else {
            return super.getSize(paint, text, start, end, fm)
        }
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        if (vertical) {
            val drawable = drawable
            canvas.save()
            canvas.translate(x, ((bottom - top - drawable.bounds.bottom) / 2 + top).toFloat())
            drawable.draw(canvas)
            canvas.restore()
        } else {
            super.draw(canvas, text, start, end, x, top, y, bottom, paint)
        }
    }
}