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
    protected open fun setStyleAndTheme(): Array<Int> {
        //弹窗时，调整宽度为默认。否则可能宽度过窄。
        return arrayOf(
            STYLE_NO_TITLE,
            android.R.style.Theme_Material_Dialog_MinWidth
        )
    }

    /**
     *  默认的背景是否透明
     */
    protected open fun defaultBackgroundTransparent(): Boolean {
        return true
    }

    /**
     * 宽度占窗口宽度的百分比例
     *
     * 返回 null 表示自适应
     */
    protected open fun widthPercent(): Float? {
        return 0.8f
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(setStyleAndTheme()[0], setStyleAndTheme()[1])
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
        //背景透明（这个好像没有效果...）
        if (defaultBackgroundTransparent()) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        //宽度占比
        dialog?.window?.apply {
            val dm = DisplayMetrics()
            windowManager?.defaultDisplay?.getMetrics(dm)
            setLayout(
                if (widthPercent() != null) {
                    (dm.widthPixels * widthPercent()!!).toInt()
                } else {
                    ViewGroup.LayoutParams.WRAP_CONTENT
                },
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

}

abstract class SupportBottomSheetDialogFragment : BottomSheetDialogFragment() {

    @LayoutRes
    abstract fun setDialogLayoutRes(): Int

    abstract override fun onViewCreated(view: View, savedInstanceState: Bundle?)

    /**
     *  默认的背景是否透明
     */
    protected open fun defaultBackgroundTransparent(): Boolean {
        return true
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
                if (defaultBackgroundTransparent()) {
                    rootView.setBackgroundResource(android.R.color.transparent)
                }
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