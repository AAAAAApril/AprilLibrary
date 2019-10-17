package com.april.develop.dialog

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.CallSuper
import androidx.annotation.FloatRange
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import com.april.develop.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class SupportDialogFragment : DialogFragment() {

    @LayoutRes
    abstract fun setDialogLayoutRes(): Int

    abstract override fun onViewCreated(view: View, savedInstanceState: Bundle?)

    /**
     * 设置样式和主题
     */
    protected open fun setStyleAndTheme(): Array<Int?> {
        //弹窗时，调整宽度为默认。否则可能宽度过窄。
        return arrayOf(
            STYLE_NO_TITLE,
            android.R.style.Theme_Material_Dialog_MinWidth
        )
    }

    /**
     *  窗口透明度（黑色遮罩的阴暗程度，系统默认的在 0.7 左右）
     */
    @FloatRange(from = 0.0, to = 1.0)
    protected open fun windowDarkFrameAlpha(): Float {
        return 0.6f
    }

    /**
     * 宽度、高度占窗口宽度、高度的百分比例
     *
     * 返回 null 表示自适应
     */
    protected open fun widthAndHeightPercent(): Array<Float?> {
        return arrayOf(0.8f, null)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val style = setStyleAndTheme()[0]
        val theme = setStyleAndTheme()[1]
        if (style != null && theme != null) {
            setStyle(style, theme)
        }
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //弹窗时，不要标题
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(setDialogLayoutRes(), container, false)
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.window?.apply {
            //背景透明
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(windowDarkFrameAlpha())
            //宽度占比
            val dm = DisplayMetrics()
            windowManager?.defaultDisplay?.getMetrics(dm)
            //宽高比数据
            val widthAndHeight = widthAndHeightPercent()
            setLayout(
                //宽度
                if (widthAndHeight[0] != null) {
                    (dm.widthPixels * widthAndHeight[0]!!).toInt()
                } else {
                    ViewGroup.LayoutParams.WRAP_CONTENT
                },
                //高度
                if (widthAndHeight[1] != null) {
                    (dm.heightPixels * widthAndHeight[1]!!).toInt()
                } else {
                    ViewGroup.LayoutParams.WRAP_CONTENT
                }
            )
        }
    }

}

abstract class SupportBottomSheetDialogFragment : BottomSheetDialogFragment() {

    @LayoutRes
    abstract fun setDialogLayoutRes(): Int

    abstract override fun onViewCreated(view: View, savedInstanceState: Bundle?)

    /**
     *  窗口透明度（黑色遮罩的阴暗程度，系统默认的在 0.7 左右）
     */
    @FloatRange(from = 0.0, to = 1.0)
    protected open fun windowDarkFrameAlpha(): Float {
        return 0.6f
    }

    /**
     * 展开时，固定死的高度占整个窗口高度的百分比，此时不允许滑动
     *
     * 返回 null 表示不固定高度
     */
    protected open fun fixedHeightPercent(): Float? {
        return null
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //弹窗时，不要标题
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(setDialogLayoutRes(), container, false)
    }

    private val mBottomSheetBehavior: BottomSheetBehavior<View>? by lazy {
        (((view?.parent as? View)?.layoutParams as? CoordinatorLayout.LayoutParams)?.behavior) as? BottomSheetBehavior<View>?
    }
    private val mBottomSheetBehaviorCallback by lazy {
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                //禁止拖拽，
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    //设置为收缩状态
                    mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
        }
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.let { window ->
            window.findViewById<View>(R.id.design_bottom_sheet)?.let { rootView ->
                //背景透明
                rootView.setBackgroundResource(android.R.color.transparent)
                window.setDimAmount(windowDarkFrameAlpha())
                //固定死高度
                if (fixedHeightPercent() != null) {
                    rootView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    rootView.post {
                        mBottomSheetBehavior?.setBottomSheetCallback(mBottomSheetBehaviorCallback)
                        //设置高度
                        val point = Point()
                        window.windowManager.defaultDisplay.getSize(point)
                        mBottomSheetBehavior?.peekHeight =
                            ((point.y) * fixedHeightPercent()!!).toInt()
                    }
                }
            }
        }
    }

}