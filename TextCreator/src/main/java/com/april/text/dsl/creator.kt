package com.april.text.dsl

import android.content.Context
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt

fun TextView.spannableText(
    //是否是设置给 hint
    forHint: Boolean = false,
    //是否是增加文字模式
    appendMode: Boolean = false,
    //点击时的高亮背景
    @ColorInt highLightColor: Int = Color.TRANSPARENT,
    block: RichTextCreator.() -> Unit
): TextView {
    val creator = context.richTextCreator(block)
    if (creator.hasClick && (this !is EditText)) {
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = highLightColor
    }
    if (forHint) {
        hint = creator.builder
    } else {
        if (appendMode) {
            append(creator.builder)
        } else {
            text = creator.builder
        }
    }
    return this
}

fun Context.richTextCreator(block: RichTextCreator.() -> Unit): RichTextCreator {
    return RichTextCreator(this).apply(block)
}