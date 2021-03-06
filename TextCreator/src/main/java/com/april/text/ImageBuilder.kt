package com.april.text

import android.text.Spanned
import android.widget.TextView
import androidx.annotation.DrawableRes

/**
 * 构建资源图片
 */
class ImageBuilder internal constructor() {

    /**
     * 图片资源
     */
    @DrawableRes
    var value: Int = 0

    /**
     * 图片相对于文字垂直居中（否则 底部对齐）
     */
    var centerVertical: Boolean = true
    /**
     * 该图片的点击事件
     */
    var onClick: ((view: TextView, imageResource: Int) -> Unit)? = null

    /**
     * 用于占位的空白文字
     */
    internal val placementTextValue: String = " "

    internal val spanFlag: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE

}