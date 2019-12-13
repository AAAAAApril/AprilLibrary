package com.april.text

import android.graphics.drawable.Drawable
import android.text.Spanned
import android.widget.TextView

/**
 * 构建 Drawable
 */
class DrawableBuilder internal constructor() {

    /**
     * 图片 Drawable
     */
    var value: Drawable? = null

    /**
     * 图片相对于文字垂直居中（否则 底部对齐）
     */
    var centerVertical: Boolean = true
    /**
     * 该图片的点击事件
     */
    var onClick: ((view: TextView) -> Unit)? = null

    /**
     * 用于占位的空白文字
     */
    internal val placementTextValue: String = " "

    internal val spanFlag: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE

}