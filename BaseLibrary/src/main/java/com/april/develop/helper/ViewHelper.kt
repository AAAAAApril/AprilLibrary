package com.april.develop.helper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 * 本文件扩展函数汇总
 *
 * [gone] View gone
 * [visible] View visible
 * [invisible] View invisible
 * [backgroundColorRes] View 背景色 资源色
 * [backgroundTintListValueOfColorRes] View backgroundTintList
 *
 * [trimString] TextView 去掉末尾空格的字符串
 * [halfBold] TextView 文字半粗，介于 normal 和 bold 之间
 * [textColorRes] TextView 文字颜色 资源文件
 * [drawables] TextView drawable start top end bottom
 * [copyText] TextView 复制文字
 *
 * [search] EditText 软键盘搜索功能
 * [send] EditText 软键盘发送功能
 * [showPassword] TextView 显示与隐藏密码
 *
 * [showSoftInput] View 显示与隐藏软键盘
 * [softInputShowing] View 软键盘是否显示
 */

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.backgroundColorRes(@ColorRes colorRes: Int) {
    setBackgroundColor(ContextCompat.getColor(context, colorRes))
}

fun View.backgroundTintListValueOfColorRes(@ColorRes colorRes: Int) {
    backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
}

fun TextView.trimString(): String {
    return text.toString().trim()
}

/**
 * 一半粗，介于 normal 和 bold 之间
 */
fun TextView.halfBold(halfBold: Boolean = true) {
    paint.isFakeBoldText = (halfBold)
    postInvalidate()
}

fun TextView.textColorRes(@ColorRes colorRes: Int) {
    setTextColor(ContextCompat.getColor(context, colorRes))
}

/**
 * 设置上下左右的图标
 */
fun TextView.drawables(
    start: Int = 0,
    top: Int = 0,
    end: Int = 0,
    bottom: Int = 0
) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        start, top, end, bottom
    )
}

/**
 * 设置上下左右的图标
 */
fun TextView.drawables(
    start: Drawable? = null,
    top: Drawable? = null,
    end: Drawable? = null,
    bottom: Drawable? = null
) {
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        start, top, end, bottom
    )
}

/**
 * 复制 TextView 上面的文字
 * [Boolean] 是否成功
 */
fun TextView.copyText(): Boolean {
    val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    return if (manager != null) {
        manager.primaryClip = ClipData.newPlainText("Label", text)
        true
    } else {
        false
    }
}

/**
 * 软键盘回车变成搜索
 */
fun EditText.search(block: (String) -> Unit) {
    imeOptions = EditorInfo.IME_ACTION_SEARCH
    setSingleLine()
    setOnEditorActionListener { v, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            showSoftInput(false)
            block(v.text.toString())
        }
        return@setOnEditorActionListener false
    }
}

/**
 * 软键盘回车变成发送
 */
fun EditText.send(block: (String) -> Unit) {
    imeOptions = EditorInfo.IME_ACTION_SEND
    setSingleLine()
    setOnEditorActionListener { v, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            showSoftInput(false)
            block(v.text.toString())
        }
        return@setOnEditorActionListener false
    }
}

/**
 * 显示与隐藏密码
 *
 * [show] 是否显示密码，否则隐藏
 * [cursorToEnd] 光标是否移动至末尾
 */
fun TextView.showPassword(
    show: Boolean,
    cursorToEnd: Boolean = true
) {
    if (show) {
        transformationMethod = HideReturnsTransformationMethod.getInstance()
    } else {
        transformationMethod = PasswordTransformationMethod.getInstance()
    }
    if (cursorToEnd && this is EditText) {
        setSelection(text.length)
    }
}

/**
 * 操作软键盘
 *
 * [show] 是否显示，否则隐藏
 */
fun View.showSoftInput(show: Boolean = true) {
    val obj = context.getSystemService(Context.INPUT_METHOD_SERVICE)
    obj?.also { o ->
        (o as InputMethodManager).let { manager ->
            if (show) {
                requestFocus()
                manager.showSoftInput(this, 0)
                if (this is EditText) {
                    setSelection(text.length)
                }
            } else {
                clearFocus()
                manager.hideSoftInputFromWindow(windowToken, 0)
            }
            return@let
        }
    }
}

/**
 * 判断软键盘
 *
 * [Boolean] 是否正在显示
 */
fun View.softInputShowing(): Boolean {
    val manager: InputMethodManager? =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return manager != null && manager.isActive
}
