package com.april.develop.dialog

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.annotation.CallSuper
import androidx.annotation.FloatRange
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class SupportDialogFragment : DialogFragment() {

    @LayoutRes
    protected abstract fun supportDialogLayoutRes(): Int

    /**
     * 默认背景是否透明（透明之后才能看清自己给布局设置的背景）
     */
    protected open fun supportDialogTransparent(): Boolean {
        return false
    }

    /**
     *  窗口透明度（黑色遮罩的阴暗程度，系统默认的在 0.7 左右）
     */
    @FloatRange(from = 0.0, to = 1.0)
    protected open fun supportDialogWindowDarkFrameAlpha(): Float {
        return 0.6f
    }

    /**
     * 窗口位置
     */
    protected open fun supportDialogGravity(): Int {
        return Gravity.CENTER
    }

    /**
     * Dialog 显示宽度
     */
    protected open fun supportDialogWidth(windowWidthPixels: Int): Int {
        return (windowWidthPixels * 0.8f).toInt()
    }

    /**
     * Dialog 显示高度
     */
    protected open fun supportDialogHeight(windowHeightPixels: Int): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //弹窗时，不要标题
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(supportDialogLayoutRes(), container, false)
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.apply {
            setGravity(supportDialogGravity())
            //背景透明
            if (supportDialogTransparent()) {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            setDimAmount(supportDialogWindowDarkFrameAlpha())
            //宽高
            DisplayMetrics().also {
                windowManager.defaultDisplay.getMetrics(it)
            }.let {
                setLayout(
                    supportDialogWidth(it.widthPixels),
                    supportDialogHeight(it.heightPixels)
                )
            }
        }
    }

}

abstract class SupportBottomSheetDialogFragment : BottomSheetDialogFragment() {

    @LayoutRes
    protected abstract fun supportDialogLayoutRes(): Int

    /**
     * 默认背景是否透明（透明之后才能看清自己给布局设置的背景）
     */
    protected open fun supportDialogTransparent(): Boolean {
        return false
    }

    /**
     *  窗口透明度（黑色遮罩的阴暗程度，系统默认的在 0.7 左右）
     */
    @FloatRange(from = 0.0, to = 1.0)
    protected open fun supportDialogWindowDarkFrameAlpha(): Float {
        return 0.6f
    }

    /**
     * 展开时，固定死的高度，此时不允许滑动
     *
     * 返回 null 表示不固定高度
     */
    protected open fun supportDialogFixedHeight(windowHeightPixels: Int): Int? {
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
        return inflater.inflate(supportDialogLayoutRes(), container, false)
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
        dialog?.window?.also { window ->
            //窗口阴影
            window.setDimAmount(supportDialogWindowDarkFrameAlpha())
            (view?.parent as? ViewGroup)?.let { parent ->
                //背景透明
                if (supportDialogTransparent()) {
                    parent.setBackgroundResource(android.R.color.transparent)
                }
                //设置高度
                val fixedHeight = Point().let { point ->
                    window.windowManager.defaultDisplay.getSize(point)
                    supportDialogFixedHeight(point.y)
                }
                //固定死高度
                if (fixedHeight != null) {
                    parent.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    parent.post {
                        mBottomSheetBehavior?.let { behavior ->
                            behavior.removeBottomSheetCallback(mBottomSheetBehaviorCallback)
                            behavior.addBottomSheetCallback(mBottomSheetBehaviorCallback)
                            behavior.peekHeight = fixedHeight
                        }
                    }
                }
            }
        }
    }

}