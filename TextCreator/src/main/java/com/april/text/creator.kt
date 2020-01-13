package com.april.text

import android.content.Context
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt


/*

        textView.spannableText {
            text {
                value = "你"
                textColor = getColorInt(R.color.colorAccent)
                onClick = { view, text ->
                    //do something
                }
            }
            line(1)
            image {
                drawableValue = getDrawable(R.drawable.icon_money_round_yellow)
                centerVertical = true
            }
            line(1)
            text {
                value = "好"
                textColor = getColorInt(R.color.colorPrimary)
            }
            line(2)
            image {
                resourceValue = R.drawable.icon_money_round_green
                centerVertical = false
                onClick = {
                    //do something
                }
            }
            line()
            text {
                value = "呀"
                textColor = getColorInt(R.color.colorPrimaryDark)
            }
        }

 */

fun TextView.spannableText(
    //是否是设置给 hint
    forHint: Boolean = false,
    //是否是增加文字模式
    appendMode: Boolean = false,
    //点击时的高亮背景
    @ColorInt highLightColor: Int = Color.TRANSPARENT,
    block: SpannableTextCreator.() -> Unit
): TextView {
    val creator = context.spannableTextCreator(block)
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

fun Context.spannableTextCreator(block: (SpannableTextCreator.() -> Unit)? = null): SpannableTextCreator {
    return SpannableTextCreator(this).also { creator ->
        if (block != null) {
            creator.apply(block)
        }
    }
}