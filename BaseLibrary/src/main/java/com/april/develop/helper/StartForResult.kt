package com.april.develop.helper

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * 更方便地使用 startActivityForResult
 */
fun FragmentActivity.startForResult(
    intent: Intent,
    requestCode: Int,
    TAG: String = StartForResult::class.java.name,
    callBack: ((requestCode: Int, resultCode: Int, data: Intent?) -> Unit)? = null
) {
    StartForResult.getInstance(supportFragmentManager, TAG).startForResult(
        intent, requestCode, object : StartForResultCallBack {
            override fun onStartForResult(requestCode: Int, resultCode: Int, data: Intent?) {
                callBack?.invoke(requestCode, resultCode, data)
            }
        }
    )
}

/**
 * 更方便地使用 startActivityForResult
 */
fun Fragment.startForResult(
    intent: Intent,
    requestCode: Int,
    TAG: String = StartForResult::class.java.name,
    callBack: ((requestCode: Int, resultCode: Int, data: Intent?) -> Unit)? = null
) {
    StartForResult.getInstance(childFragmentManager, TAG).startForResult(
        intent, requestCode, object : StartForResultCallBack {
            override fun onStartForResult(requestCode: Int, resultCode: Int, data: Intent?) {
                callBack?.invoke(requestCode, resultCode, data)
            }
        }
    )
}

interface StartForResultCallBack {
    fun onStartForResult(requestCode: Int, resultCode: Int, data: Intent?)
}

class StartForResult : Fragment() {
    companion object {
        fun getInstance(
            manager: FragmentManager,
            TAG: String = StartForResult::class.java.name
        ): StartForResult {
            var fragment = manager.findFragmentByTag(TAG)
            return if (fragment == null) {
                fragment = StartForResult()
                manager
                    .beginTransaction()
                    .add(fragment, TAG)
                    .commit()
                manager.executePendingTransactions()
                fragment
            } else {
                fragment as StartForResult
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    private var callBack: StartForResultCallBack? = null

    fun startForResult(
        intent: Intent,
        requestCode: Int,
        callBack: StartForResultCallBack?
    ) {
        this.callBack = callBack
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callBack?.onStartForResult(requestCode, resultCode, data)
    }

}