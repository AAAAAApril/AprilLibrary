package com.april.text

import android.graphics.Typeface
import android.text.style.DynamicDrawableSpan
import androidx.annotation.IntDef
import androidx.annotation.StringDef


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