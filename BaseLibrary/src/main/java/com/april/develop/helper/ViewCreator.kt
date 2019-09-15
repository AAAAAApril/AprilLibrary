package com.april.develop.helper

import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

inline fun ViewGroup.createTextView(block: TextView.() -> Unit): TextView {
    val view = TextView(context)
    block(view)
    return view
}

inline fun ViewGroup.createEditText(block: EditText.() -> Unit):EditText{
    val view = EditText(context)
    block(view)
    return view
}

inline fun ViewGroup.createButton(block: Button.() -> Unit): Button {
    val view = Button(context)
    block(view)
    return view
}

inline fun ViewGroup.createImageView(block: ImageView.() -> Unit): ImageView {
    val view = ImageView(context)
    block(view)
    return view
}
