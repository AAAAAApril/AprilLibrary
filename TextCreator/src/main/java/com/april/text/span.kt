package com.april.text

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.View
import androidx.annotation.DrawableRes


/**
 * 自定义点击
 */
internal class OnClickSpan(
    private val showUnderLine: Boolean,
    private val text: String = "",
    private val onClick: ((View, String) -> Unit)?
) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = showUnderLine
    }

    override fun onClick(widget: View) {
        onClick?.invoke(widget, text)
    }
}

/**
 * 图片样式处理
 *
 * 主要处理图片显示的位置
 */
internal class ImageStyleSpan : ImageSpan {

    private val centerVertical: Boolean

    constructor(drawable: Drawable, centerVertical: Boolean) : super(drawable.also {
        if (it.bounds.isEmpty) {
            it.setBounds(
                0, 0, it.intrinsicWidth,
                it.intrinsicHeight
            )
        }
    }) {
        this.centerVertical = centerVertical
    }

    constructor(context: Context, @DrawableRes drawableRes: Int, centerVertical: Boolean) : super(
        context,
        drawableRes
    ) {
        this.centerVertical = centerVertical
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        if (centerVertical) {
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
        if (centerVertical) {
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
