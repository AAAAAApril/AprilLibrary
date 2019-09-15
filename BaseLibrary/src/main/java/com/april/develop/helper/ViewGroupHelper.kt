package com.april.develop.helper

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout

fun LinearLayout.addChild(child: View,
                          index: Int = -1,
                          width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                          height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                          weight: Float = 0f,
                          gravity: Int = -1,
                          marginDP: Int? = null,
                          marginStartDP: Int = 0,
                          marginTopDP: Int = 0,
                          marginEndDP: Int = 0,
                          marginBottomDP: Int = 0
): LinearLayout {
    addView(child, index, LinearLayout.LayoutParams(width, height).apply {
        this.weight = weight
        this.gravity = gravity
        this.marginStart = dp2px(marginDP, marginStartDP)
        this.leftMargin = dp2px(marginDP, marginStartDP)
        this.topMargin = dp2px(marginDP, marginTopDP)
        this.marginEnd = dp2px(marginDP, marginEndDP)
        this.rightMargin = dp2px(marginDP, marginEndDP)
        this.bottomMargin = dp2px(marginDP, marginBottomDP)
    })
    return this
}

fun FrameLayout.addChild(child: View,
                         index: Int = -1,
                         width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                         height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                         gravity: Int = -1,
                         marginDP: Int? = null,
                         marginStartDP: Int = 0,
                         marginTopDP: Int = 0,
                         marginEndDP: Int = 0,
                         marginBottomDP: Int = 0
): FrameLayout {
    addView(child, index, FrameLayout.LayoutParams(width, height, gravity).apply {
        this.marginStart = dp2px(marginDP, marginStartDP)
        this.leftMargin = dp2px(marginDP, marginStartDP)
        this.topMargin = dp2px(marginDP, marginTopDP)
        this.marginEnd = dp2px(marginDP, marginEndDP)
        this.rightMargin = dp2px(marginDP, marginEndDP)
        this.bottomMargin = dp2px(marginDP, marginBottomDP)
    })
    return this
}

fun Context.createLinearLayout(vertical: Boolean = true,
                               childrenGravity: Int = Gravity.START or Gravity.TOP): LinearLayout {
    return LinearLayout(this).apply {
        this.orientation = if (vertical) {
            LinearLayout.VERTICAL
        } else {
            LinearLayout.HORIZONTAL
        }
        this.gravity = childrenGravity
    }
}

private fun View.dp2px(dp: Int?, dpDefault: Int): Int {
    return ((dp ?: dpDefault) * (resources.displayMetrics.density) + 0.5f).toInt()
}