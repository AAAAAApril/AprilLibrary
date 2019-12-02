package com.april.text.dsl

import android.graphics.drawable.Drawable
import android.text.Spanned
import androidx.annotation.DrawableRes

/**
 * 构建图片
 */
class ImageBuilder internal constructor() {
    /**
     * 图片资源
     */
    @DrawableRes
    var resourceValue: Int? = null
    /**
     * 图片 Drawable
     */
    var drawableValue: Drawable? = null
    /**
     * 图片相对于文字垂直居中（否则 底部对齐）
     */
    var centerVertical: Boolean = true
    /**
     * 该图片的点击事件
     */
    var onClick: OnImageClickListener? = null

    /**
     * 用于占位的空白文字
     */
    internal val placementTextValue: String = " "

    internal val spanFlag: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
}