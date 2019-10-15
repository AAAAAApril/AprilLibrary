package com.april.develop.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.april.develop.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


abstract class SupportDialogFragment : DialogFragment() {

    @LayoutRes
    abstract fun setDialogLayoutRes(): Int

    /**
     *  默认的背景是否透明
     */
    protected open fun defaultBackgroundTransparent(): Boolean {
        return true
    }

    /**
     * 宽度占屏幕的比例
     */
    protected open fun widthPercent(): Float? {
        return 0.8f
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //弹窗时，调整宽度为默认。否则可能宽度过窄。
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Dialog_MinWidth)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //弹窗时，不要标题
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(setDialogLayoutRes(), container, false)
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //背景透明
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

    /**
     *  默认的背景是否透明
     */
    protected open fun defaultBackgroundTransparent(): Boolean {
        return true
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //弹窗时，不要标题
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(setDialogLayoutRes(), container, false)
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //背景透明
        if (defaultBackgroundTransparent()) {
            dialog
                ?.window
                ?.findViewById<View>(R.id.design_bottom_sheet)
                ?.setBackgroundResource(android.R.color.transparent)
        }
    }

}