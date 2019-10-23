package com.april.develop.helper

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Context.toast(message: CharSequence?, longToast: Boolean = false) {
    show(this, message, longToast)
}

fun Fragment.toast(message: CharSequence?, longToast: Boolean = false) {
    activity?.toast(message, longToast)
}

/**
 * 避免重复弹出的 Toast
 */
private var toast: Toast? = null

internal fun show(context: Context, message: CharSequence?, longToast: Boolean = false) {
    if (TextUtils.isEmpty(message)) {
        return
    }
    toast?.cancel()
    toast = Toast.makeText(
        context, "", if (longToast) {
            Toast.LENGTH_LONG
        } else {
            Toast.LENGTH_SHORT
        }
    )
    toast?.setText(message)
    toast?.show()
}