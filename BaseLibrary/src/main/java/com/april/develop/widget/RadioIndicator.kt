package com.april.develop.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Size
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes

/**
 * RadioGroup 实现的 指示器
 */
class RadioIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    //选中的位置（没有指示器时，为 -1）
    private var mCheckedIndex = -1
    //指示器大小
    private var mIndicatorSizePX: Size = Size(
        dp2px(8),
        dp2px(8)
    )
    //指示器未选中时的大小
    private var mIndicatorUncheckedSizePX: Size = mIndicatorSizePX
    //指示器之间的距离
    private var mIndicatorBetweenMarginPX: Int = dp2px(8)
    //指示器的样式
    @DrawableRes
    private var mIndicatorDrawableRes: Int = 0
    //指示器未选中时的样式
    @DrawableRes
    private var mIndicatorUncheckedDrawableRes: Int = 0

    init {
        gravity = Gravity.CENTER
    }

    /**
     * 选中某个位置
     */
    fun checkIndex(index: Int) {
        val lastCheckedIndex = mCheckedIndex
        if (lastCheckedIndex != -1) {
            getChildAt(lastCheckedIndex)?.let { child ->
                child.setBackgroundResource(mIndicatorUncheckedDrawableRes)
                child.layoutParams = child.layoutParams.also { params ->
                    params.width = mIndicatorUncheckedSizePX.width
                    params.height = mIndicatorUncheckedSizePX.height
                }
            }
        }
        getChildAt(index)?.let { child ->
            child.setBackgroundResource(mIndicatorDrawableRes)
            child.layoutParams = child.layoutParams.also { params ->
                params.width = mIndicatorSizePX.width
                params.height = mIndicatorSizePX.height
            }
        }
        mCheckedIndex = index
    }

    /**
     * 获取当前选中的位置
     *
     * 没有指示器时，返回 -1
     */
    fun getCheckedIndex(): Int = mCheckedIndex

    /**
     * 指示器中间的间距
     *
     * 默认 8 dp
     */
    fun setMarginBetween(marginDP: Int) {
        mIndicatorBetweenMarginPX = dp2px(marginDP)
    }

    /**
     * 设置指示器的大小（支持设置 自适应 或者 撑满）
     *
     * 默认大小为 8dp，默认未选中大小和选中时一样
     */
    fun setIndicatorSize(checkedSizeDP: Size, uncheckedSizeDP: Size = checkedSizeDP) {
        mIndicatorSizePX = checkedSizeDP.dp2px(context)
        mIndicatorUncheckedSizePX = uncheckedSizeDP.dp2px(context)
    }

    /**
     * 设置指示器样式
     */
    fun setIndicatorStyle(
        @DrawableRes checkedDrawableRes: Int,
        @DrawableRes uncheckedDrawableRes: Int
    ) {
        mIndicatorDrawableRes = checkedDrawableRes
        mIndicatorUncheckedDrawableRes = uncheckedDrawableRes
    }

    /**
     * 设置总个数
     */
    fun setTotalCount(totalCount: Int, defaultCheckedIndex: Int = 0) {
        removeAllViews()
        for (index in 0 until totalCount) {
            //添加 View
            addView(ImageView(context).also { image ->
                //设置背景资源
                image.setBackgroundResource(mIndicatorUncheckedDrawableRes)
            }, LayoutParams(
                mIndicatorUncheckedSizePX.width,
                mIndicatorUncheckedSizePX.height
            ).also { params ->
                //不是最后一个
                if (index != totalCount - 1) {
                    if (orientation == HORIZONTAL) {
                        params.rightMargin = mIndicatorBetweenMarginPX
                    } else if (orientation == VERTICAL) {
                        params.bottomMargin = mIndicatorBetweenMarginPX
                    }
                }
            })
        }
        checkIndex(defaultCheckedIndex)
    }

    /**
     * dp 转 px
     */
    private fun dp2px(dp: Int): Int {
        return (dp * (resources.displayMetrics.density) + 0.5f).toInt()
    }

}

private fun Size.dp2px(context: Context): Size {
    val seed = context.resources.displayMetrics.density
    return Size(
        width.let {
            if (it > 0) {
                (it * seed + 0.5f).toInt()
            } else {
                it
            }
        },
        height.let {
            if (it > 0) {
                (it * seed + 0.5f).toInt()
            } else {
                it
            }
        }
    )
}