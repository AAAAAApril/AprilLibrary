package com.april.text

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes

/**
 * 图片样式
 */
class ImageCreator(
    //图片资源文件
    @DrawableRes
    val imageRes: Int? = null,
    //图片 Drawable
    val imageDrawable: Drawable? = null,
    //图片是否垂直居中于文字，否则为底部对齐
    val centerVertical: Boolean = true,
    //点击事件
    onClick: ((View) -> Unit)? = null
//图片样式为占位类型，所以需要给个字符用来占位替换，这里用了空白字符 " "
) : TextCreator(
    text = " ", onClick = if (onClick == null) {
        null
    } else {
        object : TextCreatorClick {
            override fun invoke(view: View, p2: String) {
                onClick.invoke(view)
            }
        }
    }
)