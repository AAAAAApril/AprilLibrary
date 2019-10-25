package com.april.develop.helper

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment

inline fun <reified A : Activity> Activity.simpleStartActivity(TAG: String = "") {
    startActivity(Intent(this, A::class.java))
}

inline fun <reified A : Activity> Fragment.simpleStartActivity(TAG: String = "") {
    if (activity == null) {
        return
    }
    startActivity(Intent(requireActivity(), A::class.java))
}