package com.april.text.dsl

import android.graphics.Typeface

/**
 * 文字样式
 */
enum class TextStyle(val value: Int) {
    Normal(Typeface.NORMAL),
    Bold(Typeface.BOLD),
    Italic(Typeface.ITALIC),
    BoldItalic(Typeface.BOLD_ITALIC)
}