package com.april.text

import androidx.annotation.IntRange


/**
 * 换行
 *
 * [lineCount] 表示空白行的数量（默认只是换行，不增加空白行）
 */
class LineCreator(@IntRange(from = 0) lineCount: Int = 0) : TextCreator(text = (lineCount + 1).let {
    StringBuilder().apply {
        for (i in 0 until it) {
            append("\n")
        }
    }.toString()
})