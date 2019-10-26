package com.april.text

import android.view.View
import androidx.annotation.DrawableRes


/**
 * 图片样式
 */
class ImageCreator(
    //图片资源文件
    @DrawableRes
    val image: Int,
    //图片是否垂直居中于文字，否则为底部对齐
    val vertical: Boolean = false,
    //点击事件
    onClick: ((View) -> Unit)? = null
//图片样式为占位类型，所以需要给个字符用来占位替换，这里用了空白字符 " "
) : TextCreator(text = " ", onClick = onClick)
