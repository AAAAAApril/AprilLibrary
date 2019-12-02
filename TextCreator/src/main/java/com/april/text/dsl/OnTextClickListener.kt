package com.april.text.dsl

import android.widget.TextView

/**
 * 文字点击事件监听
 */
interface OnTextClickListener {
    fun onTextClick(view: TextView, text: String)
}